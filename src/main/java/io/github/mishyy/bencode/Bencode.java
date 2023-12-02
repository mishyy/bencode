package io.github.mishyy.bencode;

import io.github.mishyy.bencode.type.Type;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.Map;
import java.util.Objects;

public final class Bencode {

    private static final Bencode INSTANCE = new Bencode();

    public static Bencode get() {
        return INSTANCE;
    }

    public Type<?> type(final byte[] bytes) {
        Objects.requireNonNull(bytes, "bytes");

        try (final var in = new BencodeInputStream(new ByteArrayInputStream(bytes))) {
            return in.nextType();
        } catch (final Throwable t) {
            throw new BencodeException("Exception thrown during type detection", t);
        }
    }

    public String decodeString(final byte[] bytes) {
        return decode(Type.STRING, bytes);
    }

    public Number decodeNumber(final byte[] bytes) {
        return decode(Type.NUMBER, bytes);
    }

    public Iterable<?> decodeList(final byte[] bytes) {
        return decode(Type.LIST, bytes);
    }

    public Map<?, ?> decodeDictionary(final byte[] bytes) {
        return decode(Type.DICTIONARY, bytes);
    }

    @SuppressWarnings("unchecked")
    public <T> T decode(final Type<T> type, final byte[] bytes) {
        Objects.requireNonNull(type, "type");
        Objects.requireNonNull(bytes, "bytes");
        if (type == Type.UNKNOWN) {
            throw new IllegalArgumentException("type cannot be UNKNOWN");
        }

        try (final var in = new BencodeInputStream(new ByteArrayInputStream(bytes))) {
            if (type == Type.NUMBER) {
                return (T) in.readNumber();
            } else if (type == Type.LIST) {
                return (T) in.readList();
            } else if (type == Type.DICTIONARY) {
                return (T) in.readDictionary();
            }
            return (T) in.readString();
        } catch (final Throwable t) {
            throw new BencodeException("Exception thrown during decoding", t);
        }
    }

    public byte[] encode(final String string) {
        Objects.requireNonNull(string, "string");
        return encode(Type.STRING, string);
    }

    public byte[] encode(final Number number) {
        Objects.requireNonNull(number, "number");
        return encode(Type.NUMBER, number);
    }

    public byte[] encode(final Iterable<?> iterable) {
        Objects.requireNonNull(iterable, "iterable");
        return encode(Type.LIST, iterable);
    }

    public byte[] encode(final Map<?, ?> map) {
        Objects.requireNonNull(map, "map");
        return encode(Type.DICTIONARY, map);
    }

    private byte[] encode(final Type<?> type, final Object object) {
        try (final var baos = new ByteArrayOutputStream()) {
            try (final var out = new BencodeOutputStream(baos)) {
                if (type == Type.NUMBER) {
                    out.writeNumber((Number) object);
                } else if (type == Type.LIST) {
                    out.writeList((Iterable<?>) object);
                } else if (type == Type.DICTIONARY) {
                    out.writeDictionary((Map<?, ?>) object);
                } else {
                    out.writeString(object.toString());
                }
            }
            return baos.toByteArray();
        } catch (final Throwable t) {
            throw new BencodeException("Exception thrown during encoding", t);
        }
    }

}
