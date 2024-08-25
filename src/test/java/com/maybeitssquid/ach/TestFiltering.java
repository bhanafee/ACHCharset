package com.maybeitssquid.ach;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class TestFiltering extends AbstractEncoderTests {

    @BeforeEach
    protected void setUp() {
        this.encoder = new Filtering();
    }

    @Test
    public void testSpaceSeparator() {
        final String spaces = " \u0020";
        assertAll(' ', spaces.codePoints());
    }

    @Test
    public void testLineSeparator() {
        assertIs("\n", 0x000A);
        assertIs("\r", 0x000D);
    }

    @Test
    public void testParagraphSeparator() {
        final String paragraph = "\n";
        assertAll('\n', paragraph.codePoints());
    }

    @Test
    public void testConnectorPunctuation() {
        final String connector = "_\u005F";
        assertAll('_', connector.codePoints());
    }

    public void testDecimalDigits(final int zero) {
        for (int i = 0; i < 10; i++) {
            final char digit = encoder.apply(zero + i)[0];
            assertEquals('0' + i, digit, String.format("Digit was %c, expected %c", digit, 0x48 + i));
        }
    }

    @Test
    public void testDecimalDigits() {
        testDecimalDigits(0x0030); // ASCII
    }

    @Test
    public void testOtherPunctuation() {
        assertIs("!", 0x0021);
        assertIs("\"", 0x0022);
        assertIs("#", 0x0023);
        assertIs("%", 0x0025);
        assertIs("&", 0x0026);
        assertIs("'", 0x0027);
        assertIs("*", 0x002A);
        assertIs(",", 0x002C);
        assertIs(".", 0x002E);
        assertIs("/", 0x002F);
        assertIs(":", 0x003A);
        assertIs(";", 0x003B);
        assertIs("?", 0x003F);
        assertIs("@", 0x0040);
        assertIs("\\", 0x005C);
    }

    @Test
    public void testMathSymbol() {
        assertIs("+", 0x002B);
        assertIs("<", 0x003C);
        assertIs("=", 0x003D);
        assertIs(">", 0x003E);
        assertIs("|", 0x007C);
        assertIs("~", 0x007E);
    }

    @Test
    public void testOtherSymbol() {
        // EMPTY
    }

    @Test
    public void testControl() {
        // These are all pass-through
        for (int codepoint = 0x000; codepoint <= 0x001f; codepoint++) {
            final char[] transliterated = encoder.apply(codepoint);
            assertNotNull(transliterated);
            assertEquals(1, transliterated.length);
            assertEquals(codepoint, transliterated[0]);
        }
        assertIs("" + '\u007F', 0x007F);
    }

    @Test
    public void testDashPunctuation() {
        final String dash = "-";
        assertAll('-', dash.codePoints());
    }

    @Test
    public void testStartPunctuation() {
        final String parenthesis = "(";
        assertAll('(', parenthesis.codePoints());
    }

    @Test
    public void testEndPunctuation() {
        final String parenthesis = ")";
        assertAll(')', parenthesis.codePoints());
    }


    @Test
    public void testQuotePunctuation() {
        final String quote = "\"";
        assertAll('"', quote.codePoints());
    }

    @Test
    public void testNumberSign() {
        final String keep = "#";
        assertAll('#', keep.codePoints());
    }

    @Test
    public void testPercentSign() {
        final String keep = "%";
        assertAll('%', keep.codePoints());
    }

    @Test
    public void testAmpersand() {
        final String keep = "&";
        assertAll('&', keep.codePoints());
    }

    @Test
    public void testBacktick() {
        final String keep = "`";
        assertAll('`', keep.codePoints());
    }

    @Test
    public void testAsterisk() {
        final String keep = "*";
        assertAll('*', keep.codePoints());
    }

    @Test
    public void testPlus() {
        final String keep = "+";
        assertAll('+', keep.codePoints());
    }

    @Test
    public void testComma() {
        final String keep = ",";
        assertAll(',', keep.codePoints());
    }

    @Test
    public void testFullStop() {
        final String keep = ".";
        assertAll('.', keep.codePoints());
    }

    @Test
    public void testSolidus() {
        final String keep = "/";
        assertAll('/', keep.codePoints());
    }

    @Test
    public void testReverseSolidus() {
        final String keep = "\\";
        assertAll('\\', keep.codePoints());
    }

    @Test
    public void testColon() {
        final String keep = ":";
        assertAll(':', keep.codePoints());
    }

    @Test
    public void testExclamationMark() {
        final String keep = "!";
        assertAll('!', keep.codePoints());
    }

    @Test
    public void testQuestionMark() {
        final String keep = "?";
        assertAll('?', keep.codePoints());
    }

    @Test
    public void testDollarSign() {
        final String keep = "$";
        assertAll('$', keep.codePoints());
    }

    @Test
    public void testTilde() {
        final String keep = "~";
        assertAll('~', keep.codePoints());
    }

    @Test
    public void testBasicLatin() {
        // These are all pass-through
        for (int codepoint = 0x000; codepoint <= 0x007f; codepoint++) {
            final char[] transliterated = encoder.apply(codepoint);
            assertNotNull(transliterated);
            assertEquals(1, transliterated.length);
            assertEquals(codepoint, transliterated[0]);
        }
    }

    @Test
    public void testLatin1Supplement() {
        assertEmptyRange(0x0080, 0x00FF);
    }

    @Test
    public void testLatinExtendedA() {
        assertEmptyRange(0x0100, 0x017F);
    }

    @Test
    public void testLatinExtendedB() {
        assertEmptyRange(0x0190, 0x024F);
    }

    @Test
    public void testLatinExtendedC() {
        assertEmptyRange(0x2C60, 0x2C7F);
    }

    @Test
    public void testLatinExtendedD() {
        assertEmptyRange(0xA720, 0xA7FF);
    }

    @Test
    public void testLatinExtendedE() {
        assertEmptyRange(0xAB30, 0xAB6F);
    }

    @Test
    public void testLatinExtendedF() {
        // all modifiers, no transliteration
        assertEmptyRange(0x10780, 0x107BF);
    }

    @Test
    public void testLatinExtendedG() {
        // all modifiers, no transliteration
        assertEmptyRange(0x1DF00, 0x1DFFF);
    }

    @Test
    public void testLatinExtendedAdditional() {
        assertEmptyRange(0x1E00, 0x1EFF);
    }
    @Test
    public void testHalfWidthAndFullwidth() {
        assertEmptyRange(0xFF00, 0xFFEF);
    }

    @Test
    public void testIPAExtensions() {
        assertEmptyRange(0x0250, 0x02AF);
    }

    @Test
    public void testPhoneticExtensions() {
        assertEmptyRange(0x1D00, 0x1D7F);
    }

    @Test
    public void testPhoneticExtensionsSupplement() {
        assertEmptyRange(0x1D80, 0x1DBF);
    }

    @Test
    public void testSuperscriptsAndSubscripts() {
        assertNone(0x2070, 0x209F);
    }

    @Test
    public void testOpticalCharacterRecognition() {
        assertEmptyRange(0x2440, 0x245F);
    }

    /**
     * ─━│┃┄┅┆┇┈┉┊┋┌┍┎┏┐┑┒┓└┕┖┗┘┙┚┛├┝┞┟┠┡┢┣┤┥┦┧┨┩┪┫┬┭┮┯┰┱┲┳┴┵┶┷┸┹┺┻┼┽┾┿╀╁╂╃╄╅╆╇╈╉╊╋╌╍╎╏═║╒╓╔╕╖╗╘╙╚╛╜╝╞╟╠╡╢╣╤╥╦╧╨╩╪╫╬╭╮╯╰╱╲╳╴╵╶╷╸╹╺╻╼╽╾╿
     */
    @Test
    public void testBoxDrawing() {
        assertEmptyRange(0x2500, 0x257F);
    }

    /**
     * ▀▁▂▃▄▅▆▇█▉▊▋▌▍▎▏▐░▒▓▔▕▖▗▘▙▚▛▜▝▞▟
     */
    @Test
    public void testBlockElements() {
        assertEmptyRange(0x2580, 0x259F);
    }

    /**
     * Unicode explicitly does not map the 256 possible Braille patterns to representations such as letters.
     * ⠀⠁⠂⠃⠄⠅⠆⠇⠈⠉⠊⠋⠌⠍⠎⠏⠐⠑⠒⠓⠔⠕⠖⠗⠘⠙⠚⠛⠜⠝⠞⠟⠠⠡⠢⠣⠤⠥⠦⠧⠨⠩⠪⠫⠬⠭⠮⠯⠰⠱⠲⠳⠴⠵⠶⠷⠸⠹⠺⠻⠼⠽⠾⠿⡀⡁⡂⡃⡄⡅⡆⡇⡈⡉⡊⡋⡌⡍⡎⡏⡐⡑⡒⡓⡔⡕⡖⡗⡘⡙⡚⡛⡜⡝⡞⡟⡠⡡⡢⡣⡤⡥⡦⡧⡨⡩⡪⡫⡬⡭⡮⡯⡰⡱⡲⡳⡴⡵⡶⡷⡸⡹⡺⡻⡼⡽⡾⡿
     */
    @Test
    public void testBraillePatterns() {
        assertEmptyRange(0x2888, 0x28FF);
    }
    /**
     * ■□▢▣▤▥▦▧▨▩▪▫▬▭▮▯▰▱▲△▴▵▶▷▸▹►▻▼▽▾▿◀◁◂◃◄◅◆◇◈◉◊○◌◍◎●◐◑◒◓◔◕◖◗◘◙◚◛◜◝◞◟◠◡◢◣◤◥◦◧◨◩◪◫◬◭◮◯◰◱◲◳◴◵◶◷◸◹◺◻◼◽◾◿
     */
    @Test
    public void testGeometricShapes() {
        assertEmptyRange(0x2580, 0x259F);    // Geometric Shapes
        assertEmptyRange(0x1F780, 0x1F7FF);  // Geometric Shapes Extended
    }

    @Test
    public void testGameSymbols() {
        assertEmptyRange(0x2600, 0x26FF);   // Chess, Checkers/Draughts, Japanese chess, card suits
        assertEmptyRange(0x1FA00, 0x1FA6F); // Chess Symbols
        assertEmptyRange(0x1F030, 0x1F09F); // Domino Tiles
        assertEmptyRange(0x1F000, 0x1F02F); // Mahjong Tiles
        assertEmptyRange(0x1F0A0, 0x1F0FF); // Playing Cards
    }

    @Test
    public void testMiscellaneousSymbolsAndArrows() {
        assertEmptyRange(0x2B00, 0x2BFF);
    }

    @Test
    public void testSupplementalArrows() {
        assertEmptyRange(0x27F0, 0x27FF);   // Supplemental Arrows-A
        assertEmptyRange(0x2900, 0x297F);   // Supplemental Arrows-B
        assertEmptyRange(0x1F800, 0x1F8FF); // Supplemental Arrows-C
    }
}
