package com.maybeitssquid.ach;

import java.util.function.IntFunction;

/**
 * Function to allow only characters that are in the ASCII subset of Unicode. The input is a Unicode code point,  and
 * the default output is either an array with a single character corresponding to the code point or an empty array
 * if the code point is not in the ASCII range. Encodings for specific ASCII values can be overridden by the
 * {@code encode} functions.
 */
public class ASCIIFilter implements IntFunction<char[]> {
    public static final char[] NOTHING = new char[0];

    public static final char UNICODE_REPLACEMENT = '\uFFFD';

    protected final char[][] ASCII = new char[0x80][];

    public ASCIIFilter() {
        for (char cp = 0; cp < 0x80; cp++) {
            ASCII[cp] = new char[]{cp};
        }
    }

    public ASCIIFilter encode(final int codepoint, final char as) {
        if (codepoint >= 0x80) {
            throw new IllegalArgumentException("Requested encoding of " + codepoint + ", which exceeds 0x80");
        } else {
            this.ASCII[codepoint] = new char[] {as};
        }
        return this;
    }

    public ASCIIFilter encode(final int codepoint, final char[] as) {
        if (codepoint >= 0x80) {
            throw new IllegalArgumentException("Requested encoding of " + codepoint + ", which exceeds 0x80");
        } else {
            this.ASCII[codepoint] = as;
        }
        return this;
    }

    public ASCIIFilter encode(final int codepoint, final String as) {
        return encode(codepoint, as.toCharArray());
    }

    public ASCIIFilter block(final int codepoint) {
        if (codepoint >= 0x80) {
            throw new IllegalArgumentException("Requested blocking of " + codepoint + ", which exceeds 0x80");
        } else {
            this.ASCII[codepoint] = NOTHING;
        }
        return this;
    }

    protected char[] newLine() {
        return ASCII['\n'];
    }

    protected char[] replacement() {
        return ASCII['?'];
    }

    @Override
    public char[] apply(final int value) {
        if (value < 0x80) {
            return ASCII[value];
        } else if (value == UNICODE_REPLACEMENT) {
            return replacement();
        } else {
            return NOTHING;
        }
    }
}
