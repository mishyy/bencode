package io.github.mishyy.bencode.type.impl;

import io.github.mishyy.bencode.BencodeInputStream;
import io.github.mishyy.bencode.type.Type;
import io.github.mishyy.bencode.util.Bytes;

import java.io.IOException;
import java.io.InvalidObjectException;
import java.util.Map;

public final class UnknownType implements Type<Object> {

    @Override
    public boolean validate(final int token) {
        return false;
    }

    @Override
    public Object decode(final BencodeInputStream stream) throws IOException {
        return decode(stream, stream.read());
    }

    public Object decode(final BencodeInputStream stream, final int token) throws IOException {
        stream.unread(token);

        final var type = Bytes.extractType(token);
        if (type == Type.UNKNOWN) {
            throw new InvalidObjectException("Unknown token '" + new String(Character.toChars(token)) + "'");
        }
        return type.decode(stream);
    }

    @Override
    public byte[] encode(final Object object) throws IOException {
        return switch (object) {
            case byte[] bytes -> BYTES.encode(bytes);
            case Number number -> NUMBER.encode(number);
            case Iterable<?> iterable -> LIST.encode(iterable);
            case Map<?, ?> map -> DICTIONARY.encode(map);
            default -> STRING.encode(object.toString());
        };
    }

}
