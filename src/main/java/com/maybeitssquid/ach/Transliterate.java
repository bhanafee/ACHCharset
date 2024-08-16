package com.maybeitssquid.ach;

import java.nio.BufferOverflowException;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.nio.charset.CharsetEncoder;
import java.nio.charset.CoderResult;
import java.nio.charset.StandardCharsets;
import java.text.Normalizer;
import java.util.HashMap;
import java.util.Map;

import static java.lang.Character.*;

@SuppressWarnings("unused")
public class Transliterate extends ASCIISubset {
    private static final int UNICODE_REPLACEMENT = '\uFFFD';

    private static final byte[] NOTHING = new byte[0];
    private static final byte[] NEWLINE = System.lineSeparator().getBytes(StandardCharsets.US_ASCII);
    private static final byte[] ELLIPSES = {(byte) '.', (byte) '.', (byte) '.'};
    private static final byte[] INTERROBANG = {(byte) '?', (byte) '!'};

    private static final byte[] SPACE = {(byte) ' '};
    private static final byte[] EXCLAMATION_MARK = {(byte) '!'};
    private static final byte[] QUOTATION_MARK = {(byte) '"'};
    private static final byte[] NUMBER_SIGN = {(byte) '#'};
    private static final byte[] DOLLAR_SIGN = {(byte) '$'};
    private static final byte[] PERCENT_SIGN = {(byte) '%'};
    private static final byte[] AMPERSAND = {(byte) '&'};
    private static final byte[] APOSTROPHE = {(byte) '\''};
    private static final byte[] BACKTICK = {(byte) '`'};
    private static final byte[] LEFT_PARENTHESIS = {(byte) '('};
    private static final byte[] RIGHT_PARENTHESIS = {(byte) ')'};
    private static final byte[] LEFT_SQUARE_BRACKET = {(byte) '['};
    private static final byte[] RIGHT_SQUARE_BRACKET = {(byte) ']'};
    private static final byte[] LEFT_CURLY_BRACKET = {(byte) '{'};
    private static final byte[] RIGHT_CURLY_BRACKET = {(byte) '}'};
    private static final byte[] ASTERISK = {(byte) '*'};
    private static final byte[] PLUS_SIGN = {(byte) '+'};
    private static final byte[] COMMA = {(byte) ','};
    private static final byte[] HYPHEN_MINUS = {(byte) '-'};
    private static final byte[] FULL_STOP = {(byte) '.'};
    private static final byte[] SOLIDUS = {(byte) '/'};
    private static final byte[] REVERSE_SOLIDUS = {(byte) '\\'};
    private static final byte[] COLON = {(byte) ':'};
    private static final byte[] SEMICOLON = {(byte) ';'};
    private static final byte[] EQUALS = {(byte) '='};
    private static final byte[] QUESTION_MARK = {(byte) '?'};
    private static final byte[] CIRCUMFLEX_ACCENT = {(byte) '^'};
    private static final byte[] LOW_LINE = {(byte) '_'};
    private static final byte[] VERTICAL_LINE = {(byte) '|'};
    private static final byte[] TILDE = {(byte) '~'};

    private static final Map<Integer, byte[]> DIRECT = new HashMap<>();

