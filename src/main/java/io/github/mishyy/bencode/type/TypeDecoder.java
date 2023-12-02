package io.github.mishyy.bencode.type;

import io.github.mishyy.bencode.BencodeInputStream;

import java.io.IOException;

public interface TypeDecoder<R> {

    R decode(final BencodeInputStream stream) throws IOException;

}
