package sample.client;

import javax.sound.sampled.*;
import java.io.IOException;

public class MusicPlayer extends Thread {
    private volatile long framePos = 0;
    private volatile long oldFramePos = 0;
    private OnProgressChangeEvent onProgressChangeEvent;
    private OnStateChangedEvent onStateChangeEvent;
    private OnNextPrevChangedEvent onNextPrevChangedEvent;
    private volatile SourceDataLine dataLine;
    private AudioFormat format;
    private ClipGetter clipGetter;
    private Getter getter;
    private volatile boolean isPlaying;
    private FloatControl volumeControl;
    private MusicPlaylist currentPlaylist;
    private int indexInPlaylist;
    private boolean isCyclicPlaylist;
    private boolean isDestroyed = false;

    public MusicPlayer() {
        indexInPlaylist = 0;
        isCyclicPlaylist = false;

        this.clipGetter = new ClipGetter();


        try {
            this.format = new AudioFormat(44100, 16, 2, true, false);

            DataLine.Info dataLineInfo = new DataLine.Info(SourceDataLine.class, format);
            dataLine = (SourceDataLine) AudioSystem.getLine(dataLineInfo);


            dataLine.open(format);
            volumeControl = (FloatControl) dataLine.getControl(FloatControl.Type.MASTER_GAIN);
        } catch (LineUnavailableException e) {
            e.printStackTrace();
        }
    }

    public void SetOnStateChangeCallback(OnStateChangedEvent callback) {
        this.onStateChangeEvent = callback;
    }

    public void SetOnProgressChangeCallback(OnProgressChangeEvent callback) {
        this.onProgressChangeEvent = callback;
    }

    public void SetOnNextPrevChangedCallback(OnNextPrevChangedEvent callback) {
        this.onNextPrevChangedEvent = callback;
    }

    public int GetCurrentAudioID() {
        return currentPlaylist.Get(indexInPlaylist);
    }

    public void SetProgress(double progress) {
        if (isPlaying) {
            reset();
            clipGetter.setFramePos((int) (clipGetter.getTotalFrames() * progress));
            dataLine.start();
        } else {
            reset();
            clipGetter.setFramePos((int) (clipGetter.getTotalFrames() * progress));
        }
        oldFramePos = framePos = (int) (clipGetter.getTotalFrames() * progress);
    }

    public void SetPlaylist(MusicPlaylist playlist, int pos) {
        synchronized (clipGetter) {
            synchronized (dataLine) {
                this.currentPlaylist = playlist;
                this.indexInPlaylist = pos;
                oldFramePos = 0;
                framePos = 0;
                setIndexInPlaylist();
                if (onNextPrevChangedEvent != null)
                    onNextPrevChangedEvent.OnChanged(this);
            }
        }
    }

    public boolean isPlaying() {
        return isPlaying;
    }

    public void pause() {
        dataLine.stop();
        isPlaying = false;
        if (onStateChangeEvent != null)
            onStateChangeEvent.OnStateChanged(MusicPlayerState.Paused);
    }

    public void play() {
        dataLine.start();
        isPlaying = true;
        if (onStateChangeEvent != null)
            onStateChangeEvent.OnStateChanged(MusicPlayerState.Played);
    }

    public void toggle() {
        if (isPlaying) {
            pause();
        } else {
            play();
        }
    }

    public synchronized void reset() {
        dataLine.close();
        try {
            dataLine.open(format);
        } catch (LineUnavailableException e) {
            e.printStackTrace();
        }
        if (isPlaying)
            dataLine.start();
        oldFramePos = 0;
        clipGetter.setPos(0);
    }

    private boolean isNextExists() {
        return indexInPlaylist + 1 < currentPlaylist.Size();
    }

    private boolean isPrevExists() {
        return indexInPlaylist - 1 >= 0;
    }

    private synchronized void setIndexInPlaylist() {
        reset();
        if(getter != null)
            getter.Deactivate();
        clipGetter.discard();
        if(NetworkManager.IsOffline())
            getter = new StorageClipGetter(currentPlaylist.Get(indexInPlaylist), clipGetter);
        else
            getter = new NetworkClipGetter(currentPlaylist.Get(indexInPlaylist), clipGetter);
    }

    public void next() {
        if(isNextExists()) {
            indexInPlaylist++;
            setIndexInPlaylist();
        } else {
            if(isCyclicPlaylist) {
                indexInPlaylist = 0;
                setIndexInPlaylist();
            }
        }
        if (onNextPrevChangedEvent != null)
            onNextPrevChangedEvent.OnChanged(this);
    }

    public void prev() {
        if(framePos / format.getFrameRate() > 3) {
            reset();
        }
        else if(isPrevExists()) {
            indexInPlaylist--;
            setIndexInPlaylist();
        } else {
            if(isCyclicPlaylist) {
                indexInPlaylist = currentPlaylist.Size() - 1;
                setIndexInPlaylist();
            } else {
                reset();
            }
        }
        if (onNextPrevChangedEvent != null)
            onNextPrevChangedEvent.OnChanged(this);
    }

    public void setVolume(double volume) {
        if (volume <= 0) {
            volumeControl.setValue(volumeControl.getMinimum());
            return;
        }
        if (volume > 1) {
            volumeControl.setValue(volumeControl.getMaximum());
        }
        volume = Math.log(volume * 100) / Math.log(100);
        float min = volumeControl.getMinimum();
        float max = volumeControl.getMaximum();
        volumeControl.setValue((float) ((max - min) * volume + min));
    }

    public double getVolume() {
        double v = volumeControl.getValue();
        float min = volumeControl.getMinimum();
        float max = volumeControl.getMaximum();
        v = (v - min) / (max - min);
        return Math.pow(Math.E, v * Math.log(100)) / 100;
    }

    public void Destroy() {
        getter.Deactivate();
        isDestroyed = true;
    }

    @Override
    public void run() {
        try {
            byte[] buffer = new byte[1024];
            while (true) {
                framePos = oldFramePos + dataLine.getLongFramePosition();
                if(onProgressChangeEvent != null && clipGetter.getTotalFrames() > 0)
                    onProgressChangeEvent.OnProgressChange(framePos, clipGetter.getTotalFrames());
                //controller.SetProgress((double) framePos / clipGetter.getTotalFrames());
                if(clipGetter.available() > 0) {
                    if (isPlaying) {
                        int len = clipGetter.read(buffer);
                        if (len > 0 && dataLine.isOpen()) {
                            dataLine.write(buffer, 0, len);
                        }
                    }
                } else if(framePos >= clipGetter.getTotalFrames() && clipGetter.isFullyLoaded() && isPlaying) {
                    if(isNextExists() || isCyclicPlaylist) {
                        next();
                        if(onNextPrevChangedEvent != null)
                            onNextPrevChangedEvent.OnChanged(this);
                    } else {
                        synchronized (dataLine) {
                            dataLine.stop();
                            isPlaying = false;
                        }
                        if (onStateChangeEvent != null)
                            onStateChangeEvent.OnStateChanged(MusicPlayerState.Paused);
                    }
                }
                if (isDestroyed) return;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
