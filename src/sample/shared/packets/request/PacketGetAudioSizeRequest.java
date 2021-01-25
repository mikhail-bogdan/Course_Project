package sample.shared.packets.request;

import sample.shared.ByteConverter;
import sample.shared.packets.IByteConvertible;

import java.io.IOException;
import java.io.InputStream;

public class PacketGetAudioSizeRequest implements IByteConvertible {
    public final byte type = 1;
    public final int size = 4;
    public int clipID;

    public byte[] toBytes() {
        byte[] data = new byte[size + 5];
        data[0] = type;
        System.arraycopy(ByteConverter.intToBytes(size), 0, data, 1, 4);
        System.arraycopy(ByteConverter.intToBytes(clipID), 0, data, 5, 4);
        return data;
    }

    public boolean fromBytes(InputStream stream) throws IOException {
        byte type = (byte)stream.read();
        if (type != this.type) return false;
        byte[] buffer = new byte[4];
        stream.read(buffer);
        int size = ByteConverter.bytesToInt(buffer, 0);
        if (size != this.size) return false;
        stream.read(buffer);
        clipID = ByteConverter.bytesToInt(buffer, 0);
        return true;
    }
}
