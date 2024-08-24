package com.maybeitssquid.ach;

import java.text.Normalizer.Form;

import static java.lang.Character.*;

public class Categorizing extends Normalizing {

    @SuppressWarnings("unused")
    public Categorizing(final Form form) {
        super(form);
    }

    public Categorizing() {
        super();
    }

    @Override
    public Categorizing encode(final int codepoint, final char as) {
        super.encode(codepoint, as);
        return this;
    }

    @Override
    public Categorizing encode(final int codepoint, final char[] as) {
        super.encode(codepoint, as);
        return this;
    }

    @Override
    public Categorizing encode(final int codepoint, final String as) {
        super.encode(codepoint, as);
        return this;
    }

    @Override
    public Categorizing block(final int codepoint) {
        super.block(codepoint);
        return this;
    }

    @Override
    protected char[] dispatch(final int codepoint) {
        if (codepoint < 0x80) {
            return ASCII[codepoint];
        } else {
            switch (Character.getType(codepoint)) {
                case UPPERCASE_LETTER:
                    return uppercase(codepoint);
                case LOWERCASE_LETTER:
                    return lowercase(codepoint);
                case MODIFIER_LETTER:
                    return modifierLetter(codepoint);
                case DECIMAL_DIGIT_NUMBER:
                    return decimalDigit(codepoint);
                case SPACE_SEPARATOR:
                    return spaceSeparator(codepoint);
                case LINE_SEPARATOR:
                    return lineSeparator(codepoint);
                case PARAGRAPH_SEPARATOR:
                    return paragraphSeparator(codepoint);
                case CONTROL:
                    return control(codepoint);
                case DASH_PUNCTUATION:
                    return dashPunctuation(codepoint);
                case START_PUNCTUATION:
                    return startPunctuation(codepoint);
                case END_PUNCTUATION:
                    return endPunctuation(codepoint);
                case CONNECTOR_PUNCTUATION:
                    return connectorPunctuation(codepoint);
                case OTHER_PUNCTUATION:
                    return otherPunctuation(codepoint);
                case MATH_SYMBOL:
                    return mathSymbol(codepoint);
                case MODIFIER_SYMBOL:
                    return modifierSymbol(codepoint);
                case OTHER_SYMBOL:
                    return otherSymbol(codepoint);
                case INITIAL_QUOTE_PUNCTUATION:
                case FINAL_QUOTE_PUNCTUATION:
                    return quotePunctuation(codepoint);
                default:
                    return NOTHING;
            }
        }
    }

    protected char[] uppercase(@SuppressWarnings("unused") final int codepoint) {
        if (codepoint < 0x80 && Character.isUpperCase(codepoint)) {
            return ASCII[codepoint];
        } else {
            return NOTHING;
        }
    }

    protected char[] lowercase(@SuppressWarnings("unused") final int codepoint) {
        if (codepoint < 0x80 && Character.isLowerCase(codepoint)) {
            return ASCII[codepoint];
        } else {
            return NOTHING;
        }
    }

    protected char[] modifierLetter(@SuppressWarnings("unused") final int codepoint) {
        return NOTHING;
    }

    protected char[] decimalDigit(final int codepoint) {
        final int value = Character.getNumericValue(codepoint);
        if (value >= 0) {
            return ASCII['0' + Character.getNumericValue(codepoint)];
        } else {
            return NOTHING;
        }
    }

    protected char[] spaceSeparator(@SuppressWarnings("unused") final int codepoint) {
        return ASCII[' '];
    }

    protected char[] lineSeparator(@SuppressWarnings("unused") final int codepoint) {
        return newLine();
    }

    protected char[] paragraphSeparator(@SuppressWarnings("unused") final int codepoint) {
        return newLine();
    }

    protected char[] control(@SuppressWarnings("unused") final int codepoint) {
        return NOTHING;
    }

    protected char[] dashPunctuation(@SuppressWarnings("unused") final int codepoint) {
        return ASCII['-'];
    }

    protected char[] startPunctuation(@SuppressWarnings("unused") final int codepoint) {
        return ASCII['('];
    }

    protected char[] endPunctuation(@SuppressWarnings("unused") final int codepoint) {
        return ASCII[')'];
    }

    protected char[] connectorPunctuation(@SuppressWarnings("unused") final int codepoint) {
        return ASCII['_'];
    }

    protected char[] otherPunctuation(@SuppressWarnings("unused") final int codepoint) {
        return NOTHING;
    }

    protected char[] modifierSymbol(@SuppressWarnings("unused") final int codepoint) {
        return NOTHING;
    }

    protected char[] mathSymbol(@SuppressWarnings("unused") final int codepoint) {
        return NOTHING;
    }

    protected char[] otherSymbol(final int codepoint) {
        return NOTHING;
    }

    protected char[] quotePunctuation(@SuppressWarnings("unused") final int codepoint) {
        return ASCII['"'];
    }
}
