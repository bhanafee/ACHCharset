package com.maybeitssquid.ach;

import java.nio.CharBuffer;
import java.text.Normalizer;
import java.util.HashMap;
import java.util.Map;

public class Normalizing extends Filtering {

    private final Map<Integer, char[]> encodings = new HashMap<>();

    private final Normalizer.Form form;

    public Normalizing(final Normalizer.Form form) {
        this.form = form;
    }

    public Normalizing() {
        this(Normalizer.Form.NFKD);
    }

    @SuppressWarnings("unused")
    public Normalizer.Form getForm() {
        return form;
    }

    @Override
    public Normalizing encode(final int codepoint, final char as) {
        if (codepoint <= 0x0080) {
            super.encode(codepoint, as);
        } else if (as <= 0x0080) {
            encodings.put(codepoint, ASCII[as]);
        } else {
            encodings.put(codepoint, new char[]{as});
        }
        return this;
    }

    @Override
    public Normalizing encode(final int codepoint, final char[] as) {
        if (codepoint <= 0x0080) {
            super.encode(codepoint, as);
        } else {
            encodings.put(codepoint, as);
        }
        return this;
    }

    @Override
    public Normalizing encode(final int codepoint, final String as) {
        super.encode(codepoint, as);
        return this;
    }

    @Override
    public Normalizing block(final int codepoint) {
        if (codepoint > 0x0080) {
            encodings.remove(codepoint);
        } else {
            super.block(codepoint);
        }
        return this;
    }

    @Override
    public Normalizing blockControls() {
        super.blockControls();
        return this;
    }

    @Override
    public char[] apply(final int value) {
        if (value < 0x80) {
            return ASCII[value];
        } else if (this.encodings.containsKey(value)) {
            return this.encodings.get(value);
        } else {
            final String normalized = Normalizer.normalize(Character.toString(value), this.form);
            final CharBuffer buffer = CharBuffer.allocate(20);
            normalized.codePoints().forEach(i -> buffer.put(dispatch(i)));
            final char[] result;
            switch (buffer.position()) {
                case 0:
                    result = NOTHING;
                    break;
                case 1:
                    result = ASCII[buffer.get(0)];
                    break;
                default:
                    result = new char[buffer.position()];
                    buffer.position(0);
                    buffer.get(result);
            }
            this.encodings.put(value, result);
            return result;
        }
    }

    protected char[] dispatch(final int codepoint) {
        if (codepoint < 0x80) {
            return ASCII[codepoint];
        } else {
            return NOTHING;
        }
    }
}
