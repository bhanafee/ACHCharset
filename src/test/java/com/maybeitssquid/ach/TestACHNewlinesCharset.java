package com.maybeitssquid.ach;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.nio.charset.CharsetEncoder;
import java.nio.charset.CoderResult;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.*;

public class TestACHNewlinesCharset extends AbstractCharsetTests {

    @BeforeEach
    public void setupCharset() {
        charset = new TransliteratingASCIIProvider().charsetForName("X-ACH-Newlines");
    }

    @Test
    @Override
    public void testContains() {
        super.testContains();

        assertTrue(charset.contains(Charset.forName("X-ACH-Newlines")));
    }
 }
