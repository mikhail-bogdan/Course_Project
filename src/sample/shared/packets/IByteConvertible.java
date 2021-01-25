package sample.shared.packets;

import java.io.IOException;
import java.io.InputStream;

public interface IByteConvertible {
    byte[] toBytes();
    boolean fromBytes(InputStream stream) throws IOException;
}
