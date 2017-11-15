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

    public void writeBools(boolean b1, boolean b2, boolean b3, boolean b4,
                           boolean b5, boolean b6, boolean b7, boolean b8) {
        buffer[pos++] = (byte)(
            (b1 ? 1<<0 : 0) +
            (b2 ? 1<<1 : 0) +
            (b3 ? 1<<2 : 0) +
            (b4 ? 1<<3 : 0) +
            (b5 ? 1<<4 : 0) +
            (b6 ? 1<<5 : 0) +
            (b7 ? 1<<6 : 0) +
            (b8 ? 1<<7 : 0));
    }

    public boolean readBool(int index) {
        return ((buffer[pos] >> index) & 0x01) == 1;
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
