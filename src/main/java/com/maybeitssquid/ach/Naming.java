package com.maybeitssquid.ach;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Function to convert a Unicode codepoint into a char[], where all the characters are in the ASCII range 0x00 to 0x7F.
 */
public class Naming extends Categorizing {

    private static final Pattern latin = Pattern.compile("LATIN (SMALL |CAPITAL )?LETTER ([A-Z]+ )*(?<letter>\\p{Upper}\\p{Upper}?)\\b");

    public Naming() {
        encode(0x00B4, "");
        encode(0x00B7, '.');
        encode(0x00BC, "1/4");
        encode(0x00BD, "1/2");
        encode(0x00BE, "3/4");
        encode(0x00D7, '*');
        encode(0x00F7, '/');
        encode(0x0138, 'q');
        encode(0x014A, "NG");
        encode(0x014B, "ng");
        encode(0x0152, "OE");
        encode(0x0153, "oe");
        encode(0x01A6, 'z');
        encode(0x0259, 'e');
        encode(0x025A, 'e');
        encode(0x02B9, '\'');
        encode(0x02BA, '"');
        encode(0x02EE, '"');
        encode(0x066B, ',');
        encode(0x066D, '*');
        encode(0x01C3, '!');
        encode(0x1D01, "AE");
        encode(0x1D15, "OU");
        encode(0x1D2F, 'B');
        encode(0x1D3B, 'N');
        encode(0x1D4A, 'e');
        encode(0x1D4E, 'i');
        encode(0x1D7E, 'u');
        encode(0x1D95, 'e');
        encode(0x1DA7, 'i');
        encode(0x1EFA, "LL");
        encode(0x1EFB, "ll");
        encode(0x1EFC, 'V');
        encode(0x1EFD, 'v');
        encode(0x2032, '\'');
        encode(0x2033, '"');
        encode(0x2035, '`');
        encode(0x2036, '"');
        encode(0x2038, '^');
        encode(0x203B, '*');
        encode(0x203D, "!?");
        encode(0x2042, '*');
        encode(0x204A, '&');
        encode(0x2053, '~');
        encode(0x205A, ':');
        encode(0x207B, '-');
        encode(0x208B, '-');
        encode(0x2044, '/');
        encode(0x2114, '#');
        encode(0x2215, '/');
        encode(0x2236, ':');
        encode(0x2317, '#');
        encode(0x266D, 'b');
        encode(0x266F, '#');
        encode(0x26A0, '!');
        encode(0x26B9, '*');
        encode(0x27CB, '/');
        encode(0x27CD, '\\');
        encode(0x2C7B, 'E');
        encode(0x3003, '"');
        encode(0xA730, 'F');
        encode(0xA731, 'S');
        encode(0xA7AF, 'Q');
        encode(0xAB37, 'l');
        encode(0xAB46, 'R');
        encode(0xAB5D, 'l');
        encode(0xAB67, "tx");
    }

    @Override
    public Naming encode(final int codepoint, final char as) {
        super.encode(codepoint, as);
        return this;
    }

    @Override
    public Naming encode(final int codepoint, final char[] as) {
        super.encode(codepoint, as);
        return this;
    }

    @Override
    public Naming encode(final int codepoint, final String as) {
        super.encode(codepoint, as);
        return this;
    }

    @Override
    public Categorizing block(final int codepoint) {
        super.block(codepoint);
        return this;
    }

    @Override
    public Naming blockControls() {
        super.blockControls();
        return this;
    }

    protected char[] byName(final int codepoint) {
        final String name = Character.getName(codepoint);
        if (name.contains("EXCLAMATION MARK")) {
            return ASCII['!'];
        } else if (name.contains("QUESTION MARK")) {
            return ASCII['?'];
        } else if (name.contains("SEMICOLON")) {
            return ASCII[';'];
        } else if (name.contains("COMMA")) {
            return ASCII[','];
        } else if (name.contains("COLON")) {
            return ASCII[':'];
        } else if (name.contains("TILDE")) {
            return ASCII['~'];
        } else if (name.contains("PLUS SIGN")) {
            return ASCII['+'];
        } else if (name.contains("EQUALS SIGN")) {
            return ASCII['='];
        } else if (name.contains("REVERSE SOLIDUS")) {
            return ASCII['\\'];
        } else if (name.contains("SOLIDUS")) {
            return ASCII['/'];
        } else if (name.contains("ASTERISK")) {
            return ASCII['*'];
        } else if (name.contains("PERCENT SIGN")) {
            return ASCII['%'];
        } else if (name.contains("AMPERSAND")) {
            return ASCII['&'];
        } else if (name.contains("FULL STOP")) {
            return ASCII['.'];
        } else if (name.contains("APOSTROPHE")) {
            return ASCII['\''];
        } else {
            return NOTHING;
        }
    }

    private String letter(final int codepoint) {
        final String name = Character.getName(codepoint);
        final Matcher m = latin.matcher(name);
        return m.find() ? m.group("letter") : "";
    }

    protected char[] uppercase(final int codepoint) {
        final String letter = letter(codepoint);
        switch (letter.length()) {
            case 0:
                return NOTHING;
            case 1:
                return ASCII[letter.charAt(0)];
            default:
                return letter.toCharArray();
        }
    }

    protected char[] lowercase(final int codepoint) {
        final String letter = letter(codepoint);
        switch (letter.length()) {
            case 0:
                return NOTHING;
            case 1:
                return ASCII[Character.toLowerCase(letter.charAt(0))];
            default:
                return letter.toLowerCase().toCharArray();
        }
    }

    protected char[] modifierLetter(final int codepoint) {
        return byName(codepoint);
    }

    protected char[] startPunctuation(final int codepoint) {
        final String name = Character.getName(codepoint);
        if (name.contains("SQUARE BRACKET")) {
            return ASCII['['];
        } else if (name.contains("CURLY BRACKET")) {
            return ASCII['{'];
        } else {
            return super.startPunctuation(codepoint);
        }
    }

    protected char[] endPunctuation(final int codepoint) {
        final String name = Character.getName(codepoint);
        if (name.contains("SQUARE BRACKET")) {
            return ASCII[']'];
        } else if (name.contains("CURLY BRACKET")) {
            return ASCII['}'];
        } else {
            return super.endPunctuation(codepoint);
        }
    }

    protected char[] otherPunctuation(final int codepoint) {
        return byName(codepoint);
    }

    protected char[] modifierSymbol(final int codepoint) {
        return byName(codepoint);
    }

    protected char[] mathSymbol(final int codepoint) {
        return byName(codepoint);
    }

    protected char[] otherSymbol(final int codepoint) {
        return codepoint == UNICODE_REPLACEMENT ? ASCII['?'] : byName(codepoint);
    }

    protected char[] quotePunctuation(final int codepoint) {
        final String name = Character.getName(codepoint);
        if (name.contains("SINGLE")) {
            return ASCII['\''];
        } else {
            return super.quotePunctuation(codepoint);
        }
    }
}
