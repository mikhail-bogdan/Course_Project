package sample.shared.packets.response;

import sample.shared.ByteConverter;
import sample.shared.packets.IByteConvertible;

import java.io.IOException;
import java.io.InputStream;

public class PacketGetAudioTextDataResponse implements IByteConvertible {
    public final byte type = 12;
    public int size = -1;

    public String author;
    public String audioName;

    @Override
    public byte[] toBytes() {
        size = author.length() + audioName.length() + 4;
        byte[] data = new byte[size + 5];
        data[0] = type;
        System.arraycopy(ByteConverter.intToBytes(size), 0, data, 1, 4);
        short authorSize = (short) author.length();
        short audioNameSize = (short) audioName.length();
        System.arraycopy(ByteConverter.shortToBytes(authorSize), 0, data, 5, 2);
        System.arraycopy(ByteConverter.shortToBytes(audioNameSize), 0, data, 7, 2);
        System.arraycopy(author.getBytes(), 0, data, 9, authorSize);
        System.arraycopy(audioName.getBytes(), 0, data, 9 + authorSize, audioNameSize);
        return data;
    }

    @Override
    public boolean fromBytes(InputStream stream) throws IOException {
        byte type = (byte)stream.read();
        if (type != this.type) return false;
        byte[] buffer = new byte[4];
        stream.read(buffer);
        int size = ByteConverter.bytesToInt(buffer, 0);
        stream.read(buffer, 0, 2);
        short authorSize = ByteConverter.bytesToShort(buffer, 0);
        stream.read(buffer, 0, 2);
        short audioNameSize = ByteConverter.bytesToShort(buffer, 0);
        StringBuilder builder = new StringBuilder();
        for(int j = 0; j < authorSize; j++)
            builder.append((char)stream.read());
        author = builder.toString();
        builder = new StringBuilder();
        for(int j = 0; j < audioNameSize; j++)
            builder.append((char)stream.read());
        audioName = builder.toString();
        return true;
    }
}
