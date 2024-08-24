package com.maybeitssquid.ach;

import java.util.Arrays;
import java.util.List;
import java.util.function.IntFunction;
import java.util.regex.Pattern;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

abstract public class AbstractEncoderTests {
    private final Pattern alphanumeric = Pattern.compile("[a-zA-Z0-9]");

    protected IntFunction<char[]> encoder;

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

}
