package io.github.mishyy.bencode;

import io.github.mishyy.bencode.type.Type;
import io.github.mishyy.bencode.util.Bytes;

import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PushbackInputStream;
import java.util.Map;

public final class BencodeInputStream extends FilterInputStream {

    private final PushbackInputStream in;

    public BencodeInputStream(final InputStream in) {
        super(new PushbackInputStream(in));
        this.in = (PushbackInputStream) super.in;
    }

    public void unread(final int b) throws IOException {
        in.unread(b);
    }


    public Type<?> nextType() throws IOException {
        final var token = peek();
        Bytes.checkEOF(token);
        return Bytes.extractType(token);
    }

    public byte[] readBytes() throws IOException {
        return Type.BYTES.decode(this);
    }

    public String readString() throws IOException {
        return Type.STRING.decode(this);
    }

    public Number readNumber() throws IOException {
        return Type.NUMBER.decode(this);
    }

    @SuppressWarnings("unchecked")
    public <T> Iterable<T> readList() throws IOException {
        return (Iterable<T>) Type.LIST.decode(this);
    }

    @SuppressWarnings("unchecked")
    public <K, V> Map<K, V> readDictionary() throws IOException {
        return (Map<K, V>) Type.DICTIONARY.decode(this);
    }

    private int peek() throws IOException {
        final var b = in.read();
        in.unread(b);
        return b;
    }

}