    static {
        // EXCLAMATION MARK
        DIRECT.put((int) '\u00A1', EXCLAMATION_MARK); // Inverted exclamation mark
        DIRECT.put((int) '\u01C3', EXCLAMATION_MARK); // Latin letter retroflex click
        DIRECT.put((int) '\u26A0', EXCLAMATION_MARK); // Warning sign
        DIRECT.put((int) '\u2757', EXCLAMATION_MARK); // Heavy exclamation mark symbol
        DIRECT.put((int) '\u2762', EXCLAMATION_MARK); // Heavy exclamation mark ornament
        DIRECT.put((int) '\u2E53', EXCLAMATION_MARK); // Medieval exclamation mark
        DIRECT.put((int) '\uA71D', EXCLAMATION_MARK); // Modifier letter raised exclamation mark
        // QUOTATION MARK
        DIRECT.put((int) '\u02BA', QUOTATION_MARK); // Modifier letter double prime
        DIRECT.put((int) '\u02EE', QUOTATION_MARK); // Modifier letter double apostrophe
        DIRECT.put((int) '\u05F4', QUOTATION_MARK); // Hebrew punctuation gershayim
        DIRECT.put((int) '\u2033', QUOTATION_MARK); // Double prime
        DIRECT.put((int) '\u3003', QUOTATION_MARK); // Ditto mark
        // NUMBER SIGN
        DIRECT.put((int) '\u2114', NUMBER_SIGN); // L B bar symbol
        DIRECT.put((int) '\u2317', NUMBER_SIGN); // Viewdata square
        DIRECT.put((int) '\u266F', NUMBER_SIGN); // Musical sharp sign
        DIRECT.put((int) '\u29E3', NUMBER_SIGN); // Equals sign and slanted parallel
        // PERCENT SIGN
        DIRECT.put((int) '\u066A', PERCENT_SIGN); // Arabic percent sign
        // AMPERSAND
        DIRECT.put((int) '\u204A', AMPERSAND); // Tironian sign et
        DIRECT.put((int) '\u214B', AMPERSAND); // Turned ampersand
        DIRECT.put(0x1F674, AMPERSAND);    // Heavy ampersand ornament
        // APOSTROPHE
        // Quote punctuation (Pi and Pf) mapped to apostrophe
        DIRECT.put((int) '\u2019', APOSTROPHE); // Right single quotation mark
        DIRECT.put((int) '\u201B', APOSTROPHE); // Single high reversed-9 quotation mark
        DIRECT.put((int) '\u2039', APOSTROPHE); // Single left-pointing angle quotation mark
        DIRECT.put((int) '\u203A', APOSTROPHE); // Single right-pointing angle quotation mark
        // Other characters mapped to apostrophe
        DIRECT.put((int) '\u02B9', APOSTROPHE); // Modifier letter prime
        DIRECT.put((int) '\u02BC', APOSTROPHE); // Modifier letter apostrophe
        DIRECT.put((int) '\u02C8', APOSTROPHE); // Modifier letter vertical line
        DIRECT.put((int) '\u05F3', APOSTROPHE); // Hebrew punctuation geresh
        DIRECT.put((int) '\u2032', APOSTROPHE); // Prime
        DIRECT.put((int) '\uA78C', APOSTROPHE); // Latin small letter saltillo
        // BACKTICK (GRAVE ACCENT)
        DIRECT.put((int) '\u2035', BACKTICK); // Reversed prime
        // LEFT SQUARE BRACKET
        DIRECT.put((int) '\u2045', LEFT_SQUARE_BRACKET); // Left square bracket with quill
        DIRECT.put((int) '\u27E6', LEFT_SQUARE_BRACKET); // Mathematical left white square bracket
        DIRECT.put((int) '\u298B', LEFT_SQUARE_BRACKET); // Left square bracket with underbar
        DIRECT.put((int) '\u298D', LEFT_SQUARE_BRACKET); // Left square bracket with tick in top corner
        DIRECT.put((int) '\u298F', LEFT_SQUARE_BRACKET); // Left square bracket with tick in bottom corner
        // RIGHT SQUARE BRACKET
        DIRECT.put((int) '\u2046', RIGHT_SQUARE_BRACKET); // Right square bracket with quill
        DIRECT.put((int) '\u27E7', RIGHT_SQUARE_BRACKET); // Mathematical right white square bracket
        DIRECT.put((int) '\u298C', RIGHT_SQUARE_BRACKET); // Right square bracket with underbar
        DIRECT.put((int) '\u298E', RIGHT_SQUARE_BRACKET); // Right square bracket with tick in top corner
        DIRECT.put((int) '\u2990', RIGHT_SQUARE_BRACKET); // Right square bracket with tick in bottom corner
        // LEFT CURLY BRACKET
        DIRECT.put((int) '\u2774', LEFT_CURLY_BRACKET); // Medium left curly bracket ornament
        DIRECT.put((int) '\u2983', LEFT_CURLY_BRACKET); // Left white curly bracket
        // RIGHT CURLY BRACKET
        DIRECT.put((int) '\u2775', RIGHT_CURLY_BRACKET); // Medium right curly bracket ornament
        DIRECT.put((int) '\u2984', RIGHT_CURLY_BRACKET); // Right white curly bracket
        // ASTERISK
        DIRECT.put((int) '\u066D', ASTERISK); // Arabic five pointed star
        DIRECT.put((int) '\u203B', ASTERISK); // Reference mark
        DIRECT.put((int) '\u2042', ASTERISK); // Asterism
        DIRECT.put((int) '\u204E', ASTERISK); // Low asterisk
        DIRECT.put((int) '\u2051', ASTERISK); // Two asterisks aligned vertically
        DIRECT.put((int) '\u2217', ASTERISK); // Asterisk operator
        DIRECT.put((int) '\u26B9', ASTERISK); // Sextile
        DIRECT.put((int) '\uA673', ASTERISK); // Slavonic asterisk
        DIRECT.put(0x1F7B6, ASTERISK);        // Medium six spoked asterisk
        // PLUS SIGN
        DIRECT.put((int) '\u02D6', PLUS_SIGN); // Modifier letter plus sign
        DIRECT.put((int) '\u2795', PLUS_SIGN); // Heavy plus sign
        // COMMA
        DIRECT.put((int) '\u060C', COMMA); // Arabic comma
        DIRECT.put((int) '\u066B', COMMA); // Arabic decimal separator
        DIRECT.put((int) '\u2E41', COMMA); // Reversed comma
        DIRECT.put((int) '\u2E4C', COMMA); // Medieval comma
        DIRECT.put((int) '\u3001', COMMA); // Ideographic comma
        // FULL STOP
        DIRECT.put((int) '\u00B7', FULL_STOP); // Middle dot
        DIRECT.put((int) '\u06D4', FULL_STOP); // Arabic full stop
        DIRECT.put((int) '\u2E33', FULL_STOP); // Raised dot
        DIRECT.put((int) '\u2E3C', FULL_STOP); // Stenographic full stop
        DIRECT.put((int) '\u3002', FULL_STOP); // Ideographic full stop
        // SOLIDUS
        DIRECT.put((int) '\u2044', SOLIDUS); // Fraction slash
        DIRECT.put((int) '\u2215', SOLIDUS); // Division slash
        DIRECT.put((int) '\u27CB', SOLIDUS); // Mathematical rising diagonal
        DIRECT.put((int) '\u29F8', SOLIDUS); // Big solidus
        DIRECT.put((int) '\u2E4A', SOLIDUS); // Dotted solidus
        // REVERSE SOLIDUS
        DIRECT.put((int) '\u27CD', REVERSE_SOLIDUS); // Mathematical falling diagonal
        DIRECT.put((int) '\u29F9', REVERSE_SOLIDUS); // Big reverse solidus
        // COLON
        DIRECT.put((int) '\u02D0', COLON); // Modifier letter triangular colon
        DIRECT.put((int) '\u02F8', COLON); // Modifier letter raised colon
        DIRECT.put((int) '\u1365', COLON); // Ethiopic colon
        DIRECT.put((int) '\u205A', COLON); // Two dot punctuation
        DIRECT.put((int) '\u205D', COLON); // Tricolon
        DIRECT.put((int) '\u2236', COLON); // Ratio
        DIRECT.put((int) '\uA789', COLON); // Modifier letter colon
        // SEMICOLON
        DIRECT.put((int) '\u061B', SEMICOLON); // Arabic semicolon
        DIRECT.put((int) '\u204F', SEMICOLON); // Reversed semicolon
        DIRECT.put((int) '\u2E35', SEMICOLON); // Turned semicolon
        // EQUALS
        DIRECT.put((int) '\u2248', EQUALS); // Almost equal to
        DIRECT.put((int) '\u2261', EQUALS); // Identical to
        DIRECT.put((int) '\uA78A', EQUALS); // Modifier letter short equals sign
        DIRECT.put(0x1F7F0, EQUALS);        // Heavy equals sign
        // QUESTION MARK
        DIRECT.put((int) '\u00BF', QUESTION_MARK); // Inverted question mark
        DIRECT.put((int) '\u061F', QUESTION_MARK); // Arabic question mark
        DIRECT.put((int) '\u2753', QUESTION_MARK); // Black question mark ornament
        DIRECT.put((int) '\u2BD1', QUESTION_MARK); // Uncertainty sign
        DIRECT.put((int) '\u2E2E', QUESTION_MARK); // Reversed question mark
        DIRECT.put((int) '\u2E54', QUESTION_MARK); // Medieval question mark
        DIRECT.put((int) '\u203D', INTERROBANG); // Interrobang
        // CIRCUMFLEX ACCENT
        DIRECT.put((int) '\u2038', CIRCUMFLEX_ACCENT); // Caret
        DIRECT.put((int) '\u2227', CIRCUMFLEX_ACCENT); // Logical and
        DIRECT.put((int) '\u2303', CIRCUMFLEX_ACCENT); // Up arrowhead
        // LOW LINE
        DIRECT.put((int) '\u2017', LOW_LINE); // Double low line
        // VERTICAL LINE
        DIRECT.put((int) '\u00A6', VERTICAL_LINE); // Broken bar
        DIRECT.put((int) '\u01C0', VERTICAL_LINE); // Latin letter dental click
        DIRECT.put((int) '\u05C0', VERTICAL_LINE); // Hebrew punctuation paseq
        DIRECT.put((int) '\u0964', VERTICAL_LINE); // Devanagari danda
        DIRECT.put((int) '\u2223', VERTICAL_LINE); // Divides
        DIRECT.put((int) '\u2758', VERTICAL_LINE); // Light vertical bar
        // TILDE
        DIRECT.put((int) '\u02DC', TILDE); // Small tilde
        DIRECT.put((int) '\u2053', TILDE); // Swung dash
        DIRECT.put((int) '\u223C', TILDE); // Tilde operator
        DIRECT.put((int) '\u2E1B', TILDE); // Tilde with ring above
        DIRECT.put((int) '\u2E2F', TILDE); // Vertical tilde

        DIRECT.put(0x1F4B2, DOLLAR_SIGN);  // Heavy dollar sign

        DIRECT.put((int) '\u1801', ELLIPSES);  // Mongolian ellipsis
        DIRECT.put((int) '\u2026', ELLIPSES);  // Horizontal ellipsis
        DIRECT.put((int) '\uFE19', ELLIPSES);  // Vertical ellipsis
    }

