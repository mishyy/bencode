package io.github.mishyy.Tokens.type.impl;

import io.github.mishyy.bencode.BencodeInputStream;
import io.github.mishyy.bencode.type.Type;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.*;

import static io.github.mishyy.bencode.util.Bytes.checkEOF;
import static io.github.mishyy.bencode.util.Bytes.validateToken;

public final class DictionaryType implements Type<Map<?, ?>> {

    @Override
    public boolean validate(final int token) {
        return token == Tokens.DICTIONARY;
    }

    @Override
    public Map<?, ?> decode(final BencodeInputStream stream) throws IOException {
        var token = stream.read();
        validateToken(stream, DICTIONARY, token);

        final var map = new LinkedHashMap<>();
        while ((token = stream.read()) != Tokens.TERMINATOR) {
            checkEOF(token);
            stream.unread(token);

            final var key = STRING.decode(stream);
            final var value = UNKNOWN.decode(stream);
            map.put(key, value);
        }
        return map;
    }

    @Override
    public byte[] encode(final Map<?, ?> m) throws IOException {
        Objects.requireNonNull(m, "map");

        var map = m;
        if (!(m instanceof SortedMap<?, ?>)) {
            map = new TreeMap<>(m);
        }

        try (final var stream = new ByteArrayOutputStream()) {
            stream.write(Tokens.DICTIONARY);
            for (final var entry : map.entrySet()) {
                stream.write(UNKNOWN.encode(entry.getKey().toString()));
                stream.write(UNKNOWN.encode(entry.getValue()));
            }
            stream.write(Tokens.TERMINATOR);
            return stream.toByteArray();
        }
    }

}
