package com.maybeitssquid.ach;

import java.nio.BufferOverflowException;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.*;
import java.text.Normalizer;
import java.util.HashMap;
import java.util.Map;

import static java.lang.Character.*;

@SuppressWarnings("unused")
public class Transliterate extends CharsetEncoder {

    public static final byte SPACE = (byte) ' ';
    public static final byte EXCLAMATION_MARK = (byte) '!';
    public static final byte QUOTATION_MARK = (byte) '"';
    public static final byte NUMBER_SIGN = (byte) '#';
    public static final byte DOLLAR_SIGN = (byte) '$';
    public static final byte PERCENT_SIGN = (byte) '%';
    public static final byte AMPERSAND = (byte) '&';
    public static final byte APOSTROPHE = (byte) '\'';
    public static final byte BACKTICK = (byte) '`';
    public static final byte LEFT_PARENTHESIS = (byte) '(';
    public static final byte RIGHT_PARENTHESIS = (byte) ')';
    public static final byte LEFT_SQUARE_BRACKET = (byte) '[';
    public static final byte RIGHT_SQUARE_BRACKET = (byte) ']';
    public static final byte LEFT_CURLY_BRACKET = (byte) '{';
    public static final byte RIGHT_CURLY_BRACKET = (byte) '}';
    public static final byte ASTERISK = (byte) '*';
    public static final byte PLUS_SIGN = (byte) '+';
    public static final byte COMMA = (byte) ',';
    public static final byte HYPHEN_MINUS = (byte) '-';
    public static final byte FULL_STOP = (byte) '.';
    public static final byte SOLIDUS = (byte) '/';
    public static final byte REVERSE_SOLIDUS = (byte) '\\';
    public static final byte COLON = (byte) ':';
    public static final byte SEMICOLON = (byte) ';';
    public static final byte EQUALS = (byte) '=';
    public static final byte QUESTION_MARK = (byte) '?';
    public static final byte COMMERCIAL_AT = (byte) '@';
    public static final byte CIRCUMFLEX_ACCENT = (byte) '^';
    public static final byte LOW_LINE = (byte) '_';
    public static final byte VERTICAL_LINE = (byte) '|';
    public static final byte TILDE = (byte) '~';

    private static final int MONGOLIAN_ELLIPSIS = '\u1801';
    private static final int HORIZONTAL_ELLIPSIS = '\u2026';
    private static final int VERTICAL_ELLIPSIS = '\uFE19';
    private static final int UNICODE_REPLACEMENT = '\uFFFD';

    private final byte[] NEWLINE;
    private static final byte[] ELLIPSES = "...".getBytes(StandardCharsets.US_ASCII);

    private static final Map<Character, Byte> SPECIALS = new HashMap<>();
    private static final Map<Integer, Byte> CODEPOINT_SPECIALS = new HashMap<>();