    /**
     * Create a character set that decodes US-ASCII and aggressively transliterates Unicode to encode into US-ASCII.
     */
    public Transliterate() {
        super("X-ASCII-TRANSLITERATE", new String[]{}, true);
    }

    @Override
    public CharsetEncoder newEncoder() {
        return new TransliteratingEncoder(this);
    }

    private static class TransliteratingEncoder extends CharsetEncoder {
        public TransliteratingEncoder(final Charset cs) {
            super(cs, 1F, 11F, new byte[]{REPLACEMENT});
        }

        @Override
        protected CoderResult encodeLoop(final CharBuffer in, final ByteBuffer out) {
            while (in.hasRemaining()) {
                final int mark = out.position();
                final int codepoint = Character.codePointAt(in, 0);
                try {
                    transliterate(codepoint, out);
                    if (out.position() == mark) {
                        final String normalized = Normalizer.normalize(Character.toString(codepoint), Normalizer.Form.NFKD);
                        normalized.codePoints().forEach(i -> transliterate(i, out));
                    }
                    if (out.position() == mark) {
                        return CoderResult.unmappableForLength(1);
                    }
                } catch (final BufferOverflowException e) {
                    out.position(mark);
                    return CoderResult.OVERFLOW;
                }
                if (Character.isSupplementaryCodePoint(codepoint)) {
                    in.position(in.position() + 2);
                } else {
                    in.position(in.position() + 1);
                }
            }
            return CoderResult.UNDERFLOW;
        }

