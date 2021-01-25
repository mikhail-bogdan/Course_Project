package sample.client.model;

import javafx.util.Pair;
import sample.shared.ByteConverter;
import sample.shared.packets.request.PacketGetLoginSaltRequest;
import sample.shared.packets.request.PacketGetUserPlaylistsRequest;
import sample.shared.packets.request.PacketLoginRequest;
import sample.shared.packets.response.PacketGetLoginSaltResponse;
import sample.shared.packets.response.PacketGetUserPlaylistsResponse;
import sample.shared.packets.response.PacketLoginResponse;

import java.io.*;
import java.net.Socket;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.LinkedList;

public class AccountManager {
    private static final String settingsFilename = "accountSessionKey";
    private static boolean isUserLoggedIn;

    private static byte[] sessionKey;
    private static String login = "test";

    public static void Init() {
        isUserLoggedIn = false;
        try {
            if(new File(settingsFilename).exists()) {
                FileInputStream inputStream = new FileInputStream(settingsFilename);
                int i = inputStream.read();
                if (i == 1) {
                    sessionKey = new byte[128];
                    inputStream.read(sessionKey);
                    byte[] buffer = new byte[2];
                    inputStream.read(buffer);
                    short loginSize = ByteConverter.bytesToShort(buffer, 0);
                    StringBuilder builder = new StringBuilder();
                    for(int j = 0; j < loginSize; j++)
                        builder.append((char) inputStream.read());
                    login = builder.toString();
                    isUserLoggedIn = true;
                }
            } else {
                isUserLoggedIn = false;
            }
            //MusicSocket tmp = NetworkManager.GetMusicSocket();
            //Socket socket = tmp.socket;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void Save() {
        try {
            byte[] buffer = new byte[131 + login.length()];
            buffer[0] = (byte) (isUserLoggedIn ? 1 : 0);
            if(isUserLoggedIn) {
                System.arraycopy(sessionKey, 0, buffer, 1, 128);
                System.arraycopy(ByteConverter.shortToBytes((short)login.length()), 0, buffer, 129, 2);
                System.arraycopy(login.getBytes(), 0, buffer, 131, login.length());
            }
            FileOutputStream stream = new FileOutputStream(settingsFilename);
            stream.write(buffer);
            stream.flush();
            stream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static byte[] GetSessionKey() {
        return sessionKey;
    }

    public static boolean IsUserLoggedIn() {
        return isUserLoggedIn;
    }

    public static int LogIn(String login, String password) {
        AccountManager.login = login;
        PacketGetLoginSaltRequest loginSaltRequest = new PacketGetLoginSaltRequest();
        MusicSocket tmp = NetworkManager.GetMusicSocket();
        if(tmp == null) return 100;
        Socket socket = tmp.socket;
        PacketGetLoginSaltResponse loginSaltResponse = new PacketGetLoginSaltResponse();

        try {
            socket.getOutputStream().write(loginSaltRequest.toBytes());
            socket.getOutputStream().flush();
            loginSaltResponse.fromBytes(socket.getInputStream());
        } catch (IOException e) {
            return 100;
        }



        MessageDigest digest = null;
        try {
            digest = MessageDigest.getInstance("SHA-256");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }

        digest.update(password.getBytes());
        byte[] tmp_result = digest.digest();
        byte[] result = new byte[32 * 2];
        System.arraycopy(loginSaltResponse.salt1, 0, result, 0, 16);
        System.arraycopy(tmp_result, 0, result, 16, 32);
        System.arraycopy(loginSaltResponse.salt2, 0, result, 48, 16);
        digest.update(result);

        PacketLoginRequest loginRequest = new PacketLoginRequest();
        loginRequest.login = login;
        loginRequest.passwordHash = digest.digest();
        PacketLoginResponse loginResponse = new PacketLoginResponse();

        try {
            socket.getOutputStream().write(loginRequest.toBytes());
            socket.getOutputStream().flush();
            loginResponse.fromBytes(socket.getInputStream());
        } catch (IOException e) {
            return 100;
        }

        if(loginResponse.error == 0) {
            isUserLoggedIn = true;
            sessionKey = loginResponse.sessionKey;
            //Save();
            return 0;
        }
        isUserLoggedIn = false;
        Save();
        return loginResponse.error;
    }

    public static String getLogin() {
        return login;
    }

    public static Pair<String[],LinkedList<LinkedList<Integer>>> GetUserPlaylists() {
        if (!isUserLoggedIn) return null;

        PacketGetUserPlaylistsRequest request = new PacketGetUserPlaylistsRequest();
        request.sessionKey = sessionKey;

        MusicSocket tmp = NetworkManager.GetMusicSocket();
        if(tmp == null) return null;
        Socket socket = tmp.socket;

        PacketGetUserPlaylistsResponse response = new PacketGetUserPlaylistsResponse();

        try {
            socket.getOutputStream().write(request.toBytes());
            socket.getOutputStream().flush();
            response.fromBytes(socket.getInputStream());
        } catch (IOException e) {
            return null;
        }

        return new Pair<>(response.playlistsNames, response.playlists);
    }
}
