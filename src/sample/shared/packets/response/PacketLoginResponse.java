package sample.shared.packets.response;

import sample.shared.ByteConverter;
import sample.shared.packets.IByteConvertible;

import java.io.IOException;
import java.io.InputStream;

public class PacketLoginResponse implements IByteConvertible {
    public final byte type = 11;
    public int size = 1;

    public byte error;
    public byte[] sessionKey;


    @Override
    public byte[] toBytes() {
        byte[] data;
        if(error == 0) {
            size = 129;
            data = new byte[size + 5];
            data[0] = type;
            System.arraycopy(ByteConverter.intToBytes(size), 0, data, 1, 4);
            data[5] = error;
            System.arraycopy(sessionKey, 0, data, 6, 128);
        } else {
            size = 1;
            data = new byte[size + 5];
            data[0] = type;
            System.arraycopy(ByteConverter.intToBytes(size), 0, data, 1, 4);
            data[5] = error;
        }
        return data;
    }

    @Override
    public boolean fromBytes(InputStream stream) throws IOException {
        byte type = (byte) stream.read();
        if (type != this.type) return false;
        byte[] buffer = new byte[4];
        stream.read(buffer);
        int size = ByteConverter.bytesToInt(buffer, 0);
        error = (byte) stream.read();
        if(error == 0) {
            this.size = 129;
            if (size != this.size) return false;
            sessionKey = new byte[128];
            stream.read(sessionKey);
        } else {
            this.size = 1;
            if (size != this.size) return false;
        }
        return true;
    }
}
