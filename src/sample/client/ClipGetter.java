package sample.client;

import javax.sound.sampled.AudioFormat;
import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.atomic.AtomicReferenceFieldUpdater;

public class ClipGetter extends InputStream {
    private volatile long totalSize;
    private volatile long totalFrames;
    private static final int DEFAULT_BUFFER_SIZE = 8192;
    private volatile int pos;
    private volatile int count;
    private volatile int markPos;
    private int markLimit;
    private volatile byte[] buffer;
    private int fullSampleSize;

    public ClipGetter() {
        this.buffer = new byte[DEFAULT_BUFFER_SIZE];
        this.markPos = -1;
        this.count = 0;
        this.pos = 0;
        this.fullSampleSize = 0;
        this.totalSize = 0;
        this.totalFrames = 0;
        this.markLimit = 0;
    }

    public synchronized void setPos(int pos) {
        this.pos = pos;
    }

    public synchronized void setFramePos(int framePos) {
        this.pos = framePos * 4;
    }

    public synchronized void setTotalSize(long totalSize) {
        this.totalSize = totalSize;
        this.totalFrames = totalSize / 4;
    }

    public synchronized long getTotalFrames() {
        return totalFrames;
    }

    public synchronized boolean isFullyLoaded() {
        return count > 0 && count >= totalSize;
    }

    @Override
    public synchronized void mark(int i) {
        this.markLimit = i;
        this.markPos = this.pos;
    }

    @Override
    public synchronized void reset() throws IOException {
        if (this.markPos < 0) {
            throw new IOException("Resetting to invalid mark");
        } else {
            this.pos = markPos;
        }
    }

    @Override
    public boolean markSupported() {
        return true;
    }

    public synchronized void push(byte[] data, int len) {
        if (this.count + len >= buffer.length) {
            int newLen = this.count * 2;
            while(newLen <= this.count + len)
                newLen *= 2;
            byte[] newBuffer = new byte[newLen];
                System.arraycopy(buffer, 0, newBuffer, 0, buffer.length);
            synchronized (buffer) {
                buffer = newBuffer;
            }
        }
        System.arraycopy(data, 0, buffer, this.count, len);
        this.count += len;

    }

    @Override
    public synchronized int read() {
        if (this.pos >= this.count) {
            return -1;
        }
        return buffer[pos++] & 255;
    }

    public int read(byte[] buffer) {
        return read(buffer, 0, buffer.length);
    }

    public int read(byte[] buffer, int offset, int len) {
        int readLen = 0;
        for(int i = 0; i < len; i++) {
            int tmp = read();
            if(tmp == -1) {
                return readLen;
            } else {
                readLen++;
                buffer[i] = (byte)(tmp & 255);
            }
        }
        return len;
    }

    @Override
    public long skip(long l) {
        if (l <= 0L) {
            return 0L;
        } else {
            if (this.pos + l >= this.count) {
                int len = this.count - this.pos;
                this.pos = this.count;
                return len;
            } else {
                this.pos += l;
                return l;
            }
        }
    }

    @Override
    public int available() throws IOException {
        return count - pos;
    }

    public void discard() {
        this.count = 0;
        this.pos = 0;
        this.markPos = -1;
        this.fullSampleSize = 0;
        this.totalSize = 0;
        this.totalFrames = 0;
        this.markLimit = 0;
    }
}
