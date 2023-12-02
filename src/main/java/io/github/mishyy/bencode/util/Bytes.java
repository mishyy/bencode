package io.github.mishyy.bencode.util;

import io.github.mishyy.bencode.BencodeInputStream;
import io.github.mishyy.bencode.type.Type;

import java.io.EOFException;
import java.io.IOException;
import java.io.InvalidObjectException;

public final class Bytes {

    private static final Type<?>[] TYPES = { Type.STRING, Type.NUMBER, Type.LIST, Type.DICTIONARY };
    private static final int EOF = -1;

    public static Type<?> extractType(final int token) throws InvalidObjectException {
        for (final var type : TYPES) {
            if (type.validate(token)) {
                return type;
            }
        }
        return Type.UNKNOWN;
    }

    public static void validateToken(final BencodeInputStream stream, final Type<?> type, final int token) throws IOException {
        checkEOF(token);
        if (!type.validate(token)) {
            stream.unread(token);
            throw new InvalidObjectException("Unexpected token '" + new String(Character.toChars(token)) + "' ");
        }
    }

    public static void checkEOF(final int b) throws EOFException {
        if (b == EOF) {
            throw new EOFException();
        }
    }

}
