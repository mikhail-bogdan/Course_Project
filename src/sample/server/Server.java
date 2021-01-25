package sample.server;

import com.sun.javafx.logging.PlatformLogger;
import com.sun.javafx.util.Logging;
import sample.shared.ByteConverter;
import sample.shared.packets.response.*;

import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.logging.LogManager;

public class Server {
    private static PlatformLogger logger;
    public static void main(String[] args) throws IOException {
        //LogManager.getLogManager().readConfiguration(Server.class.getResourceAsStream("resources/logging.properties"));
        logger = Logging.getAccessibilityLogger();
        SessionManager.Init();
        AccountManager.Init();
        AudioDatabase.Init();
        ServerSocket socket = new ServerSocket(22000);
        while (true) {
            Socket sock = socket.accept();
            new MyThread(sock).start();
        }
    }

    private static class MyThread extends Thread {
        private Socket sock;
        private byte[] salt1;
        private byte[] salt2;

        public MyThread(Socket sock) {
            this.sock = sock;
        }

        @Override
        public void run() {
            try {
                byte[] tmpBuffer = new byte[5];
                InputStream stream = sock.getInputStream();
                while (true) {
                    int s = stream.read(tmpBuffer);
                    if(s == 0) {
                        Thread.onSpinWait();
                        continue;
                    }
                    int len = ByteConverter.bytesToInt(tmpBuffer, 1);
                    byte[] buffer = new byte[len];
                    int readLen = 0;
                    while (readLen < len) {
                        int l = stream.read(buffer, readLen, len - readLen);
                        readLen += l;
                    }
                    if (tmpBuffer[0] == 1) {
                        int audioID = ByteConverter.bytesToInt(buffer, 0);
                        AudioReader reader = new AudioReader("data/saved/" + audioID + ".wav");
                        byte[] outBuffer = new byte[13];
                        outBuffer[0] = 1;
                        System.arraycopy(ByteConverter.intToBytes(8), 0, outBuffer, 1, 4);
                        System.arraycopy(ByteConverter.longToBytes(reader.getClipSize()), 0, outBuffer, 5, 8);
                        sock.getOutputStream().write(outBuffer);
                        sock.getOutputStream().flush();
                    } else if (tmpBuffer[0] == 2) {
                        int audioID = ByteConverter.bytesToInt(buffer, 0);
                        AudioReader reader = new AudioReader("data/saved/" + audioID + ".wav");
                        long offset = ByteConverter.bytesToLong(buffer, 4);
                        int l = ByteConverter.bytesToInt(buffer, 12);
                        byte[] outBuffer = new byte[5 + l];
                        outBuffer[0] = 2;
                        System.arraycopy(ByteConverter.intToBytes(l), 0, outBuffer, 1, 4);
                        System.arraycopy(reader.getData(offset, l), 0, outBuffer, 5, l);
                        sock.getOutputStream().write(outBuffer);
                        sock.getOutputStream().flush();
                    } else if(tmpBuffer[0] == 10) {
                        logger.info("Client requested salt.");
                        salt1 = new byte[16];
                        salt2 = new byte[16];
                        SecureRandom random = new SecureRandom();
                        random.nextBytes(salt1);
                        random.nextBytes(salt2);
                        PacketGetLoginSaltResponse response = new PacketGetLoginSaltResponse();
                        response.salt1 = salt1;
                        response.salt2 = salt2;
                        sock.getOutputStream().write(response.toBytes());
                        sock.getOutputStream().flush();
                    } else if(tmpBuffer[0] == 11) {
                        logger.info("Client requested login.");
                        if (salt1 == null || salt2 == null) continue;
                        short loginSize = ByteConverter.bytesToShort(buffer, 0);
                        StringBuilder stringBuilder = new StringBuilder();
                        for (int i = 2; i < loginSize + 2; i++)
                            stringBuilder.append((char) buffer[i]);
                        String login = stringBuilder.toString();
                        UserData user = AccountManager.GetUser(login);
                        if (user == null) {
                            PacketLoginResponse loginResponse = new PacketLoginResponse();
                            loginResponse.error = 1;
                            sock.getOutputStream().write(loginResponse.toBytes());
                            sock.getOutputStream().flush();
                            continue;
                        }

                        byte[] passwordHash = new byte[32];
                        System.arraycopy(buffer, loginSize + 2, passwordHash, 0, 32);

                        byte[] result = new byte[32 * 2];
                        System.arraycopy(salt1, 0, result, 0, 16);
                        System.arraycopy(salt2, 0, result, 48, 16);
                        System.arraycopy(user.passwordHash, 0, result, 16, 32);

                        MessageDigest digest = null;
                        try {
                            digest = MessageDigest.getInstance("SHA-256");
                        } catch (NoSuchAlgorithmException e) {
                            e.printStackTrace();
                        }
                        digest.update(result);
                        byte[] tmp_result = digest.digest();

                        boolean isHashesEquals = true;
                        for (int i = 0; i < 32; i++)
                            if (passwordHash[i] != tmp_result[i]) {
                                isHashesEquals = false;
                                break;
                            }
                        if (isHashesEquals) {
                            PacketLoginResponse loginResponse = new PacketLoginResponse();
                            loginResponse.error = 0;
                            loginResponse.sessionKey = SessionManager.CreateSession(user.id);
                            sock.getOutputStream().write(loginResponse.toBytes());
                            sock.getOutputStream().flush();
                        } else {
                            PacketLoginResponse loginResponse = new PacketLoginResponse();
                            loginResponse.error = 1;
                            sock.getOutputStream().write(loginResponse.toBytes());
                            sock.getOutputStream().flush();
                        }
                    } else if(tmpBuffer[0] == 16) {
                        logger.info("Client requested playlists.");
                        byte[] sessionKey = new byte[128];
                        System.arraycopy(buffer, 0, sessionKey, 0, 128);
                        int userID = SessionManager.GetUserID(sessionKey);
                        UserData user = AccountManager.GetUser(userID);
                        if (user == null) break;
                        PacketGetUserPlaylistsResponse response = new PacketGetUserPlaylistsResponse();
                        response.playlistsNames = user.playlistsNames.toArray(new String[0]);
                        response.playlists = user.playlists;
                        sock.getOutputStream().write(response.toBytes());
                        sock.getOutputStream().flush();
                    } else if(tmpBuffer[0] == 12) {
                        int audioID = ByteConverter.bytesToInt(buffer, 0);
                        logger.info("Client requested audio text data (audioID=" + audioID + ").");
                        String[] data = AudioDatabase.GetAudioTextData(audioID);
                        if (data == null) break;
                        PacketGetAudioTextDataResponse response = new PacketGetAudioTextDataResponse();
                        response.author = data[0];
                        response.audioName = data[1];
                        sock.getOutputStream().write(response.toBytes());
                        sock.getOutputStream().flush();
                    } else if(tmpBuffer[0] == 17) {
                        logger.info("Client requested allAudio.");
                        int[] audios = AudioDatabase.GetAllAudios();
                        PacketAllAudioResponse response = new PacketAllAudioResponse();
                        response.audios = audios;
                        sock.getOutputStream().write(response.toBytes());
                        sock.getOutputStream().flush();
                    }
                }
                sock.close();
            } catch (IOException ignored) {
                try {
                    sock.close();
                } catch (IOException ignored1) {}
            }
        }
    }
}
