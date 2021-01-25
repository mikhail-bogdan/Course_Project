package sample.shared.packets.request;

import sample.shared.ByteConverter;
import sample.shared.packets.IByteConvertible;

import java.io.IOException;
import java.io.InputStream;

public class PacketLoginRequest implements IByteConvertible {
    public final byte type = 11;
    public int size = -1;

    public short loginSize;
    public String login;
    public byte[] passwordHash;



    @Override
    public byte[] toBytes() {
        loginSize = (short) login.length();
        size = loginSize + 34;
        byte[] data = new byte[size + 5];
        data[0] = type;
        System.arraycopy(ByteConverter.intToBytes(size), 0, data, 1, 4);
        System.arraycopy(ByteConverter.shortToBytes(loginSize), 0, data, 5, 2);
        System.arraycopy(login.getBytes(), 0, data, 7, loginSize);
        System.arraycopy(passwordHash, 0, data, 7 + loginSize, 32);
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
        loginSize = ByteConverter.bytesToShort(buffer, 0);
        this.size = loginSize + 34;
        if (size != this.size) return false;
        StringBuilder builder = new StringBuilder();
        for(int i = 0; i < loginSize; i++)
            builder.append((char)stream.read());
        login = builder.toString();
        passwordHash = new byte[32];
        stream.read(passwordHash);
        return true;
    }
}
