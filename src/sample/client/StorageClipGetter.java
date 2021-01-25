package sample.client;

import sample.shared.ByteConverter;

import javax.sound.sampled.AudioFormat;
import java.io.FileInputStream;
import java.io.IOException;

public class StorageClipGetter implements Getter {
    private GetterThread thread;
    private ClipGetter clipGetter;
    private volatile boolean isActive;
    private volatile int clipID;
    private long clipSize;
    private AudioFormat format;

    public StorageClipGetter(int clipID, ClipGetter clipGetter) {
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
            try {
                FileInputStream stream = new FileInputStream("./data/saved/" + clipID + ".wav");
                boolean isDataFound = false;
                byte[] buffer = new byte[4096];
                while(!isDataFound) {
                    stream.read(buffer, 0, 8);
                    if (buffer[0] == 'R' && buffer[1] == 'I' && buffer[2] == 'F' && buffer[3] == 'F') {
                        stream.read(buffer, 0, 4);
                    } else if (buffer[0] == 'd' && buffer[1] == 'a' && buffer[2] == 't' && buffer[3] == 'a') {
                        isDataFound = true;
                        clipSize = ByteConverter.bytesToInt(buffer, 4);
                        clipGetter.setTotalSize(clipSize);
                    } else if (buffer[0] == 'f' && buffer[1] == 'm' && buffer[2] == 't' && buffer[3] == ' ') {
                        stream.read(buffer, 0, ByteConverter.bytesToInt(buffer, 4));
                        short nChannels = ByteConverter.bytesToShort(buffer, 2);
                        int samplesPerSec = ByteConverter.bytesToInt(buffer, 4);
                        short sampleSizeInBits = ByteConverter.bytesToShort(buffer, 14);
                        format = new AudioFormat(samplesPerSec, sampleSizeInBits, nChannels, true, false);
                    } else {
                        stream.read(buffer, 0, ByteConverter.bytesToInt(buffer, 4));
                    }
                }

                while (stream.available() > 0) {
                    int len = stream.read(buffer, 0, 4096);
                    if(isActive)
                        clipGetter.push(buffer, len);
                    else
                        break;
                }

                stream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
