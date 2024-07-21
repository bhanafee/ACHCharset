package com.maybeitssquid.ach;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.*;

import static org.junit.jupiter.api.Assertions.*;

public class TestACHCharset {

    private Charset test;

    private final CharBuffer chars = CharBuffer.allocate(1024);
    private final ByteBuffer bytes = ByteBuffer.allocate(1024);

    private final byte A = 0x41;
    private final byte B = 0x42;
    private final byte C = 0x43;
    private final byte D = 0x44;
    private final byte[] ABCD = new byte[] {A, B, C, D};

    @BeforeEach
    public void setup() {
        test = new ACHCharset();
        chars.clear();
        bytes.clear();
    }

    @Test
    public void testContains() {
        assertFalse(test.contains(StandardCharsets.US_ASCII));
        assertFalse(test.contains(StandardCharsets.ISO_8859_1));
        assertFalse(test.contains(StandardCharsets.UTF_8));
        assertFalse(test.contains(null));

        assertTrue(test.contains(test));
        assertTrue(test.contains(new ACHCharset()));
    }

    /** Test the convenience wrapper using the decoder. */
    @Test
    public void testDecode() {
        bytes.put(ABCD);
        bytes.rewind();
        final CharBuffer output = test.decode(bytes);
        assertNotNull(output);
        assertEquals('A', output.get());
        assertEquals('B', output.get());
        assertEquals('C', output.get());
        assertEquals('D', output.get());
    }

    /* Test the decoder directly. */

    @Test
    public void testDecoderUnderflow() {
        final CharsetDecoder decoder = test.newDecoder();
        assertNotNull(decoder);

        bytes.put(ABCD);
        bytes.limit(3);
        bytes.rewind();

        CoderResult result = decoder.decode(bytes, chars, true);
        assertTrue(result.isUnderflow());

        assertEquals(3, bytes.position());
        assertEquals(3, chars.position());

        chars.rewind();
        assertEquals('A', chars.get());
        assertEquals('B', chars.get());
        assertEquals('C', chars.get());
    }

    @Test
    public void testDecoderOverflow() {
        final CharsetDecoder decoder = test.newDecoder();
        assertNotNull(decoder);

        bytes.put(ABCD);
        bytes.limit(3);
        bytes.rewind();

        chars.limit(2);

        CoderResult result = decoder.decode(bytes, chars, true);
        assertTrue(result.isOverflow());

        assertEquals(2, bytes.position());
        assertEquals(2, chars.position());

        chars.rewind();
        assertEquals('A', chars.get());
        assertEquals('B', chars.get());
    }

    @Test
    public void testDecoderMalformedInput() {
        final CharsetDecoder decoder = test.newDecoder();
        assertNotNull(decoder);

        bytes.put(new byte[] {A, 2, B, -128, C, -1, D});
        bytes.limit(7);
        bytes.rewind();

        CoderResult result;
        for (int i = 0; i < 3; i++) {
            result = decoder.decode(bytes, chars, false);
            assertTrue(result.isMalformed());
            assertEquals(1, result.length());
            bytes.position(bytes.position() + 1);
        }
        result = decoder.decode(bytes, chars, true);
        assertTrue(result.isUnderflow());

        assertEquals(7, bytes.position());
        assertEquals(4, chars.position());

        final char[] output = new char[4];
        chars.rewind();
        chars.get(output);
        assertArrayEquals("ABCD".toCharArray(), output);
    }


    /** Test the convenience wrapper using the encoder. */
    @Test
    public void testEncode() {
        final ByteBuffer simple = test.encode("ABC");
        assertNotNull(simple);
        assertEquals(A, simple.get());
        assertEquals(B, simple.get());
        assertEquals(C, simple.get());
    }


    /** Test the encoder directly. */
    @Test
    public void testEncoderCanEncode() {
        final CharsetEncoder encoder = test.newEncoder();
        assertNotNull(encoder);

        assertTrue(encoder.canEncode('A'));
        assertFalse(encoder.canEncode((char) 0));
        assertFalse(encoder.canEncode((char) 127));
        assertFalse(encoder.canEncode((char) 128));
        assertFalse(encoder.canEncode((char) 129));
        assertFalse(encoder.canEncode((char) 254));
        assertFalse(encoder.canEncode((char) 255));
        assertFalse(encoder.canEncode((char) 257));
    }

    /* Test the encoder directly. */

    @Test
    public void testEncoderUnderflow() {
        final CharsetEncoder encoder = test.newEncoder();
        assertNotNull(encoder);

        chars.put("ABC");
        chars.limit(3);
        chars.rewind();

        CoderResult result = encoder.encode(chars, bytes, true);
        assertTrue(result.isUnderflow());

        assertEquals(3, chars.position());
        assertEquals(3, bytes.position());

        bytes.rewind();
        assertEquals(A, bytes.get());
        assertEquals(B, bytes.get());
        assertEquals(C, bytes.get());
    }

    @Test
    public void testEncoderOverflow() {
        final CharsetEncoder encoder = test.newEncoder();
        assertNotNull(encoder);

        chars.put("ABC");
        chars.limit(3);
        chars.rewind();

        bytes.limit(2);

        CoderResult result = encoder.encode(chars, bytes, true);
        assertTrue(result.isOverflow());

        assertEquals(2, chars.position());
        assertEquals(2, bytes.position());

        bytes.rewind();
        assertEquals(A, bytes.get());
        assertEquals(B, bytes.get());
    }

    @Test
    public void testEncoderUnmappable() {
        final CharsetEncoder encoder = test.newEncoder();
        assertNotNull(encoder);

        chars.put(new char[] {'A', 0x0002, 'B', 0x0080, 'C', 0x0100, 'D'});
        chars.limit(7);
        chars.rewind();

        CoderResult result;
        for (int i = 0; i < 3; i++) {
            result = encoder.encode(chars, bytes, false);
            assertTrue(result.isUnmappable());
            assertEquals(1, result.length());
            chars.position(chars.position() + 1);
        }
        result = encoder.encode(chars, bytes, true);
        assertTrue(result.isUnderflow());

        assertEquals(7, chars.position());
        assertEquals(4, bytes.position());

        final byte[] output = new byte[4];
        bytes.rewind();
        bytes.get(output);
        assertArrayEquals(new byte[] {A, B, C, D}, output);
    }
}
