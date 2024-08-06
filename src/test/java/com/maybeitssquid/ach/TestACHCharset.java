package com.maybeitssquid.ach;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.*;

import static org.junit.jupiter.api.Assertions.*;

public class TestACHCharset {

    private Charset ACH;

    private final CharBuffer chars = CharBuffer.allocate(1024);
    private final ByteBuffer bytes = ByteBuffer.allocate(1024);

    private final byte LF = 0x0A;
    private final byte CR = 0x0D;

    private final byte A = 0x41;
    private final byte B = 0x42;
    private final byte C = 0x43;
    private final byte D = 0x44;
    private final byte[] ABCD = new byte[] {A, B, C, D};

    @BeforeEach
    public void setup() {
        ACH = new ACHCharset();
        chars.clear();
        bytes.clear();
    }

    @Test
    public void testContains() {
        assertFalse(ACH.contains(StandardCharsets.US_ASCII));
        assertFalse(ACH.contains(StandardCharsets.ISO_8859_1));
        assertFalse(ACH.contains(StandardCharsets.UTF_8));
        assertFalse(ACH.contains(null));

        assertTrue(ACH.contains(ACH));
        assertTrue(ACH.contains(new ACHCharset()));
    }

    /** Test the convenience wrapper using the decoder. */
    @Test
    public void testDecode() {
        bytes.put(ABCD);
        bytes.rewind();
        final CharBuffer output = ACH.decode(bytes);
        assertNotNull(output);
        assertEquals('A', output.get());
        assertEquals('B', output.get());
        assertEquals('C', output.get());
        assertEquals('D', output.get());
    }

    @Test
    public void testDecodeNewlinesAndReplacements() {
        final byte NL = -123;
        final char REPLACEMENT = 0xFFFD;

        bytes.put(new byte[] {A, LF, B, CR, C, NL, D, 127, -1, -2, -128, A});
        bytes.rewind();
        final CharBuffer output = ACH.decode(bytes);
        assertNotNull(output);

        assertEquals('A', output.get());
        assertEquals('\n', output.get());
        assertEquals('B', output.get());
        assertEquals(REPLACEMENT, output.get());
        assertEquals('C', output.get());
        assertEquals(REPLACEMENT, output.get());
        assertEquals('D', output.get());
        assertEquals(REPLACEMENT, output.get());
        assertEquals(REPLACEMENT, output.get());
        assertEquals(REPLACEMENT, output.get());
        assertEquals(REPLACEMENT, output.get());
        assertEquals('A', output.get());
    }

    /* Test the decoder directly. */

    @Test
    public void testDecoderUnderflow() {
        final CharsetDecoder decoder = ACH.newDecoder();
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
        final CharsetDecoder decoder = ACH.newDecoder();
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
        final CharsetDecoder decoder = ACH.newDecoder();
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
        final ByteBuffer simple = ACH.encode("ABC");
        assertNotNull(simple);
        assertEquals(A, simple.get());
        assertEquals(B, simple.get());
        assertEquals(C, simple.get());
    }

    @Test
    public void testEncodeNewlinesAndReplacements() {
        final byte REPLACEMENT = (byte) '?';
        final String testInput = new String(new char[] {'A', LF,'B', CR, 'C', 'D', 0x007F, 0x0080, 0x0081, 0x00FF, 'A'});
        final ByteBuffer simple = ACH.encode(testInput);
        assertNotNull(simple);
        assertEquals(A, simple.get());
        assertEquals(0x0A, simple.get(), "Accept LF");
        assertEquals(B, simple.get());
        assertEquals(REPLACEMENT, simple.get());
        assertEquals(C, simple.get());
        assertEquals(D, simple.get());
        assertEquals(REPLACEMENT, simple.get());
        assertEquals(REPLACEMENT, simple.get());
        assertEquals(REPLACEMENT, simple.get());
        assertEquals(REPLACEMENT, simple.get());
        assertEquals(A, simple.get());
    }

    /** Test the encoder directly. */
    @Test
    public void testEncoderCanEncode() {
        final CharsetEncoder encoder = ACH.newEncoder();
        assertNotNull(encoder);

        assertTrue(encoder.canEncode('A'));
        assertFalse(encoder.canEncode((char) 0));
        assertFalse(encoder.canEncode((char) 0x7F));
        assertFalse(encoder.canEncode((char) 0x80));
        assertFalse(encoder.canEncode((char) 0x81));
        assertFalse(encoder.canEncode((char) 0xFD));
        assertFalse(encoder.canEncode((char) 0xFF));
        assertFalse(encoder.canEncode((char) 0x100));
        assertFalse(encoder.canEncode((char) 0x800));
        assertFalse(encoder.canEncode((char) 0xFFFF));

        // Special cases for newlines
        assertFalse(encoder.canEncode((char) 0x000D), "Cannot encode CR");
        assertTrue(encoder.canEncode((char) 0x000A), "Can encode LF");
    }

    /* Test the encoder directly. */

    @Test
    public void testEncoderUnderflow() {
        final CharsetEncoder encoder = ACH.newEncoder();
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
        final CharsetEncoder encoder = ACH.newEncoder();
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
        final CharsetEncoder encoder = ACH.newEncoder();
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

        // read all characters
        assertEquals(7, chars.position());
        // encoding does not include 3 bad characters
        assertEquals(4, bytes.position());

        final byte[] output = new byte[4];
        bytes.rewind();
        bytes.get(output);
        assertArrayEquals(new byte[] {A, B, C, D}, output);
    }
}
