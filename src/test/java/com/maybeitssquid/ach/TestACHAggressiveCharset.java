package com.maybeitssquid.ach;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.*;

public class TestACHAggressiveCharset extends AbstractCharsetTests {

    final char[] CANNOT_ENCODE =
            {'\u0000', '\u007F', '\u0080', '\u0081', '\u00FD', '\u00FF', '\u0100', '\u0800', '\uFFFF'};

    @BeforeEach
    public void setupCharset() {
        charset = new TransliteratingASCIIProvider().charsetForName("X-ACH-Aggressive");
    }


    @Test
    @Override
    public void testContains() {
        super.testContains();

        assertTrue(charset.contains(Charset.forName("X-ACH-Aggressive")));
    }

    @Test
    @Override
    public void testCannotEncode() {
        testEncoderCannotEncode(new char[] {'\u0000', '\u007F', '\u0080', '\u0081', '\u0800', '\uFFFF'});
    }
}
