package sample.client;

import javax.sound.sampled.AudioFormat;

public class NetworkClipGetter implements Getter {
    private GetterThread thread;
    private ClipGetter clipGetter;
    private volatile boolean isActive;
    private int clipID;
    private long clipSize;
    private AudioFormat format;

    public NetworkClipGetter(int clipID, ClipGetter clipGetter) {
        this.isActive = true;
        this.clipGetter = clipGetter;
        this.clipID = clipID;
        thread = new GetterThread();
        thread.start();
    }

    public void Deactivate() {
        this.isActive = false;
        synchronized (clipGetter) {
            clipID = -1;
            clipGetter = null;
        }
    }


    private class GetterThread extends Thread {
        @Override
        public void run() {
            MusicSocket socket = NetworkManager.GetMusicSocket();
            long index = 0;
            long size = socket.GetClipSize(clipID);
            clipGetter.setTotalSize(size);

            while (index < size) {
                byte[] data = socket.GetClipData(clipID, index, 1024);
                if (isActive)
                    clipGetter.push(data, data.length);
                else
                    break;
                index += data.length;
            }

            socket.Close();
        }
    }
}
