package sample.shared.packets.response;

import sample.shared.ByteConverter;
import sample.shared.packets.IByteConvertible;

import java.io.IOException;
import java.io.InputStream;

public class PacketAllAudioResponse implements IByteConvertible {
    public final byte type = 17;
    public int size = -1;

    public int[] audios;

    @Override
    public byte[] toBytes() {
        int audiosSize = audios.length;
        size = audiosSize * 4 + 4;
        byte[] data = new byte[size + 5];
        data[0] = type;
        System.arraycopy(ByteConverter.intToBytes(size), 0, data, 1, 4);
        System.arraycopy(ByteConverter.intToBytes(audiosSize), 0, data, 5, 4);
        for(int i = 0; i < audiosSize; i++)
            System.arraycopy(ByteConverter.intToBytes(audios[i]), 0, data, 9 + i * 4, 4);
        return data;
    }

    @Override
    public boolean fromBytes(InputStream stream) throws IOException {
        byte type = (byte)stream.read();
        if (type != this.type) return false;
        byte[] buffer = new byte[4];
        stream.read(buffer);
        int size = ByteConverter.bytesToInt(buffer, 0);
        stream.read(buffer);
        int audiosSize = ByteConverter.bytesToInt(buffer, 0);
        audios = new int[audiosSize];
        for(int i = 0; i < audiosSize; i++) {
            stream.read(buffer);
            audios[i] = ByteConverter.bytesToInt(buffer, 0);
        }
        return true;
    }
}
