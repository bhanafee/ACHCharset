package com.maybeitssquid.ach;

import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.*;
import java.util.function.IntFunction;

public class TransliteratingASCII extends Charset {

    private final IntFunction<char[]> transliterator;

    /**
     * Initializes a new charset with the given canonical name and alias
     * set.
     *
     * @param canonicalName  The canonical name of this charset
     * @param aliases        An array of this charset's aliases, or null if it has no aliases
     * @param transliterator The function to convert a code point into zero or more characters
     */
    protected TransliteratingASCII(final String canonicalName, final String[] aliases, final IntFunction<char[]> transliterator) {
        super(canonicalName, aliases);
        this.transliterator = transliterator;
    }

    public boolean containsASCII() {
        for (char ch = 0; ch < 0x0080; ch++) {
            char[] encoding = transliterator.apply(ch);
            if (encoding == null || encoding.length != 1 || encoding[0] != ch) {
                return false;
            }
        }
        return true;
    }

    @Override
    public boolean contains(final Charset cs) {
        if (this.equals(cs)) {
            return true;
        } else if (cs == null) {
            return false;
        } else if (StandardCharsets.US_ASCII.equals(cs)) {
            return containsASCII();
        } else {
            return false;
        }
    }

    @Override
    public CharsetDecoder newDecoder() {
        return new CharsetDecoder(this, 1F, 1F) {
            @Override
            protected CoderResult decodeLoop(final ByteBuffer in, final CharBuffer out) {
                while (in.hasRemaining()) {
                    final byte b = in.get(in.position());
                    if (b >= 0) {
                        final char[] transliterated = transliterator.apply(b);
                        if (transliterated == null || transliterated.length == 0) {
                            return CoderResult.unmappableForLength(1);
                        } else if (transliterated.length <= out.remaining()) {
                            in.position(in.position() + 1);
                            out.put(transliterated);
                        } else {
                            return CoderResult.OVERFLOW;
                        }
                    } else {
                        return CoderResult.malformedForLength(1);
                    }
                }
                return CoderResult.UNDERFLOW;
            }
        };
    }

    @Override
    public CharsetEncoder newEncoder() {
        return new CharsetEncoder(this, 1F, 1F, new byte[]{(byte) '?'}) {
            @Override
            protected CoderResult encodeLoop(final CharBuffer in, final ByteBuffer out) {
                while (in.hasRemaining()) {
                    final int codepoint = Character.codePointAt(in, 0);
                    final int length = Character.isSupplementaryCodePoint(codepoint) ? 2 : 1;

                    final char[] transliterated = transliterator.apply(codepoint);
                    if (transliterated.length == 0) {
                        return CoderResult.unmappableForLength(length);
                    } else if (transliterated.length > out.remaining()) {
                        return CoderResult.OVERFLOW;
                    } else {
                        final int mark = out.position();
                        for (final char c : transliterated) {
                            if (c > 0x007F) {
                                out.position(mark);
                                return CoderResult.unmappableForLength(length);
                            } else {
                                out.put((byte) c);
                            }
                        }
                        in.position(in.position() + length);
                    }
                }
                return CoderResult.UNDERFLOW;
            }
        };
    }
}
