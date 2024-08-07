package com.maybeitssquid.ach;

import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.*;
import java.util.Arrays;

public class ASCIISubset extends Charset {

    private static final char UNUSED = '\uFFFF';
    private static final char[] PURE_ASCII = new char[0x80];

    static {
        for (char ch = '\u0000'; ch < '\u0080'; ch++) {
            PURE_ASCII[ch] = ch;
        }
    }

    private final char[] decode;

    /**
     * Initializes a new ASCII subset with the given canonical name and alias set.
     *
     * @param canonicalName The canonical name of this charset
     * @param aliases       An array of this charset's aliases, or null if it has no aliases
     * @param controls      Whether control characters are allowed.
     * @param allowed       Specific control characters that are allowed. If this parameter includes any values then
     *                      all other control characters implicitly are not allowed. This parameter has no effect if
     *                      all controls are allowed.
     */
    protected ASCIISubset(final String canonicalName, final String[] aliases, final boolean controls, final char... allowed) {
        super(canonicalName, aliases);
        if (controls && (allowed == null || allowed.length == 0)) {
            this.decode = PURE_ASCII;
        } else {
            this.decode = Arrays.copyOf(PURE_ASCII, PURE_ASCII.length);
            if (!controls) {
                Arrays.fill(this.decode, 0x00, 0x20, UNUSED);
                this.decode[0x7F] = UNUSED;
            }
            for (final char ch : allowed) {
                if (ch < 0x80) {
                    this.decode[ch] = ch;
                } else {
                    throw new IllegalArgumentException("Non-ASCII character '" + ch + "' cannot be used in ASCII subset");
                }
            }
        }
    }

    @Override
    public boolean contains(final Charset cs) {
        if (cs == this) {
            return true;
        } else if (cs == null) {
            return false;
        } else if (StandardCharsets.US_ASCII.equals(cs)) {
            return this.decode == PURE_ASCII;
        } else if (ASCIISubset.class.equals(cs.getClass())) {
            final ASCIISubset that = (ASCIISubset) cs;
            for (int i = 0; i < this.decode.length; i++) {
                if (this.decode[i] != that.decode[i] && that.decode[i] != UNUSED) {
                    return false;
                }
            }
            return true;
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
                    if (!out.hasRemaining()) {
                        return CoderResult.OVERFLOW;
                    } else {
                        final byte b = in.get();
                        if (b < 0) {
                            in.position(in.position() - 1);
                            return CoderResult.malformedForLength(1);
                        } else {
                            final char ch = decode[Byte.toUnsignedInt(b)];
                            if (ch == UNUSED) {
                                in.position(in.position() - 1);
                                return CoderResult.unmappableForLength(1);
                            } else {
                                out.put(ch);
                            }
                        }
                    }
                }
                return CoderResult.UNDERFLOW;
            }
        };
    }

    @Override
    public CharsetEncoder newEncoder() {
        return new CharsetEncoder(this, 1F, 1F) {
            @Override
            public boolean canEncode(final char c) {
                return c < decode.length && decode[c] == c;
            }

            @Override
            protected CoderResult encodeLoop(final CharBuffer in, final ByteBuffer out) {
                while (in.hasRemaining()) {
                    if (out.hasRemaining()) {
                        char ch = in.get();
                        if (canEncode(ch)) {
                            out.put((byte) ch);
                        } else {
                            in.position(in.position() - 1);
                            return CoderResult.unmappableForLength(1);
                        }
                    } else {
                        return CoderResult.OVERFLOW;
                    }
                }
                return CoderResult.UNDERFLOW;
            }
        };
    }

}
