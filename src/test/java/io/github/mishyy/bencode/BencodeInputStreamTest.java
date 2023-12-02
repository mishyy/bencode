package io.github.mishyy.bencode;

import io.github.mishyy.bencode.type.Type;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.EOFException;
import java.io.InvalidObjectException;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@SuppressWarnings("unchecked")
public final class BencodeInputStreamTest {

    private BencodeInputStream in;

    private void instantiate(final String string) {
        in = new BencodeInputStream(new ByteArrayInputStream(string.getBytes()));
    }

    @Test
    public void testNextTypeString() throws Exception {
        instantiate("7");

        assertEquals(Type.STRING, in.nextType());
        assertEquals(1, in.available());
    }

    @Test
    public void testNextTypeNumber() throws Exception {
        instantiate("i1");

        assertEquals(Type.NUMBER, in.nextType());
        assertEquals(2, in.available());
    }

    @Test
    public void testNextTypeList() throws Exception {
        instantiate("l123");

        assertEquals(Type.LIST, in.nextType());
        assertEquals(4, in.available());
    }

    @Test
    public void testNextTypeDictionary() throws Exception {
        instantiate("dtesting");

        assertEquals(Type.DICTIONARY, in.nextType());
        assertEquals(8, in.available());
    }

    @Test
    public void testNextTypeUnknown() throws Exception {
        instantiate("unknown");

        assertEquals(Type.UNKNOWN, in.nextType());
        assertEquals(7, in.available());
    }

    @Test
    public void testReadString() throws Exception {
        instantiate("12:Hello World!123");

        assertEquals("Hello World!", in.readString());
        assertEquals(3, in.available());
    }

    @Test
    public void testReadStringEmpty() throws Exception {
        instantiate("0:123");

        assertEquals("", in.readString());
        assertEquals(3, in.available());
    }

    @Test
    public void testReadStringNaN() throws Exception {
        instantiate("1c3:Testing");

        assertThrows(InvalidObjectException.class, () -> in.readString());
        assertEquals(10, in.available());
    }

    @Test
    public void testReadStringEOF() throws Exception {
        instantiate("123456");

        assertThrows(EOFException.class, () -> in.readString());
        assertEquals(0, in.available());
    }

    @Test
    public void testReadStringEmptyStream() throws Exception {
        instantiate("");

        assertThrows(EOFException.class, () -> in.readString());
        assertEquals(0, in.available());
    }

    @Test
    public void testReadNumber() throws Exception {
        instantiate("i123456e123");

        assertEquals(123456, in.readNumber().longValue());
        assertEquals(3, in.available());
    }

    @Test
    public void testReadNumberNaN() throws Exception {
        instantiate("i123cbve1");

        assertThrows(NumberFormatException.class, () -> in.readNumber());
        assertEquals(1, in.available());
    }

    @Test
    public void testReadNumberEOF() throws Exception {
        instantiate("i123");

        assertThrows(EOFException.class, () -> in.readNumber());
        assertEquals(0, in.available());
    }

    @Test
    public void testReadNumberEmptyStream() throws Exception {
        instantiate("");

        assertThrows(EOFException.class, () -> in.readNumber());
        assertEquals(0, in.available());
    }

    @Test
    public void testReadNumberScientificNotation() throws Exception {
        instantiate("i-2.9155148901435E+18e");

        assertEquals(-2915514890143500000L, in.readNumber().longValue());
    }

    @Test
    public void testReadList() throws Exception {
        instantiate("l5:Hello6:World!li123ei456eeetesting");

        final var result = (List<Object>) in.readList();

        assertEquals(3, result.size());

        assertEquals("Hello", result.get(0));
        assertEquals("World!", result.get(1));

        final var list = (List<Number>) result.get(2);
        assertEquals(123L, list.get(0).longValue());
        assertEquals(456L, list.get(1).longValue());

        assertEquals(7, in.available());
    }

    @Test
    public void testReadListEmpty() throws Exception {
        instantiate("le123");

        assertTrue(((List<Object>) in.readList()).isEmpty());
        assertEquals(3, in.available());
    }

    @Test
    public void testReadListInvalidItem() throws Exception {
        instantiate("l2:Worlde");

        assertThrows(InvalidObjectException.class, () -> in.readList());
        assertEquals(4, in.available());
    }

    @Test
    public void testReadListEOF() throws Exception {
        instantiate("l5:Hello");

        assertThrows(EOFException.class, () -> in.readList());
        assertEquals(0, in.available());
    }

    @Test
    public void testReadDictionary() throws Exception {
        instantiate("d4:dictd3:1234:test3:4565:thinge4:listl11:list-item-111:list-item-2e6:numberi123456e6:string5:valuee");

        final var result = in.readDictionary();
        assertEquals(4, result.size());

        assertEquals("value", result.get("string"));
        assertEquals(123456L, ((Number) result.get("number")).longValue());

        final var list = (List<Object>) result.get("list");
        assertEquals(2, list.size());
        assertEquals("list-item-1", list.get(0));
        assertEquals("list-item-2", list.get(1));

        final var map = (Map<String, Object>) result.get("dict");
        assertEquals(2, map.size());
        assertEquals("test", map.get("123"));
        assertEquals("thing", map.get("456"));

        assertEquals(0, in.available());
    }

    @Test
    public void testReadDictionaryEmpty() throws Exception {
        instantiate("de123test");

        assertTrue(in.readDictionary().isEmpty());
        assertEquals(7, in.available());
    }

    @Test
    public void testReadDictionaryInvalidItem() throws Exception {
        instantiate("d4:item5:value3:testing");

        assertThrows(InvalidObjectException.class, () -> in.readDictionary());
        assertEquals(4, in.available());
    }

    @Test
    public void testReadDictionaryEOF() throws Exception {
        instantiate("d4:item5:test");

        assertThrows(EOFException.class, () -> in.readDictionary());
        assertEquals(0, in.available());
    }

}
