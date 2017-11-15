package me.shreyasr.networking.network;

import java.io.OutputStream;

public class SerializationStream {

    private final byte[] buffer;
    private int pos;
    private int dataSize;

    public SerializationStream(byte[] buffer) {
        this.buffer = buffer;
        this.pos = 0;
        this.dataSize = 0;
    }

    public SerializationStream(byte[] buffer, int dataSize) {
        this.buffer = buffer;
        this.pos = 0;
        this.dataSize = dataSize;
    }

    public int getPos() {
        return pos;
    }

    public void writeByte(byte b) {
        buffer[pos++] = b;
    }

    public byte readByte() {
        checkDataSize(1);
        return buffer[pos++];
    }

    public void writeInt(int i) {
        buffer[pos++] = (byte) (i >> 0);
        buffer[pos++] = (byte) (i >> 8);
        buffer[pos++] = (byte) (i >> 16);
        buffer[pos++] = (byte) (i >> 24);
    }

    public int readInt() {
        checkDataSize(4);
        return
            (buffer[pos++] & 255) << 0 |
            (buffer[pos++] & 255) << 8 |
            (buffer[pos++] & 255) << 16 |
            (buffer[pos++] & 255) << 24;
    }

    private void checkDataSize(int additionalBytes) {
        if (pos + additionalBytes > dataSize) {
            throw new IndexOutOfBoundsException(
                "Attempting to read " + additionalBytes + " bytes " +
                "at position " + pos + " " +
                "in buffer of size " + dataSize);
        }
    }
}