        private void transliterate(final int codepoint, final ByteBuffer out) throws BufferOverflowException {
            if (codepoint < 0x80) {
                out.put((byte) codepoint);
            } else if (DIRECT.containsKey(codepoint)) {
                out.put(DIRECT.get(codepoint));
            } else {
                final int category = Character.getType(codepoint);
                if (category == DASH_PUNCTUATION) {
                    out.put(HYPHEN_MINUS);
                } else if (category == CONNECTOR_PUNCTUATION) {
                    out.put(LOW_LINE);
                } else if (category == SPACE_SEPARATOR) {
                    out.put(SPACE);
                } else if (category == INITIAL_QUOTE_PUNCTUATION || category == FINAL_QUOTE_PUNCTUATION) {
                    // DIRECT covers apostrophes within these categories
                    out.put(QUOTATION_MARK);
                } else if (category == START_PUNCTUATION) {
                    // DIRECT covers left square and curly brackets
                    out.put(LEFT_PARENTHESIS);
                } else if (category == END_PUNCTUATION) {
                    // DIRECT covers right square and curly brackets
                    out.put(RIGHT_PARENTHESIS);
                } else if (category == LINE_SEPARATOR || category == PARAGRAPH_SEPARATOR) {
                    out.put(NEWLINE);
                } else if (category == DECIMAL_DIGIT_NUMBER) {
                    out.put((byte) ('0' + Character.getNumericValue(codepoint)));
                } else if (codepoint == UNICODE_REPLACEMENT) {
                    out.put(replacement());
                }
            }
        }
    }

}
