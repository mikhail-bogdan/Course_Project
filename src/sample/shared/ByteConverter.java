package sample.shared;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class ByteConverter {
    public static byte[] intToBytes(int value) {
        return ByteBuffer.allocate(4).order(ByteOrder.LITTLE_ENDIAN).putInt(value).array();
    }

    public static int bytesToInt(byte[] bytes, int offset) {
        return ByteBuffer.wrap(bytes, offset, 4).order(ByteOrder.LITTLE_ENDIAN).getInt();
    }
    public static byte[] longToBytes(long value) {
        return ByteBuffer.allocate(8).order(ByteOrder.LITTLE_ENDIAN).putLong(value).array();
    }

    public static long bytesToLong(byte[] bytes, int offset) {
        return ByteBuffer.wrap(bytes, offset, 8).order(ByteOrder.LITTLE_ENDIAN).getLong();
    }

    public static short bytesToShort(byte[] bytes, int offset) {
        return ByteBuffer.wrap(bytes, offset, 2).order(ByteOrder.LITTLE_ENDIAN).getShort();
    }

    public static byte[] boolToBytes(boolean value) {
        return new byte[] {(byte)(value ? 1 : 0)};
    }

    public static byte boolToByte(boolean value) {
        return (byte)(value ? 1 : 0);
    }

    public static boolean byteToBool(byte b) {
        return b == 1;
    }

    public static byte[] shortToBytes(short value) {
        return ByteBuffer.allocate(2).order(ByteOrder.LITTLE_ENDIAN).putShort(value).array();
    }
}
