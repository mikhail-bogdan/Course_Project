package sample.shared.packets.response;

import sample.shared.ByteConverter;
import sample.shared.packets.IByteConvertible;

import java.io.IOException;
import java.io.InputStream;

public class PacketGetAudioDataResponse implements IByteConvertible {
    public final byte type = 2;
    public int size;
    public byte[] audioData;

    @Override
    public byte[] toBytes() {
        size = audioData.length;
        byte[] data = new byte[size + 5];
        data[0] = type;
        System.arraycopy(ByteConverter.intToBytes(size), 0, data, 1, 4);
        System.arraycopy(audioData, 0, data, 5, audioData.length);
        return data;
    }

    @Override
    public boolean fromBytes(InputStream stream) throws IOException {
        byte type = (byte)stream.read();
        if (type != this.type) return false;
        byte[] buffer = new byte[4];
        stream.read(buffer);
        size = ByteConverter.bytesToInt(buffer, 0);
        audioData = new byte[size];
        stream.read(audioData);
        return true;
    }
}
