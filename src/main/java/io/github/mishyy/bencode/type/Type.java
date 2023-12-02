package io.github.mishyy.bencode.type;

import io.github.mishyy.Tokens.type.impl.DictionaryType;
import io.github.mishyy.bencode.BencodeInputStream;
import io.github.mishyy.bencode.type.impl.*;

import java.io.IOException;
import java.util.Map;

public interface Type<T> extends TypeValidator, TypeDecoder<T>, TypeEncoder<T> {

    Type<byte[]> BYTES = new BytesType();
    Type<String> STRING = new StringType();
    Type<Number> NUMBER = new NumberType();
    Type<Iterable<?>> LIST = new ListType();
    Type<Map<?, ?>> DICTIONARY = new DictionaryType();
    UnknownType UNKNOWN = new UnknownType();

    @Override
    boolean validate(final int token);

    @Override
    T decode(final BencodeInputStream stream) throws IOException;

    @Override
    byte[] encode(final T t) throws IOException;

    interface Tokens {

        int NUMBER = 'i';
        int LIST = 'l';
        int DICTIONARY = 'd';
        int TERMINATOR = 'e';
        int SEPARATOR = ':';

    }

}
