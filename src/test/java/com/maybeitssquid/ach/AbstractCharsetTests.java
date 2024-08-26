package com.maybeitssquid.ach;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

abstract public class AbstractCharsetTests {

    public final char[] CAN_ENCODE = { ' ', '!', '/', '0', '9', ':', '@', 'A', 'Z', '[', '`', 'a', 'z', '{', '~'};

    final char[] CANNOT_ENCODE =
            {'\u0000', '\u007F', '\u0080', '\u0081', '\u00FD', '\u00FF', '\u0100', '\u0800', '\uFFFF'};

    protected Charset charset;

    protected final CharBuffer chars = CharBuffer.allocate(1024);
    protected final ByteBuffer bytes = ByteBuffer.allocate(1024);

    public final byte LF = 0x0A;
    public final byte CR = 0x0D;

    public final byte A = 0x41;
    public final byte B = 0x42;
    public final byte C = 0x43;
    public final byte D = 0x44;
    public final byte[] ABCD = new byte[] {A, B, C, D};


    @BeforeEach
    public void setup() {
        chars.clear();
        bytes.clear();
    }

    @Test
    public void testContains() {
        assertFalse(charset.contains(StandardCharsets.US_ASCII));
        assertFalse(charset.contains(StandardCharsets.ISO_8859_1));
        assertFalse(charset.contains(StandardCharsets.UTF_8));
        assertFalse(charset.contains(null));

        assertTrue(charset.contains(charset));
    }

    /** Test the convenience wrapper using the decoder. */
    @Test
    public void testDecode() {
        bytes.put(ABCD);
        bytes.rewind();
        final CharBuffer output = charset.decode(bytes);
        assertNotNull(output);
        assertEquals('A', output.get());
        assertEquals('B', output.get());
        assertEquals('C', output.get());
        assertEquals('D', output.get());
    }

    /* Test the decoder directly. */

    @Test
    public void testDecoderUnderflow() {
        final CharsetDecoder decoder = charset.newDecoder();
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
        final CharsetDecoder decoder = charset.newDecoder();
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
        final CharsetDecoder decoder = charset.newDecoder();
        assertNotNull(decoder);

        bytes.put(new byte[] {A, 2, B, -128, C, -1, D});
        bytes.limit(7);
        bytes.rewind();

        CoderResult result;

        result = decoder.decode(bytes, chars, false);
        // encodings within ASCII range are merely unmappable
        assertTrue(result.isUnmappable());
        assertEquals(1, result.length());
        bytes.position(bytes.position() + 1);

        result = decoder.decode(bytes, chars, false);
        // encodings outside ASCII range are malformed
        assertTrue(result.isMalformed());
        assertEquals(1, result.length());
        bytes.position(bytes.position() + 1);

        result = decoder.decode(bytes, chars, false);
        // encodings outside ASCII range are malformed
        assertTrue(result.isMalformed());
        assertEquals(1, result.length());
        bytes.position(bytes.position() + 1);

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
        final ByteBuffer simple = charset.encode("ABC");
        assertNotNull(simple);
        assertEquals(A, simple.get());
        assertEquals(B, simple.get());
        assertEquals(C, simple.get());
    }

    @Test
    public void testEncoderOverflow() {
        final CharsetEncoder encoder = charset.newEncoder();
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

    /* Test the encoder directly. */

    @Test
    public void testEncoderUnderflow() {
        final CharsetEncoder encoder = charset.newEncoder();
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
    public void testEncodeNewlinesAndReplacements() {
        final byte REPLACEMENT = (byte) '?';
        final String testInput = new String(new char[]{'A', LF, 'B', CR, 'C', 'D', 0x007F, 0x0080, 0x0081, 'A'});
        final ByteBuffer simple = charset.encode(testInput);
        assertNotNull(simple);
        assertEquals(A, simple.get());
        assertEquals((byte) '\n', simple.get(), "Should encode LF");
        assertEquals(B, simple.get());
        assertEquals((byte) '\r', simple.get(), "Should encode CR");
        assertEquals(C, simple.get());
        assertEquals(D, simple.get());
        assertEquals(REPLACEMENT, simple.get(), "Accepted control (0x7F)");
        assertEquals(REPLACEMENT, simple.get(), "Accepted out of range (0x80)");
        assertEquals(REPLACEMENT, simple.get(), "Accepted out of range (0x81)");
        assertEquals(A, simple.get());
    }

    @Test
    public void testDecodeNewlinesAndReplacements() {
        final byte NL = -123;
        final char REPLACEMENT = 0xFFFD;

        bytes.put(new byte[] {A, LF, B, CR, C, NL, D, 127, -1, -2, -128, A});
        bytes.rewind();
        final CharBuffer output = charset.decode(bytes);
        assertNotNull(output);

        assertEquals('A', output.get());
        assertEquals('\n', output.get(), "Should decode LF");
        assertEquals('B', output.get());
        assertEquals('\r', output.get(), "Should decode CR");
        assertEquals('C', output.get());
        assertEquals(REPLACEMENT, output.get(), "Failed to replace Unicode NEL");
        assertEquals('D', output.get());
        assertEquals(REPLACEMENT, output.get(), "Failed to replace control (127)");
        assertEquals(REPLACEMENT, output.get(), "Failed to replace out of range (-1)");
        assertEquals(REPLACEMENT, output.get(), "Failed to replace out of range (-2)");
        assertEquals(REPLACEMENT, output.get(), "Failed to replace out of range (-128)");
        assertEquals('A', output.get());
    }

    /**
     * Test the encoder directly.
     */
    public void testEncoderCanEncode(char... chars) {
        final CharsetEncoder encoder = charset.newEncoder();
        assertNotNull(encoder);

        for (char c : chars) {
            assertTrue(encoder.canEncode(c), String.format("Claims cannot encode '%c' (%04X)", c, (int) c));
        }
    }
    /**
     * Test the encoder directly.
     */
    public void testEncoderCannotEncode(char... chars) {
        final CharsetEncoder encoder = charset.newEncoder();
        assertNotNull(encoder);

        for (char c : chars) {
            assertFalse(encoder.canEncode(c), String.format("Claims can encode '%c' (%04X)", c, (int) c));
        }
    }

    @Test
    public void testCanEncode() {
        testEncoderCanEncode(CAN_ENCODE);
    }

    @Test
    public void testCannotEncode() {
        testEncoderCannotEncode(CANNOT_ENCODE);
    }
}
