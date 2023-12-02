package io.github.mishyy.bencode;

import io.github.mishyy.bencode.type.Type;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;

import java.io.EOFException;
import java.io.InvalidObjectException;
import java.util.*;
import java.util.concurrent.ConcurrentSkipListMap;

import static org.junit.jupiter.api.Assertions.*;

@SuppressWarnings("unchecked")
public final class BencodeTest {

    private static final Bencode BENCODE = Bencode.get();

    private static <T extends Throwable> void assertCause(final Class<T> cause, final Executable executable) {
        final var e = assertThrows(BencodeException.class, executable);
        e.printStackTrace();
        assertInstanceOf(cause, e.getCause());
    }

    @Test
    public void testTypeString() {
        assertSame(Type.STRING, BENCODE.type("7".getBytes()));
    }

    @Test
    public void testTypeNumber() {
        assertSame(Type.NUMBER, BENCODE.type("i1".getBytes()));
    }

    @Test
    public void testTypeList() {
        assertSame(Type.LIST, BENCODE.type("l123".getBytes()));
    }

    @Test
    public void testTypeDictionary() {
        assertSame(Type.DICTIONARY, BENCODE.type("dtesting".getBytes()));
    }

    @Test
    public void testTypeUnknown() {
        assertSame(Type.UNKNOWN, BENCODE.type("unknown".getBytes()));
    }

    @Test
    public void testTypeEmpty() {
        assertCause(EOFException.class, () -> BENCODE.type(new byte[0]));
    }

    @Test
    public void testTypeNullBytes() {
        assertThrows(NullPointerException.class, () -> BENCODE.type(null), "bytes");
    }

    @Test
    public void testDecodeNullType() {
        assertThrows(NullPointerException.class, () -> BENCODE.decode(null, "12:Hello World!".getBytes()), "type");
    }

    @Test
    public void testDecodeUnknownType() {
        assertThrows(IllegalArgumentException.class, () -> BENCODE.decode(Type.UNKNOWN, "12:Hello World!".getBytes()), "type cannot be UNKNOWN");
    }

    @Test
    public void testDecodeNullBytes() {
        assertThrows(NullPointerException.class, () -> BENCODE.decode(Type.STRING, null), "bytes");
    }

    @Test
    public void testDecodeString() {
        assertEquals("Hello World!", BENCODE.decodeString("12:Hello World!".getBytes()));
    }

    @Test
    public void testDecodeStringMultiByteCodePoints() {
        assertEquals("Garçon", BENCODE.decodeString("7:Garçon".getBytes()));
    }

    @Test
    public void testDecodeEmptyString() {
        assertEquals("", BENCODE.decodeString("0:123".getBytes()));
    }

    @Test
    public void testDecodeStringNaN() {
        assertCause(InvalidObjectException.class, () -> BENCODE.decodeString("1c3:Testing".getBytes()));
    }

    @Test
    public void testDecodeStringEOF() {
        assertCause(EOFException.class, () -> BENCODE.decodeString("123456".getBytes()));
    }

    @Test
    public void testDecodeStringEmpty() {
        assertCause(EOFException.class, () -> BENCODE.decodeString("".getBytes()));
    }

    @Test
    public void testDecodeNumber() {
        assertEquals(123456L, BENCODE.decodeNumber("i123456e123".getBytes()));
    }

    @Test
    public void testDecodeNumberNaN() {
        assertCause(NumberFormatException.class, () -> BENCODE.decodeNumber("i123cbve1".getBytes()));
    }

    @Test
    public void testDecodeNumberEOF() {
        assertCause(EOFException.class, () -> BENCODE.decodeNumber("i123".getBytes()));
    }

    @Test
    public void testDecodeNumberEmpty() {
        assertCause(EOFException.class, () -> BENCODE.decodeNumber("".getBytes()));
    }

    @Test
    public void testDecodeList() {
        final var decoded = (List<Object>) BENCODE.decodeList("l5:Hello6:World!li123ei456eeetesting".getBytes());
        assertEquals(3, decoded.size());

        assertEquals("Hello", decoded.get(0));
        assertEquals("World!", decoded.get(1));

        final var list = (List<Number>) decoded.get(2);
        assertEquals(123L, list.get(0).longValue());
        assertEquals(456L, list.get(1).longValue());
    }

    @Test
    public void testDecodeListEmpty() {
        final var decoded = (List<Object>) BENCODE.decodeList("le123".getBytes());
        assertTrue(decoded.isEmpty());
    }

