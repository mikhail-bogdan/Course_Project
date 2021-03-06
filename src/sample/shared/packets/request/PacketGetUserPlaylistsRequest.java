package sample.shared.packets.request;

import sample.shared.ByteConverter;
import sample.shared.packets.IByteConvertible;

import java.io.IOException;
import java.io.InputStream;

public class PacketGetUserPlaylistsRequest implements IByteConvertible {
    public final byte type = 16;
    public final int size = 128;

    public byte[] sessionKey;



    @Override
    public byte[] toBytes() {
        byte[] data = new byte[size + 5];
        data[0] = type;
        System.arraycopy(ByteConverter.intToBytes(size), 0, data, 1, 4);
        System.arraycopy(sessionKey, 0, data, 5, 128);
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
        sessionKey = new byte[128];
        stream.read(sessionKey);
        return true;
    }
}
