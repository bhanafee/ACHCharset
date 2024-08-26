package com.maybeitssquid.ach;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;

import static org.junit.jupiter.api.Assertions.*;

public class TestACHFilterCharset extends AbstractCharsetTests {

    @BeforeEach
    public void setupCharset() {
        charset = new TransliteratingASCIIProvider().charsetForName("X-ACH");
    }

    @Test
    @Override
    public void testContains() {
        super.testContains();

        assertTrue(charset.contains(Charset.forName("X-ACH")));
        assertTrue(charset.contains(Charset.forName("ACH")));
    }

    @Test
    public void testDecodeNewlinesAndReplacements() {
        final byte NL = -123;
        final char REPLACEMENT = 0xFFFD;

        bytes.put(new byte[]{A, LF, B, CR, C, NL, D, 127, -1, -2, -128, A});
        bytes.rewind();
        final CharBuffer output = charset.decode(bytes);
        assertNotNull(output);

        assertEquals('A', output.get());
        assertEquals(REPLACEMENT, output.get(), "Failed to replace LF");
        assertEquals('B', output.get());
        assertEquals(REPLACEMENT, output.get(), "Failed to replace CR");
        assertEquals('C', output.get());
        assertEquals(REPLACEMENT, output.get(), "Failed to replace Unicode NEL");
        assertEquals('D', output.get());
        assertEquals(REPLACEMENT, output.get(), "Failed to replace control (127)");
        assertEquals(REPLACEMENT, output.get(), "Failed to replace out of range (-1)");
        assertEquals(REPLACEMENT, output.get(), "Failed to replace out of range (-2)");
        assertEquals(REPLACEMENT, output.get(), "Failed to replace out of range (-128)");
        assertEquals('A', output.get());
    }

    @Test
    public void testEncodeNewlinesAndReplacements() {
        final byte REPLACEMENT = (byte) '?';
        final String testInput = new String(new char[]{'A', LF, 'B', CR, 'C', 'D', 0x007F, 0x0080, 0x0081, 0x00FF, 'A'});
        final ByteBuffer simple = charset.encode(testInput);
        assertNotNull(simple);
        assertEquals(A, simple.get());
        assertEquals(REPLACEMENT, simple.get(), "Accepted LF");
        assertEquals(B, simple.get());
        assertEquals(REPLACEMENT, simple.get(), "Accepted CR");
        assertEquals(C, simple.get());
        assertEquals(D, simple.get());
        assertEquals(REPLACEMENT, simple.get(), "Accepted control (0x7F)");
        assertEquals(REPLACEMENT, simple.get(), "Accepted out of range (0x80)");
        assertEquals(REPLACEMENT, simple.get(), "Accepted out of range (0x81)");
        assertEquals(REPLACEMENT, simple.get(), "Accepted out of range (0xFF)");
        assertEquals(A, simple.get());
    }
}