    @Test
    public void testDecodeListInvalidItem() {
        assertCause(InvalidObjectException.class, () -> BENCODE.decodeList("l2:Worlde".getBytes()));
    }

    @Test
    public void testDecodeListEOF() {
        assertCause(EOFException.class, () -> BENCODE.decodeList("l5:Hello".getBytes()));
    }

    @Test
    public void testDecodeDictionary() {
        final var decoded = BENCODE.decodeDictionary("d4:dictd3:1234:test3:4565:thinge4:listl11:list-item-111:list-item-2e6:numberi123456e6:string5:valuee".getBytes());
        assertEquals(4, decoded.size());

        assertEquals("value", decoded.get("string"));
        assertEquals(123456L, ((Number) decoded.get("number")).longValue());

        final var list = (List<Object>) decoded.get("list");
        assertEquals(2, list.size());
        assertEquals("list-item-1", list.get(0));
        assertEquals("list-item-2", list.get(1));

        final var map = (Map<String, Object>) decoded.get("dict");
        assertEquals(2, map.size());
        assertEquals("test", map.get("123"));
        assertEquals("thing", map.get("456"));
    }

    @Test
    public void testDecodeDictionaryEmpty() {
        final var decoded = BENCODE.decodeDictionary("de123test".getBytes());
        assertTrue(decoded.isEmpty());
    }

    @Test
    public void testDecodeDictionaryInvalidItem() {
        assertCause(InvalidObjectException.class, () -> BENCODE.decodeDictionary("d4:item5:value3:testing".getBytes()));
    }

    @Test
    public void testDecodeDictionaryEOF() {
        assertCause(EOFException.class, () -> BENCODE.decodeDictionary("d4:item5:test".getBytes()));
    }

    @Test
    public void testWriteString() {
        final var encoded = BENCODE.encode("Hello World!");
        assertEquals("12:Hello World!", new String(encoded));
    }

    @Test
    public void testWriteStringMultiByteCodePoints() {
        assertEquals("7:Garçon", new String(BENCODE.encode("Garçon")));
    }

    @Test
    public void testWriteStringEmpty() {
        assertEquals("0:", new String(BENCODE.encode("")));
    }

    @Test
    public void testWriteStringNull() {
        assertThrows(NullPointerException.class, () -> BENCODE.encode((String) null), "string");
    }

    @Test
    public void testWriteNumber() {
        assertEquals("i123456e", new String(BENCODE.encode(123456)));
    }

    @Test
    public void testWriteNumberDecimal() {
        assertEquals("i123e", new String(BENCODE.encode(123.456)));
    }

    @Test
    public void testWriteNumberNull() {
        assertThrows(NullPointerException.class, () -> BENCODE.encode((Number) null), "number");
    }

    @Test
    public void testWriteList() {
        final var list = List.of(
                "Hello",
                "World!",
                Arrays.asList(123, 456)
        );
        assertEquals("l5:Hello6:World!li123ei456eee", new String(BENCODE.encode(list)));
    }

    @Test
    public void testWriteListEmpty() {
        assertEquals("le", new String(BENCODE.encode(List.of())));
    }

    @Test
    public void testWriteListNullItem() {
        final var list = List.of(
                "Hello",
                "World!",
                Arrays.asList(null, 456)
        );
        assertCause(NullPointerException.class, () -> BENCODE.encode(list));
    }

    @Test
    public void testWriteListNull() {
        assertThrows(NullPointerException.class, () -> BENCODE.encode((List<?>) null), "iterable");
    }

    @Test
    public void testWriteDictionary() {
        final var map = new LinkedHashMap<>() {{
            put("string", "value");
            put("number", 123456);
            put("list", List.of("list-item-1", "list-item-2"));
            put("dict", new ConcurrentSkipListMap<>() {{
                put(123, "test");
                put(456, "thing");
            }});
        }};
        assertEquals("d4:dictd3:1234:test3:4565:thinge4:listl11:list-item-111:list-item-2e6:numberi123456e6:string5:valuee", new String(BENCODE.encode(map)));
    }

    @Test
    public void testWriteDictionaryEmpty() {
        assertEquals("de", new String(BENCODE.encode(Map.of())));
    }

    @Test
    public void testWriteDictionaryKeyCastException() {
        assertThrows(ClassCastException.class, () -> BENCODE.encode(new TreeMap<>() {{
            put("string", "value");
            put(123, "number-key");
        }}));
    }

    @Test
    public void testWriteDictionaryNull() {
        assertThrows(NullPointerException.class, () -> BENCODE.encode((Map<?, ?>) null), "map");
    }

}
