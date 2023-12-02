package io.github.mishyy.bencode.type.impl;

import io.github.mishyy.bencode.BencodeInputStream;
import io.github.mishyy.bencode.type.Type;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;

import static io.github.mishyy.bencode.util.Bytes.checkEOF;
import static io.github.mishyy.bencode.util.Bytes.validateToken;

public final class ListType implements Type<Iterable<?>> {

    @Override
    public boolean validate(final int token) {
        return token == Tokens.LIST;
    }

    @Override
    public Iterable<?> decode(final BencodeInputStream stream) throws IOException {
        var token = stream.read();
        validateToken(stream, LIST, token);

        final var list = new ArrayList<>();
        while ((token = stream.read()) != Tokens.TERMINATOR) {
            checkEOF(token);
            list.add(UNKNOWN.decode(stream, token));
        }
        return list;
    }

    @Override
    public byte[] encode(final Iterable<?> iterable) throws IOException {
        try (final var stream = new ByteArrayOutputStream()) {
            stream.write(Tokens.LIST);
            for (final var item : iterable) {
                stream.write(UNKNOWN.encode(item));
            }
            stream.write(Tokens.TERMINATOR);
            return stream.toByteArray();
        }
    }

}
