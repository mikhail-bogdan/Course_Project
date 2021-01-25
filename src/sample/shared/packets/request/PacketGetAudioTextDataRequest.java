package sample.shared.packets.request;

import sample.shared.ByteConverter;
import sample.shared.packets.IByteConvertible;

import java.io.IOException;
import java.io.InputStream;

public class PacketGetAudioTextDataRequest implements IByteConvertible {
    public final byte type = 12;
    public final int size = 4;
    public int audioID;

    @Override
    public byte[] toBytes() {
        byte[] data = new byte[size + 5];
        data[0] = type;
        System.arraycopy(ByteConverter.intToBytes(size), 0, data, 1, 4);
        System.arraycopy(ByteConverter.intToBytes(audioID), 0, data, 5, 4);
        return data;
    }

    @Override
    public boolean fromBytes(InputStream stream) throws IOException {
        byte type = (byte)stream.read();
        if (type != this.type) return false;
        byte[] buffer = new byte[8];
        stream.read(buffer, 0, 4);
        int size = ByteConverter.bytesToInt(buffer, 0);
        if (size != this.size) return false;
        stream.read(buffer, 0, 4);
        audioID = ByteConverter.bytesToInt(buffer, 0);
        stream.read(buffer);
        return true;
    }
}
