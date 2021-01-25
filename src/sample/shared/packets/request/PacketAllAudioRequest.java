package sample.shared.packets.request;

import sample.shared.ByteConverter;
import sample.shared.packets.IByteConvertible;

import java.io.IOException;
import java.io.InputStream;

public class PacketAllAudioRequest implements IByteConvertible {
    public final byte type = 17;
    public final int size = 0;

    @Override
    public byte[] toBytes() {
        byte[] data = new byte[size + 5];
        data[0] = type;
        System.arraycopy(ByteConverter.intToBytes(size), 0, data, 1, 4);
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
        return true;
    }
}
