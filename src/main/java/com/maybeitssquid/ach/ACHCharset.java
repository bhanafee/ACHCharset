package com.maybeitssquid.ach;

import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.*;
import java.util.Arrays;

public class ACHCharset extends Charset {

    private static final char ILLEGAL = '\0';
    private static final char[] ACH = new char[256];

    static {
        Arrays.fill(ACH, ILLEGAL);
        for (int i = 32; i < 127; i++) ACH[i] = (char) i;
    }

    /**
     * Initializes a new ACH charset.
     */
    public ACHCharset() {
        super("X-ACH", new String[] {"ACH"});
    }

    /**
     * @param cs The given charset
     * @return {@inheritDoc}
     * @inheritDoc
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
            @Override
            protected CoderResult decodeLoop(final ByteBuffer in, final CharBuffer out) {
                while (in.hasRemaining()) {
                    if (!out.hasRemaining()) return CoderResult.OVERFLOW;
                    byte b = in.get();
                    char c = ACH[Byte.toUnsignedInt(b)];
                    if (c != ILLEGAL) {
                        out.put(c);
                    } else {
                        in.position(in.position() - 1);
                        return CoderResult.malformedForLength(1);
                    }
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
                return c < ACH.length && ACH[c] != ILLEGAL;
            }

            @Override
            protected CoderResult encodeLoop(final CharBuffer in, final ByteBuffer out) {
                while (in.hasRemaining()) {
                    if (!out.hasRemaining()) return CoderResult.OVERFLOW;
                    final char c = in.get();
                    if (canEncode(c)) {
                        out.put((byte) c);
                    } else {
                        in.position(in.position() - 1);
                        return CoderResult.unmappableForLength(1);
                    }
                }
                return CoderResult.UNDERFLOW;
            }
        };
    }
}
