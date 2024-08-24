package com.maybeitssquid.ach;

import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.nio.charset.CharsetEncoder;
import java.nio.charset.CoderResult;
import java.util.function.IntFunction;

public class TransliteratingEncoder extends CharsetEncoder {

    private final IntFunction<char[]> transliterator = new Transliterating();

    protected TransliteratingEncoder(final Charset cs) {
        super(cs, 1F, 11F, new byte[]{'?'});
    }

    @Override
    protected CoderResult encodeLoop(CharBuffer in, ByteBuffer out) {
        while (in.hasRemaining()) {
            final int codepoint = Character.codePointAt(in, 0);
            final int length = Character.isSupplementaryCodePoint(codepoint) ? 2 : 1;

            final char[] transliterated = this.transliterator.apply(codepoint);
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

}
