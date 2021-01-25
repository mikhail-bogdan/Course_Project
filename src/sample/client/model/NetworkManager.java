package sample.client.model;

import java.io.IOException;
import java.net.Inet4Address;
import java.net.Socket;
import java.net.UnknownHostException;

public class NetworkManager {
    private static boolean isOffline = true;
    private static Inet4Address serverAddress;
    private static final int port = 22000;

    public static void Init() {
        isOffline = false;

        try {
            serverAddress = (Inet4Address) Inet4Address.getByName("localhost");
        } catch (UnknownHostException e) {
            isOffline = true;
        }

        try {
            isOffline = !serverAddress.isReachable(1000);
        } catch (IOException e) {
            isOffline = true;
        }

        try {
            new Socket(serverAddress, port).close();
        } catch (IOException e) {
            isOffline = true;
        }
    }

    public static boolean IsOffline() {
        return isOffline;
    }

    public static MusicSocket GetMusicSocket() {
        try {
            return new MusicSocket(new Socket(serverAddress, port));
        } catch (IOException e) {
            isOffline = true;
            return null;
        }
    }
}
