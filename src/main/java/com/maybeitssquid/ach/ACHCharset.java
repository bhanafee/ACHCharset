package com.maybeitssquid.ach;

import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CharsetEncoder;
import java.nio.charset.CoderResult;
import java.util.Arrays;

/**
 * Character set that allows only the ACH-safe subset of US-ASCII.
 */
public class ACHCharset extends Charset {

    private static final char CANNOT_ENCODE = 0xFFFF;
    private static final char[] ACH = new char[256];

    static {
        Arrays.fill(ACH, CANNOT_ENCODE);
        for (int i = 32; i < 127; i++) ACH[i] = (char) i;
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
            @Override
            protected CoderResult decodeLoop(final ByteBuffer in, final CharBuffer out) {
                while (in.hasRemaining()) {
                    if (!out.hasRemaining()) return CoderResult.OVERFLOW;
                    byte b = in.get();
                    char c = ACH[Byte.toUnsignedInt(b)];
                    if (c != CANNOT_ENCODE) {
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
                return c < ACH.length && ACH[c] != CANNOT_ENCODE;
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
