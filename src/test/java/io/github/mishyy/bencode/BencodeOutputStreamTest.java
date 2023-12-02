package io.github.mishyy.bencode;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.util.*;
import java.util.concurrent.ConcurrentSkipListMap;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public final class BencodeOutputStreamTest {

    private ByteArrayOutputStream baos;
    private BencodeOutputStream out;

    @BeforeEach
    public void setUp() {
        baos = new ByteArrayOutputStream();
        out = new BencodeOutputStream(baos);
    }

    @Test
    public void testConstructorNullStream() {
        new BencodeOutputStream(null);
    }

    @Test
    public void testWriteString() throws Exception {
        out.writeString("Hello World!");

        assertEquals("12:Hello World!", baos.toString());
    }

    @Test
    public void testWriteStringEmpty() throws Exception {
        out.writeString("");

        assertEquals("0:", baos.toString());
    }

    @Test
    public void testWriteStringNull() {
        assertThrows(NullPointerException.class, () -> out.writeString((String) null));
        assertEquals(0, baos.toByteArray().length);
    }

    @Test
    public void testWriteStringByteArray() throws Exception {
        out.writeString("Hello World!".getBytes());

        assertEquals("12:Hello World!", baos.toString());
    }

    @Test
    public void testWriteStringEmptyByteArray() throws Exception {
        out.writeString(new byte[0]);

        assertEquals("0:", baos.toString());
    }

    @Test
    public void testWriteStringNullByteArray() {
        assertThrows(NullPointerException.class, () -> out.writeString((byte[]) null));
        assertEquals(0, baos.toByteArray().length);
    }

    @Test
    public void testWriteNumber() throws Exception {
        out.writeNumber(123456);

        assertEquals("i123456e", baos.toString());
    }

    @Test
    public void testWriteNumberDecimal() throws Exception {
        out.writeNumber(123.456);

        assertEquals("i123e", baos.toString());
    }

    @Test
    public void testWriteNumberNull() {
        assertThrows(NullPointerException.class, () -> out.writeNumber(null));
        assertEquals(0, baos.toByteArray().length);
    }

    @Test
    public void testWriteList() throws Exception {
        out.writeList(List.of(
                "Hello",
                "World!".getBytes(),
                List.of(123, 456),
                "Foo".getBytes()
        ));
        assertEquals("l5:Hello6:World!li123ei456ee3:Fooe", baos.toString());
    }

    @Test
    public void testWriteListEmpty() throws Exception {
        out.writeList(List.of());
        assertEquals("le", baos.toString());
    }

    @Test
    public void testWriteListNullItem() {
        assertThrows(NullPointerException.class, () -> out.writeList(List.of(
                "Hello",
                "World!".getBytes(),
                Arrays.asList(null, 456)
        )));
        assertEquals(0, baos.toByteArray().length);
    }

    @Test
    public void testWriteListNull() {
        assertThrows(NullPointerException.class, () -> out.writeList(null));
        assertEquals(0, baos.toByteArray().length);
    }

    @Test
    public void testWriteDictionary() throws Exception {
        final var map = new LinkedHashMap<>() {{
            put("string", "value");
            put("number", 123456);
            put("list", List.of("list-item-1", "list-item-2"));
            put("dict", new ConcurrentSkipListMap<>() {{
                put(123, "test".getBytes());
                put(456, "thing");
            }});
        }};
        out.writeDictionary(map);
        assertEquals("d4:dictd3:1234:test3:4565:thinge4:listl11:list-item-111:list-item-2e6:numberi123456e6:string5:valuee", baos.toString());
    }

    @Test
    public void testWriteDictionaryEmpty() throws Exception {
        out.writeDictionary(Map.of());
        assertEquals("de", baos.toString());
    }

    @Test
    public void testWriteDictionaryKeyCastException() {
        assertThrows(ClassCastException.class, () -> out.writeDictionary(new TreeMap<>() {{
            put("string", "value");
            put(123, "number-key");
        }}));
        assertEquals(0, baos.toByteArray().length);
    }

    @Test
    public void testWriteDictionaryNull() {
        assertThrows(NullPointerException.class, () -> out.writeDictionary(null));
        assertEquals(0, baos.toByteArray().length);
    }

}