    static {
        // SPACE
        SPECIALS.put('\u200B', SPACE); // Zero width space
        SPECIALS.put('\u2060', SPACE); // Word joiner
        SPECIALS.put('\u2420', SPACE); // Symbol for space
        SPECIALS.put('\u2422', SPACE); // Blank symbol
        SPECIALS.put('\u2423', SPACE); // Open box
        SPECIALS.put('\uFEFF', SPACE); // Zero width no-break space
        // EXCLAMATION MARK
        SPECIALS.put('\u00A1', EXCLAMATION_MARK); // Inverted exclamation mark
        SPECIALS.put('\u01C3', EXCLAMATION_MARK); // Latin letter retroflex click
        SPECIALS.put('\u203C', EXCLAMATION_MARK); // Double exclamation mark
        SPECIALS.put('\u26A0', EXCLAMATION_MARK); // Warning sign
        SPECIALS.put('\u2757', EXCLAMATION_MARK); // Heavy exclamation mark symbol
        SPECIALS.put('\u2762', EXCLAMATION_MARK); // Heavy exclamation mark ornament
        SPECIALS.put('\u2E53', EXCLAMATION_MARK); // Medieval exclamation mark
        SPECIALS.put('\uA71D', EXCLAMATION_MARK); // Modifier letter raised exclamation mark
        // QUOTATION MARK
        SPECIALS.put('\u02BA', QUOTATION_MARK); // Modifier letter double prime
        SPECIALS.put('\u02DD', QUOTATION_MARK); // Double acute accent
        SPECIALS.put('\u02EE', QUOTATION_MARK); // Modifier letter double apostrophe
        SPECIALS.put('\u030B', QUOTATION_MARK); // Combining double acute accent
        SPECIALS.put('\u030E', QUOTATION_MARK); // Combining double vertical line above
        SPECIALS.put('\u05F4', QUOTATION_MARK); // Hebrew punctuation gershayim
        SPECIALS.put('\u2033', QUOTATION_MARK); // Double prime
        SPECIALS.put('\u3003', QUOTATION_MARK); // Ditto mark
        // NUMBER SIGN
        SPECIALS.put('\u2114', NUMBER_SIGN); // L B bar symbol
        SPECIALS.put('\u2116', NUMBER_SIGN); // Numero sign
        SPECIALS.put('\u2317', NUMBER_SIGN); // Viewdata square
        SPECIALS.put('\u266F', NUMBER_SIGN); // Musical sharp sign
        SPECIALS.put('\u29E3', NUMBER_SIGN); // Equals sign and slanted parallel
        // PERCENT SIGN
        SPECIALS.put('\u066A', PERCENT_SIGN); // Arabic percent sign
        // AMPERSAND
        SPECIALS.put('\u204A', AMPERSAND); // Tironian sign et
        SPECIALS.put('\u214B', AMPERSAND); // Turned ampersand
        // APOSTROPHE
        // Quote punctuation (Pi and Pf) mapped to apostrophe
        SPECIALS.put('\u2018', APOSTROPHE); // Left single quotation mark
        SPECIALS.put('\u2019', APOSTROPHE); // Right single quotation mark
        SPECIALS.put('\u201B', APOSTROPHE); // Single high reversed-9 quotation mark
        SPECIALS.put('\u2039', APOSTROPHE); // Single left-pointing angle quotation mark
        SPECIALS.put('\u203A', APOSTROPHE); // Single right-pointing angle quotation mark
        // Other characters mapped to apostrophe
        SPECIALS.put('\u00B4', APOSTROPHE); // Acute accent
        SPECIALS.put('\u02B9', APOSTROPHE); // Modifier letter prime
        SPECIALS.put('\u02BC', APOSTROPHE); // Modifier letter apostrophe
        SPECIALS.put('\u02C8', APOSTROPHE); // Modifier letter vertical line
        SPECIALS.put('\u05F3', APOSTROPHE); // Hebrew punctuation geresh
        SPECIALS.put('\u2032', APOSTROPHE); // Prime
        SPECIALS.put('\uA78C', APOSTROPHE); // Latin small letter saltillo
        // BACKTICK (GRAVE ACCENT)
        SPECIALS.put('\u2035', BACKTICK); // Reversed prime
        // LEFT SQUARE BRACKET
        SPECIALS.put('\u2045', LEFT_SQUARE_BRACKET); // Left square bracket with quill
        SPECIALS.put('\u27E6', LEFT_SQUARE_BRACKET); // Mathematical left white square bracket
        SPECIALS.put('\u298B', LEFT_SQUARE_BRACKET); // Left square bracket with underbar
        SPECIALS.put('\u298D', LEFT_SQUARE_BRACKET); // Left square bracket with tick in top corner
        SPECIALS.put('\u298F', LEFT_SQUARE_BRACKET); // Left square bracket with tick in bottom corner
        SPECIALS.put('\uFE47', LEFT_SQUARE_BRACKET); // Presentation form for vertical left square bracket
        SPECIALS.put('\uFF3B', LEFT_SQUARE_BRACKET); // Fullwidth left square bracket
        // RIGHT SQUARE BRACKET
        SPECIALS.put('\u2046', RIGHT_SQUARE_BRACKET); // Right square bracket with quill
        SPECIALS.put('\u27E7', RIGHT_SQUARE_BRACKET); // Mathematical right white square bracket
        SPECIALS.put('\u298C', RIGHT_SQUARE_BRACKET); // Right square bracket with underbar
        SPECIALS.put('\u298E', RIGHT_SQUARE_BRACKET); // Right square bracket with tick in top corner
        SPECIALS.put('\u2990', RIGHT_SQUARE_BRACKET); // Right square bracket with tick in bottom corner
        SPECIALS.put('\uFE48', RIGHT_SQUARE_BRACKET); // Presentation form for vertical right square bracket
        SPECIALS.put('\uFF3D', RIGHT_SQUARE_BRACKET); // Fullwidth right square bracket
        // LEFT CURLY BRACKET
        SPECIALS.put('\u2774', LEFT_CURLY_BRACKET); // Medium left curly bracket ornament
        SPECIALS.put('\u2983', LEFT_CURLY_BRACKET); // Left white curly bracket
        SPECIALS.put('\uFE37', LEFT_CURLY_BRACKET); // Presentation form for vertical left curly bracket
        SPECIALS.put('\uFE5B', LEFT_CURLY_BRACKET); // Small left curly bracket
        SPECIALS.put('\uFF5B', LEFT_CURLY_BRACKET); // Fullwidth left curly bracket
        // RIGHT CURLY BRACKET
        SPECIALS.put('\u2775', RIGHT_CURLY_BRACKET); // Medium right curly bracket ornament
        SPECIALS.put('\u2984', RIGHT_CURLY_BRACKET); // Right white curly bracket
        SPECIALS.put('\uFE38', RIGHT_CURLY_BRACKET); // Presentation form for vertical right curly bracket
        SPECIALS.put('\uFE5C', RIGHT_CURLY_BRACKET); // Small right curly bracket
        SPECIALS.put('\uFF5D', RIGHT_CURLY_BRACKET); // Fullwidth right curly bracket
        // ASTERISK
        SPECIALS.put('\u066D', ASTERISK); // Arabic five pointed star
        SPECIALS.put('\u203B', ASTERISK); // Reference mark
        SPECIALS.put('\u2042', ASTERISK); // Asterism
        SPECIALS.put('\u204E', ASTERISK); // Low asterisk
        SPECIALS.put('\u2051', ASTERISK); // Two asterisks aligned vertically
        SPECIALS.put('\u2217', ASTERISK); // Asterisk operator
        SPECIALS.put('\u26B9', ASTERISK); // Sextile
        SPECIALS.put('\u2731', ASTERISK); // Heavy asterisk
        SPECIALS.put('\uA673', ASTERISK); // Slavonic asterisk
        // PLUS SIGN
        SPECIALS.put('\u02D6', PLUS_SIGN); // Modifier letter plus sign
        SPECIALS.put('\u2795', PLUS_SIGN); // Heavy plus sign
        SPECIALS.put('\uFB29', PLUS_SIGN); // Hebrew letter alternative plus sign
        // COMMA
        SPECIALS.put('\u060C', COMMA); // Arabic comma
        SPECIALS.put('\u066B', COMMA); // Arabic decimal separator
        SPECIALS.put('\u2E41', COMMA); // Reversed comma
        SPECIALS.put('\u2E4C', COMMA); // Medieval comma
        SPECIALS.put('\u3001', COMMA); // Ideographic comma
        // HYPHEN-MINUS
        SPECIALS.put('\u00AD', HYPHEN_MINUS); // Soft hyphen
        SPECIALS.put('\u02D7', HYPHEN_MINUS); // Modifier letter minus sign
        SPECIALS.put('\u2010', HYPHEN_MINUS); // Hyphen
        SPECIALS.put('\u2011', HYPHEN_MINUS); // Non-breaking hyphen
        SPECIALS.put('\u2012', HYPHEN_MINUS); // Figure dash
        SPECIALS.put('\u2013', HYPHEN_MINUS); // En dash
        SPECIALS.put('\u2027', HYPHEN_MINUS); // Hyphenation point
        SPECIALS.put('\u2043', HYPHEN_MINUS); // Hyphen bullet
        SPECIALS.put('\u2212', HYPHEN_MINUS); // Minus sigh
        // FULL STOP
        SPECIALS.put('\u00B7', FULL_STOP); // Middle dot
        SPECIALS.put('\u06D4', FULL_STOP); // Arabic full stop
        SPECIALS.put('\u2024', FULL_STOP); // One dot leader
        SPECIALS.put('\u2E33', FULL_STOP); // Raised dot
        SPECIALS.put('\u2E3C', FULL_STOP); // Stenographic full stop
        SPECIALS.put('\u3002', FULL_STOP); // Ideographic full stop
        // SOLIDUS
        SPECIALS.put('\u2044', SOLIDUS); // Fraction slash
        SPECIALS.put('\u2215', SOLIDUS); // Division slash
        SPECIALS.put('\u27CB', SOLIDUS); // Mathematical rising diagonal
        SPECIALS.put('\u29F8', SOLIDUS); // Big solidus
        SPECIALS.put('\u2E4A', SOLIDUS); // Dotted solidus
        // REVERSE SOLIDUS
        SPECIALS.put('\u27CD', REVERSE_SOLIDUS); // Mathematical falling diagonal
        SPECIALS.put('\u29F9', REVERSE_SOLIDUS); // Big reverse solidus
        // COLON
        SPECIALS.put('\u02D0', COLON); // Modifier letter triangular colon
        SPECIALS.put('\u02F8', COLON); // Modifier letter raised colon
        SPECIALS.put('\u1365', COLON); // Ethiopic colon
        SPECIALS.put('\u205A', COLON); // Two dot punctuation
        SPECIALS.put('\u205D', COLON); // Tricolon
        SPECIALS.put('\u2236', COLON); // Ratio
        SPECIALS.put('\uA789', COLON); // Modifier letter colon
        // SEMICOLON
        SPECIALS.put('\u037E', SEMICOLON); // Greek question mark
        SPECIALS.put('\u061B', SEMICOLON); // Arabic semicolon
        SPECIALS.put('\u204F', SEMICOLON); // Reversed semicolon
        SPECIALS.put('\u2E35', SEMICOLON); // Turned semicolon
        // EQUALS
        SPECIALS.put('\u2248', EQUALS); // Almost equal to
        SPECIALS.put('\u2261', EQUALS); // Identical to
        SPECIALS.put('\uA78A', EQUALS); // Modifier letter short equals sign
        SPECIALS.put('\uFE66', EQUALS); // Small equals sign
        // QUESTION MARK
        SPECIALS.put('\u00BF', QUESTION_MARK); // Inverted question mark
        SPECIALS.put('\u061F', QUESTION_MARK); // Arabic question mark
        SPECIALS.put('\u203D', QUESTION_MARK); // Interrobang
        SPECIALS.put('\u2047', QUESTION_MARK); // Double question mark
        SPECIALS.put('\u2753', QUESTION_MARK); // Black question mark ornament
        SPECIALS.put('\u2BD1', QUESTION_MARK); // Uncertainty sign
        SPECIALS.put('\u2E2E', QUESTION_MARK); // Reversed question mark
        SPECIALS.put('\u2E54', QUESTION_MARK); // Medieval question mark
        // COMMERCIAL AT
        SPECIALS.put('\u24D0', COMMERCIAL_AT); // Circled latin small letter a
        // CIRCUMFLEX ACCENT
        SPECIALS.put('\u2038', CIRCUMFLEX_ACCENT); // Caret
        SPECIALS.put('\u2227', CIRCUMFLEX_ACCENT); // Logical and
        SPECIALS.put('\u2303', CIRCUMFLEX_ACCENT); // Up arrowhead
        // LOW LINE
        SPECIALS.put('\u2017', LOW_LINE); // Double low line
        // VERTICAL LINE
        SPECIALS.put('\u00A6', VERTICAL_LINE); // Broken bar
        SPECIALS.put('\u01C0', VERTICAL_LINE); // Latin letter dental click
        SPECIALS.put('\u05C0', VERTICAL_LINE); // Hebrew punctuation paseq
        SPECIALS.put('\u0964', VERTICAL_LINE); // Devanagari danda
        SPECIALS.put('\u2223', VERTICAL_LINE); // Divides
        SPECIALS.put('\u2758', VERTICAL_LINE); // Light vertical bar
        // TILDE
        SPECIALS.put('\u02DC', TILDE); // Small tilde
        SPECIALS.put('\u2053', TILDE); // Swung dash
        SPECIALS.put('\u223C', TILDE); // Tilde operator
        SPECIALS.put('\u2E1B', TILDE); // Tilde with ring above
        SPECIALS.put('\u2E2F', TILDE); // Vertical tilde
        SPECIALS.put('\u201C', TILDE); // Wave dash
        SPECIALS.put('\uFF5E', TILDE); // Fullwidth tilde

        CODEPOINT_SPECIALS.put(0x1F4B2, DOLLAR_SIGN);  // Heavy dollar sign
        CODEPOINT_SPECIALS.put(0x1F674, AMPERSAND);    // Heavy ampersand ornament
        CODEPOINT_SPECIALS.put(0x1F7B6, ASTERISK);     // Medium six spoked asterisk
        CODEPOINT_SPECIALS.put(0x1F7A2, PLUS_SIGN);    // Light greek cross
        CODEPOINT_SPECIALS.put(0x1F7F0, EQUALS);       // Heavy equals sign
        CODEPOINT_SPECIALS.put(0x10191, HYPHEN_MINUS); // Roman uncia sign
    }

