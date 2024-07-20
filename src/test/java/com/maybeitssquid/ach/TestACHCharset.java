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
    private final byte[] ABC = new byte[] {A, B, C};

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
        bytes.put(ABC);
        bytes.rewind();
        final CharBuffer output = test.decode(bytes);
        assertNotNull(output);
        assertEquals('A', output.get());
        assertEquals('B', output.get());
        assertEquals('C', output.get());
    }

    /* Test the decoder directly. */

    @Test
    public void testDecoderUnderflow() {
        final CharsetDecoder decoder = test.newDecoder();
        assertNotNull(decoder);

        bytes.put(ABC);
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

        bytes.put(ABC);
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

        bytes.put(new byte[] {A, B, 0x02, C});
        bytes.rewind();

        CoderResult result = decoder.decode(bytes, chars, false);
        assertTrue(result.isMalformed());
        assertEquals(1, result.length());

        assertEquals(2, bytes.position());
        assertEquals(2, chars.position());

        chars.rewind();
        assertEquals('A', chars.get());
        assertEquals('B', chars.get());
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

        chars.put(new char[] {'A', 'B', 0x02, 'C'});
        chars.rewind();

        CoderResult result = encoder.encode(chars, bytes, false);
        assertTrue(result.isUnmappable());
        assertEquals(1, result.length());

        assertEquals(2, chars.position());
        assertEquals(2, bytes.position());

        bytes.rewind();
        assertEquals(0x41, bytes.get());
        assertEquals(0x42, bytes.get());
    }
}
