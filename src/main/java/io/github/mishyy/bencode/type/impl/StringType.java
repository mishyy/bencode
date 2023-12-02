package io.github.mishyy.bencode.type.impl;

import io.github.mishyy.bencode.BencodeInputStream;
import io.github.mishyy.bencode.type.Type;

import java.io.IOException;

public final class StringType implements Type<String> {

    @Override
    public boolean validate(final int token) {
        return BYTES.validate(token);
    }

    @Override
    public String decode(final BencodeInputStream stream) throws IOException {
        return new String(BYTES.decode(stream));
    }

    @Override
    public byte[] encode(final String string) throws IOException {
        return BYTES.encode(string.getBytes());
    }

}