    /**
     * Create a transliterating encoder.
     *
     * @param replacement The initial replacement; must not be null, must have non-zero length, must not be longer than
     *                   maxBytesPerChar, and must be legal.
     * @param lineSeparator the line separator replacement used to encode Unicode line separator or paragraph separator,
     *                      based on the categories Zl and Zp; must not be null, must not be longer thn maxBytesPerChar,
     *                      must be legal. Note that carriage return and line feed are not in those categories and are
     *                      encoded directly using '0x0D' and '0x0A', respectively.
     */
    public Transliterate(final byte[] replacement, final byte[] lineSeparator) {
        super(INTERNAL, 1f, 3f, replacement);
        this.NEWLINE = lineSeparator;
    }

    public Transliterate() {
        this(new byte[]{QUESTION_MARK}, System.lineSeparator().getBytes(StandardCharsets.US_ASCII));
    }

    @Override
    protected CoderResult encodeLoop(final CharBuffer in, final ByteBuffer out) {
        while (in.hasRemaining()) {
            if (!out.hasRemaining()) {
                return CoderResult.OVERFLOW;
            } else {
                final char ch = in.get();
                final byte b = convert(ch);
                if (b >= 0) {
                    out.put(b);
                } else {
                    final CharSequence csq;
                    if (b == -1) {
                        csq = String.valueOf(ch);
                    } else if (b == -2) {
                        if (in.hasRemaining()) {
                            final char ch2 = in.get();
                            if (Character.isLowSurrogate(ch2)) {
                                csq = String.valueOf(new char[]{ch, ch2});
                            } else {
                                // Whatever was read as a high surrogate isn't mappable without a low surrogate, but
                                // still possible the non-low surrogate character is mappable, so back up by only 1
                                in.position(in.position() - 1);
                                return CoderResult.unmappableForLength(1);
                            }
                        } else {
                            in.position(in.position() - 1);
                            return CoderResult.UNDERFLOW;
                        }
                    } else {
                        // This code should be unreachable
                        in.position(in.position() - 1);
                        return CoderResult.unmappableForLength(1);
                    }
                    final int mark = out.position();
                    try {
                        emit(csq, out);
                    } catch (final BufferOverflowException e) {
                        final int read = -b;
                        in.position(out.position() - read);
                        out.position(mark);
                        return CoderResult.OVERFLOW;
                    }
                    if (out.position() == mark) {
                        // failed to generate anything, identify as unmappable
                        final int read = -b;
                        in.position(out.position() - read);
                        return CoderResult.unmappableForLength(read);
                    }
                }
            }
        }
        return CoderResult.UNDERFLOW;
    }

