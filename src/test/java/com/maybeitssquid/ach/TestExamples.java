package com.maybeitssquid.ach;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CharsetEncoder;
import java.nio.charset.CodingErrorAction;

import static org.junit.jupiter.api.Assertions.*;

public class TestExamples {

    private final ByteArrayInputStream bytesIn = new ByteArrayInputStream(new byte[]{'a', 'b', 'c', 'd', -128, 'e', 'f'});
    private final ByteArrayOutputStream bytesOut = new ByteArrayOutputStream(255);

    final char UNMAPPABLE = 0xF00F;
    final String BAD_STRING = "abcd" + UNMAPPABLE + "ef";

    @BeforeEach
    public void setup() {
        bytesIn.reset();
        bytesOut.reset();
    }

    @Test
    public void testLengthPreservingRead() {
        final char REPLACEMENT = 0xFFFD;
        final String expected = "abcd" + REPLACEMENT + "ef";
        final Writer capture = new StringWriter();

        try {
            final Reader reader = new InputStreamReader(bytesIn, "ACH");

            reader.transferTo(capture);
            assertEquals(expected, capture.toString());
        } catch (final IOException e) {
            fail("Should not throw exception");
        }
    }

    @Test
    public void testFailingRead() {
        final StringWriter capture = new StringWriter();

        final Charset ACH = Charset.forName("ACH");
        final CharsetDecoder decoder = ACH.newDecoder().onMalformedInput(CodingErrorAction.REPORT);
        final Reader reader = new InputStreamReader(bytesIn, decoder);

        try {
            reader.transferTo(capture);
            fail("Expected exception");
        } catch (final IOException e) {
            assertEquals("", capture.toString());
        }
    }

    @Test
    public void testLengthPreservingWrite() {
        try {
            final Writer writer = new OutputStreamWriter(bytesOut, "ACH");

            writer.append(BAD_STRING);
            writer.close();

            final byte[] result = bytesOut.toByteArray();
            assertEquals(7, result.length);
            assertArrayEquals(new byte[]{'a', 'b', 'c', 'd', '?', 'e', 'f'}, result);
        } catch (final IOException e) {
            fail("Should not throw exception");
        }
    }

    @Test
    public void testFailingWrite() {
        try {
            final Charset ACH = Charset.forName("ACH");
            final CharsetEncoder encoder = ACH.newEncoder().onUnmappableCharacter(CodingErrorAction.REPORT);
            final Writer writer = new OutputStreamWriter(bytesOut, encoder);

            writer.append(BAD_STRING);
            fail("Expected exception");
        } catch (final IOException e) {
            assertEquals(0, bytesOut.toByteArray().length);
        }
    }
}
