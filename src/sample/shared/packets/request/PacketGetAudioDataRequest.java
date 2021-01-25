package sample.shared.packets.request;

import sample.shared.ByteConverter;
import sample.shared.packets.IByteConvertible;

import java.io.IOException;
import java.io.InputStream;

public class PacketGetAudioDataRequest implements IByteConvertible {
    public final byte type = 2;
    public final int size = 16;
    public int audioID;
    public long offset;
    public int blockSize;

    @Override
    public byte[] toBytes() {
        byte[] data = new byte[size + 5];
        data[0] = type;
        System.arraycopy(ByteConverter.intToBytes(size), 0, data, 1, 4);
        System.arraycopy(ByteConverter.intToBytes(audioID), 0, data, 5, 4);
        System.arraycopy(ByteConverter.longToBytes(offset), 0, data, 9, 8);
        System.arraycopy(ByteConverter.intToBytes(blockSize), 0, data, 17, 4);
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
        offset = ByteConverter.bytesToLong(buffer, 0);
        stream.read(buffer, 0, 4);
        blockSize = ByteConverter.bytesToInt(buffer, 0);
        return true;
    }
}
