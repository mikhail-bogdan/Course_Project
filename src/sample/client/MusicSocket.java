package sample.client;

import sample.shared.packets.request.PacketAllAudioRequest;
import sample.shared.packets.request.PacketGetAudioDataRequest;
import sample.shared.packets.request.PacketGetAudioSizeRequest;
import sample.shared.packets.request.PacketGetAudioTextDataRequest;
import sample.shared.packets.response.PacketAllAudioResponse;
import sample.shared.packets.response.PacketGetAudioDataResponse;
import sample.shared.packets.response.PacketGetAudioSizeResponse;
import sample.shared.packets.response.PacketGetAudioTextDataResponse;

import java.io.IOException;
import java.net.Socket;

public class MusicSocket {
    public Socket socket;

    public MusicSocket(Socket socket) {
        this.socket = socket;
    }

    public long GetClipSize(int clipID) {
        try {
            PacketGetAudioSizeRequest request = new PacketGetAudioSizeRequest();
            request.clipID = clipID;
            socket.getOutputStream().write(request.toBytes());
            socket.getOutputStream().flush();

            PacketGetAudioSizeResponse response = new PacketGetAudioSizeResponse();
            response.fromBytes(socket.getInputStream());
            return response.clipSize;
        } catch (IOException e) {
            e.printStackTrace();
            return -1;
        }
    }

    public byte[] GetClipData(int clipID, long offset, int size) {
        try {
            PacketGetAudioDataRequest request = new PacketGetAudioDataRequest();
            request.audioID = clipID;
            request.offset = offset;
            request.blockSize = size;
            socket.getOutputStream().write(request.toBytes());
            socket.getOutputStream().flush();

            PacketGetAudioDataResponse response = new PacketGetAudioDataResponse();
            response.fromBytes(socket.getInputStream());
            return response.audioData;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public String[] GetAudioTextData(int audioID) {
        try {
            PacketGetAudioTextDataRequest request = new PacketGetAudioTextDataRequest();
            request.audioID = audioID;
            socket.getOutputStream().write(request.toBytes());
            socket.getOutputStream().flush();

            PacketGetAudioTextDataResponse response = new PacketGetAudioTextDataResponse();
            response.fromBytes(socket.getInputStream());
            return new String[] {response.author, response.audioName};
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public int[] GetAllAudio() {
        try {
            PacketAllAudioRequest request = new PacketAllAudioRequest();
            socket.getOutputStream().write(request.toBytes());
            socket.getOutputStream().flush();

            PacketAllAudioResponse response = new PacketAllAudioResponse();
            response.fromBytes(socket.getInputStream());
            return response.audios;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public void Close() {
        try {
            socket.close();
        } catch (IOException ignored) {}
    }
}
