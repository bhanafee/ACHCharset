package com.maybeitssquid.ach;

import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CharsetEncoder;
import java.nio.charset.CoderResult;
import java.util.Arrays;

/**
 * Character set that allows only the ACH-safe subset of US-ASCII. It allows characters in the range 0x1F to 0x7F,
 * exclusive. In addition, it allows linefeed (0x0A) and silently skips carriage return (0x0D).
 */
public class ACHCharset extends Charset {

    private static final char CANNOT_ENCODE = 0xFFFF;
    private static final char CR = 0x000D;
    private static final byte CR_BYTE = 0x0D;
    private static final char[] ENCODINGS = new char[0x100];

    static {
        Arrays.fill(ENCODINGS, CANNOT_ENCODE);
        for (int i = 0x20; i < 0x7F; i++) ENCODINGS[i] = (char) i;
        // Special case to allow linefeed
        ENCODINGS[0x0A] = 0x000A;
    }

    /**
     * Initializes a new ACH charset.
     */
    public ACHCharset() {
        super("X-ACH", new String[] {"ACH"});
    }

    /**
     * {@inheritDoc}
     *
     * @param cs {@inheritDoc}
     * @return {@inheritDoc}
     */
    @Override
    public boolean contains(final Charset cs) {
        return this.equals(cs);
    }

    /**
     * Constructs a new decoder for ACH allowed characters.
     *
     * @return {@inheritDoc}
     */
    @Override
    public CharsetDecoder newDecoder() {
        return new CharsetDecoder(this, 1F, 1F) {
            private boolean canDecode(final byte b) {
                return b >= 0 && ENCODINGS[Byte.toUnsignedInt(b)] != CANNOT_ENCODE;
            }

            @Override
            protected CoderResult decodeLoop(final ByteBuffer in, final CharBuffer out) {
                while (in.hasRemaining()) {
                    if (!out.hasRemaining()) return CoderResult.OVERFLOW;
                    byte b = in.get();
                    if (canDecode(b)) {
                        char c = ENCODINGS[Byte.toUnsignedInt(b)];
                        out.put(c);
                    } else if (b != CR_BYTE) {
                        in.position(in.position() - 1);
                        return CoderResult.malformedForLength(1);
                    }
                    // Fall through on a CR, so it is consumed but not encoded
                }
                return CoderResult.UNDERFLOW;
            }
        };
    }

    /**
     * Constructs a new encoder for ACH allowed characters.
     *
     * @return {@inheritDoc}
     */
    @Override
    public CharsetEncoder newEncoder() {
        return new CharsetEncoder(this, 1F, 1F) {
            @Override
            public boolean canEncode(final char c) {
                return c < ENCODINGS.length && ENCODINGS[c] != CANNOT_ENCODE;
            }

            @Override
            protected CoderResult encodeLoop(final CharBuffer in, final ByteBuffer out) {
                while (in.hasRemaining()) {
                    if (!out.hasRemaining()) return CoderResult.OVERFLOW;
                    final char c = in.get();
                    if (canEncode(c)) {
                        out.put((byte) ENCODINGS[c]);
                    } else if (c != CR) {
                        in.position(in.position() - 1);
                        return CoderResult.unmappableForLength(1);
                    }
                    // Fall through on a CR, so it is consumed but not encoded
                }
                return CoderResult.UNDERFLOW;
            }
        };
    }
}
