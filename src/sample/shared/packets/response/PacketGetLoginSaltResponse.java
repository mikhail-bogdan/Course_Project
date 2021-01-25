package sample.shared.packets.response;

import sample.shared.ByteConverter;
import sample.shared.packets.IByteConvertible;

import java.io.IOException;
import java.io.InputStream;

public class PacketGetLoginSaltResponse implements IByteConvertible {
    public final byte type = 10;
    public final int size = 32;
    public byte[] salt1;
    public byte[] salt2;

    @Override
    public byte[] toBytes() {
        byte[] data = new byte[size + 5];
        data[0] = type;
        System.arraycopy(ByteConverter.intToBytes(size), 0, data, 1, 4);
        System.arraycopy(salt1, 0, data, 5, 16);
        System.arraycopy(salt2, 0, data, 21, 16);
        return data;
    }

    @Override
    public boolean fromBytes(InputStream stream) throws IOException {
        byte type = (byte)stream.read();
        if (type != this.type) return false;
        byte[] buffer = new byte[4];
        stream.read(buffer);
        int size = ByteConverter.bytesToInt(buffer, 0);
        if (size != this.size) return false;
        salt1 = new byte[16];
        salt2 = new byte[16];
        stream.read(salt1);
        stream.read(salt2);
        return true;
    }
}