    private void emit(final CharSequence csq, final ByteBuffer out) throws BufferOverflowException {
        final String normalized = Normalizer.normalize(csq, Normalizer.Form.NFKD);
        for (int i = 0; i < normalized.length(); i++) {
            final char ch = normalized.charAt(i);
            final byte b = convert(ch);
            if (b >= 0) {
                out.put(b);
            } else {
                final int cp = normalized.codePointAt(i);
                final int ct = Character.getType(cp);
                if (ct == DASH_PUNCTUATION) {
                    out.put(HYPHEN_MINUS);
                } else if (ct == CONNECTOR_PUNCTUATION) {
                    out.put(LOW_LINE);
                } else if (ct == SPACE_SEPARATOR) {
                    out.put(SPACE);
                } else if (ct == INITIAL_QUOTE_PUNCTUATION || ct == FINAL_QUOTE_PUNCTUATION) {
                    // convert(char) already covers apostrophes within these categories
                    out.put(QUOTATION_MARK);
                } else if (ct == START_PUNCTUATION) {
                    // convert(char) already covers left square and curly brackets
                    out.put(LEFT_PARENTHESIS);
                } else if (ct == END_PUNCTUATION) {
                    // convert(char) already covers right square and curly brackets
                    out.put(RIGHT_PARENTHESIS);
                } else if (ct == DECIMAL_DIGIT_NUMBER) {
                    out.put((byte) (0x30 + Character.getNumericValue(ch)));
                } else if (CODEPOINT_SPECIALS.containsKey(cp)) {
                    out.put(CODEPOINT_SPECIALS.get(cp));
                } else if (ct == LINE_SEPARATOR || ct == PARAGRAPH_SEPARATOR) {
                    // Potentially multibyte
                    out.put(NEWLINE);
                } else if (cp == HORIZONTAL_ELLIPSIS || cp == VERTICAL_ELLIPSIS || cp == MONGOLIAN_ELLIPSIS) {
                    // multibyte
                    out.put(ELLIPSES);
                } else if (cp == UNICODE_REPLACEMENT) {
                    // Potentially multibyte
                    out.put(replacement());
                }
            }
        }
    }

    private byte convert(final char ch) {
        if (ch < '\u0080') {
            return (byte) ch;
        } else if (SPECIALS.containsKey(ch)) {
            return SPECIALS.get(ch);
        } else if (isHighSurrogate(ch)) {
            return -2;
        } else {
            return -1;
        }
    }

    private static final Charset INTERNAL = new Charset("X-Transliterate-Internal", new String[0]) {

        @Override
        public boolean contains(final Charset cs) {
            return cs == this;
        }

        @Override
        public CharsetDecoder newDecoder() {
            throw new UnsupportedOperationException();
        }

        @Override
        public CharsetEncoder newEncoder() {
            return new Transliterate();
        }
    };
}
