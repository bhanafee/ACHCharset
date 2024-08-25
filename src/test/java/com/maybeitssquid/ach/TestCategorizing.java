package com.maybeitssquid.ach;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class TestCategorizing extends TestNormalizing {
    @BeforeEach
    protected void setUp() {
        this.encoder = new Categorizing();
    }

    @Test
    @Override
    public void testDecimalDigits() {
        super.testDecimalDigits();
        testDecimalDigits(0x0660); // Arabic
        testDecimalDigits(0x06F0); // Extended Arabic
        testDecimalDigits(0x0E50); // Thai
        testDecimalDigits(0x1BB0); // Sudanese
        testDecimalDigits(0x0660); // Arabic
        testDecimalDigits(0x10D30); //Hanifi Rohingya
    }

    @Test
    @Override
    public void testLineSeparator() {
        super.testLineSeparator();
        assertIs("\n", 0x2028);
    }

    @Test
    @Override
    public void testDashPunctuation() {
        final String dash = "-\u002D\u058A\u05BE\u1400\u1806\u2010\u2011\u2012\u2013\u2014\u2015\u2E17\u2E1A" +
                "\u2E3B\u2E40\u301C\u3030\u30A0\uFE31\uFE32\uFE58\uFE63\uFF0D";
        assertAll('-', dash.codePoints());
        assertIs("-", 0x10EAD);
    }

    @Test
    @Override
    public void testStartPunctuation() {
        final String parenthesis = "(\u0028\u0F3A\u0F3C\u169B\u201A\u201E\u2045\u207D\u208D\u2308\u230A" +
                "\u2329\u2774\u27E6\u2983\u298B\u298D\u298F\uFF62";
        assertAll('(', parenthesis.codePoints());
    }

    @Test
    @Override
    public void testEndPunctuation() {
        final String parenthesis = ")\u0029\u0F3B\u0F3D\u169C\u2046\u207E\u208E\u2309\u230B\u232A\u2775" +
                "\u27E7\u2984\u298C\u298E\u2990\uFF63";
        assertAll(')', parenthesis.codePoints());
    }

    @Test
    @Override
    public void testQuotePunctuation() {
        final String quote = "\u00AB\u2019\u201B\u201C\u201F\u2039\u203A\u2E02\u2E04\u2E09\u2E0C\u2E1C\u2E20" +
                "\u00BB\u201D\u2E03\u2E05\u2E0A\u2E0D\u2E1D\u2E21";
        assertAll('"', quote.codePoints());
    }

    @Test
    @Override
    public void testSpaceSeparator() {
        final String spaces = " \u0020\u00A0\u1680\u2000\u2001\u2002\u2003\u2004\u2005\u2006\u2007\u2008\u2009" +
                "\u200A\u202F\u205F\u3000";
        assertAll(' ', spaces.codePoints());
    }

    @Test
    @Override
    public void testParagraphSeparator() {
        final String paragraph = "\u2028\u2029";
        assertAll('\n', paragraph.codePoints());
    }

    @Test
    @Override
    public void testConnectorPunctuation() {
        final String connector = "_\u005F\u203F\u2040\u2054\uFE33\uFE34\uFE4D\uFE4E\uFE4F\uFF3F";
        assertAll('_', connector.codePoints());
    }

    @Test
    @Override
    public void testOtherPunctuation() {
        super.testOtherPunctuation();
        assertIs("?", 0xFFFD);
    }

    @Test
    @Override
    public void testHalfWidthAndFullwidth() {
        assertNone(0xFF00);
        for (int cp = 0xFF01; cp <= 0xFF60; cp++) {
            // fullwidth ASCII
            assertEquals(1, this.encoder.apply(cp).length);
        }
        // Based on category
        assertNone(0xFF61);
        assertIs("(", 0xFF62);
        assertIs(")", 0xFF63);
        assertNone(0xFF64);
        assertEmptyRange(0xFF65, 0xFFE2);
        // Based on category
        assertIs(" ", 0xFFE3);
        assertEmptyRange(0xFFE4, 0xFFEF);
    }
}
