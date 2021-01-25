package sample.shared.packets.response;

import sample.shared.ByteConverter;
import sample.shared.packets.IByteConvertible;

import java.io.IOException;
import java.io.InputStream;

public class PacketGetAudioSizeResponse implements IByteConvertible {
    public final byte type = 1;
    public final int size = 8;
    public long clipSize;

    public byte[] toBytes() {
        byte[] data = new byte[size + 5];
        data[0] = type;
        System.arraycopy(ByteConverter.intToBytes(size), 0, data, 1, 4);
        System.arraycopy(ByteConverter.longToBytes(clipSize), 0, data, 5, 8);
        return data;
    }

    public boolean fromBytes(InputStream stream) throws IOException {
        byte type = (byte)stream.read();
        if (type != this.type) return false;
        byte[] buffer = new byte[8];
        stream.read(buffer, 0, 4);
        int size = ByteConverter.bytesToInt(buffer, 0);
        if (size != this.size) return false;
        stream.read(buffer);
        clipSize = ByteConverter.bytesToLong(buffer, 0);
        return true;
    }
}
