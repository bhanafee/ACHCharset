package com.maybeitssquid.ach;

import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

abstract public class AbstractEncoderTests {
    private final Pattern alphanumeric = Pattern.compile("[a-zA-Z0-9]");

    protected Filtering encoder;

    public void dump(int codepoint) {
        final char[] transliterated = encoder.apply(codepoint);
        System.out.printf("%04X (%02d) '%s' to '%s' %s\n",
                codepoint,
                Character.getType(codepoint),
                Character.toString(codepoint),
                new String(transliterated),
                Character.getName(codepoint));
    }

    @SuppressWarnings("unused")
    public void dump(int start, int end) {
        for (int codepoint = start; codepoint <= end; codepoint++) {
            dump(codepoint);
        }
    }

    public void alphanumericSingles(int start, int end, Integer... exceptions) {
        final List<Integer> skip = Arrays.asList(exceptions);
        for (int codepoint = start; codepoint <= end; codepoint++) {
            if (skip.contains(codepoint)) break;
            final char[] transliterated = encoder.apply(codepoint);
            assertTrue(alphanumeric.matcher(String.valueOf(transliterated)).matches(),
                    String.format("%04X (%02d) '%s' to '%s' %s",
                            codepoint,
                            Character.getType(codepoint),
                            Character.toString(codepoint),
                            String.valueOf(transliterated),
                            Character.getName(codepoint)));
        }
    }

    protected void assertNone(final int codepoint) {
        final char[] result = encoder.apply(codepoint);
        assertEquals(0, result.length,
                String.format("Unexpected transliteration for %04X as '%s' %s",
                        codepoint, new String(result), Character.getName(codepoint)));
    }

    protected void assertNone(final int... codepoints) {
        for (int codepoint : codepoints) {
            assertNone(codepoint);
        }
    }

    protected void assertEmptyRange(int start, int end) {
        for (int codepoint = start; codepoint <= end; codepoint++) {
            assertNone(codepoint);
        }
    }

    protected void assertIs(final String expected, final int codepoint) {
        final char[] transliterated = encoder.apply(codepoint);
        assertEquals(expected, new String(transliterated),
                String.format("Incorrect transliteration for %04X as '%s' instead of '%s'",
                        codepoint, new String(transliterated), expected));
    }

    protected void assertAll(final char expected, final IntStream codepoints) {
        for (int codepoint : codepoints.toArray()) {
            final char[] transliterated = encoder.apply(codepoint);
            assertNotNull(transliterated,
                    String.format("Failed to transliterate %04X", codepoint));
            assertEquals(1, transliterated.length,
                    String.format("Wrong length for %04X (%d): %d %s",
                            codepoint, Character.getType(codepoint), transliterated.length, Character.getName(codepoint)));
            assertEquals(expected, transliterated[0],
                    String.format("Wrong transliteration for %04X, was '%c' should be '%c'",
                            codepoint, transliterated[0], expected));
        }
    }

    private void assertPreflight() {
        final char[] before = encoder.apply(0x0020);
        assertNotNull(before);
        assertEquals(1, before.length);
        assertEquals(' ', before[0]);
    }

    private char[] assertPostFlight(final Filtering configured) {
        assertNotNull(configured);

        assertEquals(encoder.getClass(), configured.getClass());
        final char[] after = configured.apply(0x0020);
        assertNotNull(after);
        return after;
    }

    @Test
    public void testEncodeChar() {
        assertPreflight();
        final Filtering result = encoder.encode(0x0020, '$');
        final char[] applied = assertPostFlight(result);
        assertEquals(1, applied.length);
        assertEquals('$', applied[0]);
    }

    @Test
    public void testEncodeCharArray() {
        assertPreflight();
        final Filtering result = encoder.encode(0x0020, new char[] { '$', 'x' });
        final char[] applied = assertPostFlight(result);
        assertEquals(2, applied.length);
        assertEquals('$', applied[0]);
        assertEquals('x', applied[1]);
    }

    @Test
    public void testEncodeString() {
        assertPreflight();
        final Filtering result = encoder.encode(0x0020, "$xyz");
        final char[] applied = assertPostFlight(result);
        assertNotNull(applied);
        assertEquals(4, applied.length);
        assertEquals('$', applied[0]);
        assertEquals('x', applied[1]);
        assertEquals('y', applied[2]);
        assertEquals('z', applied[3]);
    }

    @Test
    public void testBlock() {
        assertPreflight();
        final Filtering result = encoder.block(0x0020);
        final char[] applied = assertPostFlight(result);
        assertNotNull(applied);
        assertEquals(0, applied.length);
    }

    @Test
    public void testBlockControls() {
        assertPreflight();
        final Filtering result = encoder.blockControls();
        final char[] applied = assertPostFlight(result);
        assertEquals(1, applied.length);
        assertEquals(0, result.apply(0x00).length);
        assertEquals(0, result.apply(0x0A).length);
        assertEquals(0, result.apply(0x0D).length);
        assertEquals(0, result.apply(0x1F).length);
        assertEquals(0, result.apply(0x7F).length);
    }

    public void encodeCharHigh() {
        assertPreflight();
        final Filtering result = encoder.encode(0x00A2, 'C').encode(0x00A3, '\u00A4');
        final char[] applied = result.apply(0x00A2);
        assertEquals(1, applied.length);
        assertEquals('C', applied[0]);

        final char[] higher = result.apply(0x00A3);
        assertEquals(1, higher.length);
        assertEquals('\u00A4', higher[0]);

    }

    public void encodeCharArrayHigh() {
        assertPreflight();
        final Filtering result = encoder.encode(0x00A2, new char[]{'C', 'e'});
        final char[] applied = result.apply(0x00A2);
        assertNotNull(applied);
        assertEquals(2, applied.length);
        assertEquals('C', applied[0]);
        assertEquals('e', applied[1]);
    }

    public void encodeStringHigh() {
        assertPreflight();
        final Filtering result = encoder.encode(0x00A2, "$cent");
        final char[] applied = result.apply(0x00A2);
        assertNotNull(applied);
        assertEquals(5, applied.length);
        assertEquals('$', applied[0]);
        assertEquals('c', applied[1]);
        assertEquals('e', applied[2]);
        assertEquals('n', applied[3]);
        assertEquals('t', applied[4]);
    }

    public void blockHigh() {
        assertPreflight();
        final Filtering result = encoder.block(0x00A2);
        final char[] applied = result.apply(0x00A2);
        assertNotNull(applied);
        assertEquals(0, applied.length);
    }
}
