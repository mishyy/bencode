package io.github.mishyy.bencode.type;

import java.io.IOException;

public interface TypeEncoder<T> {

    byte[] encode(final T t) throws IOException;

}
