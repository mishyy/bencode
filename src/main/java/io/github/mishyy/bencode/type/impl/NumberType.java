package io.github.mishyy.bencode.type.impl;

import io.github.mishyy.bencode.BencodeInputStream;
import io.github.mishyy.bencode.type.Type;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigDecimal;

import static io.github.mishyy.bencode.util.Bytes.checkEOF;
import static io.github.mishyy.bencode.util.Bytes.validateToken;

public final class NumberType implements Type<Number> {

    @Override
    public boolean validate(final int token) {
        return token == Tokens.NUMBER;
    }

    @Override
    public Number decode(final BencodeInputStream stream) throws IOException {
        var token = stream.read();
        validateToken(stream, NUMBER, token);

        final var buffer = new StringBuilder();
        while ((token = stream.read()) != Tokens.TERMINATOR) {
            checkEOF(token);
            buffer.append((char) token);
        }
        return new BigDecimal(buffer.toString()).longValue();
    }

    @Override
    public byte[] encode(final Number number) throws IOException {
        try (final var stream = new ByteArrayOutputStream()) {
            stream.write(Tokens.NUMBER);
            stream.write(Long.toString(number.longValue()).getBytes());
            stream.write(Tokens.TERMINATOR);
            return stream.toByteArray();
        }
    }

}
