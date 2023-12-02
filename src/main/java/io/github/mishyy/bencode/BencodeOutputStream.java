package io.github.mishyy.bencode;

import io.github.mishyy.bencode.type.Type;

import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Map;

public final class BencodeOutputStream extends FilterOutputStream {

    public BencodeOutputStream(final OutputStream out) {
        super(out);
    }

    public void writeString(final byte[] bytes) throws IOException {
        write(Type.BYTES.encode(bytes));
    }

    public void writeString(final String string) throws IOException {
        write(Type.STRING.encode(string));
    }

    public void writeNumber(final Number number) throws IOException {
        write(Type.NUMBER.encode(number));
    }

    public void writeList(final Iterable<?> iterable) throws IOException {
        write(Type.LIST.encode(iterable));
    }

    public void writeDictionary(final Map<?, ?> map) throws IOException {
        write(Type.DICTIONARY.encode(map));
    }

}
