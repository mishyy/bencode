package io.github.mishyy.bencode.type.impl;

import io.github.mishyy.bencode.BencodeInputStream;
import io.github.mishyy.bencode.type.Type;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import static io.github.mishyy.bencode.util.Bytes.validateToken;

public final class BytesType implements Type<byte[]> {

    @Override
    public boolean validate(final int token) {
        return Character.isDigit(token);
    }

    @Override
    public byte[] decode(final BencodeInputStream stream) throws IOException {
        final var buffer = new StringBuilder();

        int token;
        while ((token = stream.read()) != Tokens.SEPARATOR) {
            validateToken(stream, Type.STRING, token);
            buffer.append((char) token);
        }

        final var length = Integer.parseInt(buffer.toString());
        return stream.readNBytes(length);
    }

    @Override
    public byte[] encode(final byte[] bytes) throws IOException {
        try (final var stream = new ByteArrayOutputStream()) {
            stream.write(Integer.toString(bytes.length).getBytes());
            stream.write(Tokens.SEPARATOR);
            stream.write(bytes);
            return stream.toByteArray();
        }
    }

}
