package sample.client.model;

import java.io.FileInputStream;
import java.io.IOException;

public class ClipGetterThread extends Thread {
    private boolean active;
    private int clipID;
    private boolean isClipSaved;
    OnDataReceivedCallback callback;

    public ClipGetterThread(int clipID, boolean isClipSaved, OnDataReceivedCallback callback) {
        this.clipID = clipID;
        this.active = true;
        this.isClipSaved = isClipSaved;
        this.callback = callback;
    }

    @Override
    public void run() {
        if (NetworkManager.IsOffline()) {
            if (isClipSaved) {
                try {
                    FileInputStream stream = new FileInputStream("./data/saved/" + clipID + ".wav");
                    byte[] data = new byte[4096];
                    while(stream.available() > 0) {
                        int len = stream.read(data);
                        if (len < 4096) {
                            byte[] tmp_data = new byte[len];
                            System.arraycopy(data, 0, tmp_data, 0, len);
                            if (callback != null) callback.OnDataReceived(tmp_data);
                        }
                        if (callback != null) callback.OnDataReceived(data);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else {
                if (callback != null) callback.OnDataReceived(null);
            }
        } else {
            MusicSocket socket = NetworkManager.GetMusicSocket();
            long clipSize = socket.GetClipSize(clipID);
            long index = 0;
            while (true) {
                while (clipID < 0) Thread.onSpinWait();

                //NetworkManager manager
            }
        }
    }



    public interface OnDataReceivedCallback {
        void OnDataReceived(byte[] data);
    }
}
