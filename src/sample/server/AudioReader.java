package sample.server;

import sample.shared.ByteConverter;

import javax.sound.sampled.AudioFormat;
import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;

public class AudioReader {
    BufferedInputStream stream;
    long clipSize;
    int dataOffset;

    public AudioReader(String filename) {
        try {
            stream = new BufferedInputStream(new FileInputStream(filename));

            boolean isDataFound = false;

            byte[] buffer = new byte[2048];

            while (!isDataFound) {
                stream.read(buffer, 0, 8);
                dataOffset += 8;
                if (buffer[0] == 'R' && buffer[1] == 'I' && buffer[2] == 'F' && buffer[3] == 'F') {
                    stream.read(buffer, 0, 4);
                    dataOffset += 4;
                } else if (buffer[0] == 'd' && buffer[1] == 'a' && buffer[2] == 't' && buffer[3] == 'a') {
                    isDataFound = true;
                    clipSize = ByteConverter.bytesToInt(buffer, 4);
                    stream.mark(0);
                } else if (buffer[0] == 'f' && buffer[1] == 'm' && buffer[2] == 't' && buffer[3] == ' ') {
                    dataOffset += ByteConverter.bytesToInt(buffer, 4);
                    stream.read(buffer, 0, ByteConverter.bytesToInt(buffer, 4));
                    short nChannels = ByteConverter.bytesToShort(buffer, 2);
                    int samplesPerSec = ByteConverter.bytesToInt(buffer, 4);
                    short sampleSizeInBits = ByteConverter.bytesToShort(buffer, 14);
                    AudioFormat format = new AudioFormat(samplesPerSec, sampleSizeInBits, nChannels, true, false);
                } else {
                    dataOffset += ByteConverter.bytesToInt(buffer, 4);
                    stream.read(buffer, 0, ByteConverter.bytesToInt(buffer, 4));
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public long getClipSize() {
        return clipSize;
    }

    public byte[] getData(long offset, int size) {
        byte[] buffer = new byte[size];
        try {
            stream.reset();
            int skipped = 0;
            while (skipped < offset) {
                long len = stream.skip(dataOffset + offset - skipped);
                skipped += len;
            }
            stream.read(buffer);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return buffer;
    }
}
