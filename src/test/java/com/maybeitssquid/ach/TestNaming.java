package com.maybeitssquid.ach;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class TestNaming extends TestCategorizing {

    @BeforeEach
    protected void setUp() {
        this.encoder = new Naming();
    }

    @Test
    @Override
    public void testControl() {
        super.testControl();
        assertIs("\n", 0x0085);
    }

    @Test
    @Override
    public void testStartPunctuation() {
        final String parenthesis = "(\u0028\u0F3A\u0F3C\u169B\u201A\u201E\u207D\u208D\u2308\u230A\u2329\uFF62";
        final String square = "[\u005B\u2045\u27E6\u298B\u298D\u298F";
        final String curly = "{\u007B\u2774\u2983";
        assertAll('(', parenthesis.codePoints());
        assertAll('[', square.codePoints());
        assertAll('{', curly.codePoints());
    }

    @Test
    @Override
    public void testEndPunctuation() {
        final String parenthesis = "(\u0028\u0F3A\u0F3C\u169B\u201A\u201E\u207D\u208D\u2308\u230A\u2329\uFF62";
        final String square = "]\u005D\u2046\u27E7\u298C\u298E\u2990";
        final String curly = "}\u007D\u2775\u2984";
        assertAll('(', parenthesis.codePoints());
        assertAll(']', square.codePoints());
        assertAll('}', curly.codePoints());
    }

    @Test
    @Override
    public void testQuotePunctuation() {
        final String left = "\u00AB\u201C\u201F\u2E02\u2E04\u2E09\u2E0C\u2E1C\u2E20";
        final String right = "\u00BB\u201D\u2E03\u2E05\u2E0A\u2E0D\u2E1D\u2E21";
        final String quote = "\"" + left + right + "\u02BA\u02EE\u2033\u3003";
        assertAll('"', quote.codePoints());

        final String quotes = "'\u2019\u201B\u2039\u203A";
        final String apostrophe = quotes + "\u02B9\u02BC\u2032";
        assertAll('\'', apostrophe.codePoints());
    }

    /*
     * SPECIFIC CODE POINT TESTS
     */

    @Test
    @Override
    public void testNumberSign() {
        final String keep = "#\u2114\u2317\u266F";
        assertAll('#', keep.codePoints());
    }

    @Test
    @Override
    public void testPercentSign() {
        final String keep = "%\u066A";
        assertAll('%', keep.codePoints());
    }

    @Test
    @Override
    public void testAmpersand() {
        final String keep = "&\u204A\u214B";
        assertAll('&', keep.codePoints());
        assertIs("&", 0x1F674);  // Heavy ampersand ornament
    }

    @Test
    @Override
    public void testBacktick() {
        final String keep = "`\u2035";
        assertAll('`', keep.codePoints());
    }

    @Test
    @Override
    public void testAsterisk() {
        final String keep = "*\u066D\u203B\u2042\u204E\u2051\u2217\u26B9\uA673";
        assertAll('*', keep.codePoints());
        assertIs("*", 0x1F7B6);
    }

    @Test
    @Override
    public void testPlus() {
        final String keep = "+\u02D6\u2795";
        assertAll('+', keep.codePoints());
    }

    @Test
    @Override
    public void testComma() {
        final String keep = ",\u060C\u066B\u2E41\u2E4C\u3001";
        assertAll(',', keep.codePoints());
    }

    @Test
    @Override
    public void testFullStop() {
        final String keep = ".\u00B7\u06D4\u2E3C\u3002";
        assertAll('.', keep.codePoints());
    }

    @Test
    @Override
    public void testSolidus() {
        final String keep = "/\u2044\u2215\u27CB\u29F8\u2E4A";
        assertAll('/', keep.codePoints());
    }

    @Test
    @Override
    public void testReverseSolidus() {
        final String keep = "\\\u27CD\u29F9";
        assertAll('\\', keep.codePoints());
    }

    @Test
    @Override
    public void testColon() {
        final String keep = ":\u02D0\u02F8\u1365\u205A\u205D\u2236\uA789";
        assertAll(':', keep.codePoints());
    }

    @Test
    @Override
    public void testExclamationMark() {
        final String exclamation = "!\u00A1\u01C3\u26A0\u2757\u2762\uA71D";
        assertAll('!', exclamation.codePoints());
    }

    @Test
    @Override
    public void testQuestionMark() {
        final String keep = "?\u00BF\u061F\u2753\u2E2E";
        assertAll('?', keep.codePoints());
    }

    @Test
    @Override
    public void testDollarSign() {
        final String keep = "$\u0024\uFE69\uFF04";
        assertAll('$', keep.codePoints());
    }

    @Test
    @Override
    public void testTilde() {
        final String keep = "~\u2053\u2E2F";
        assertAll('~', keep.codePoints());
    }

    /**
     * Most of these are redundant with cases covered elsewhere
     */
    @Test
    @Override
    public void testLigatures() {
        super.testLigatures();
        assertIs("ae", 0x00E6);

        assertIs("OE", 0x0152);
        assertIs("oe", 0x0153);
    }

    /**
     * CODE BLOCK TESTS
     */

    @Test
    @Override
    public void testLatin1Supplement() {
        assertEmptyRange(0x0080, 0x0084);
        assertIs("\n", 0x0085);
        assertEmptyRange(0x0086, 0x009F);
        assertIs(" ", 0x00A0);
        assertIs("!", 0x00A1);
        assertEmptyRange(0x00A2, 0x00A7);
        assertIs(" ", 0x00A8);
        assertNone(0x00A9);
        assertIs("a", 0x00AA);
        assertIs("\"", 0x00AB);
        assertNone(0x00AC);
        assertNone(0x00AD);
        assertNone(0x00AE);
        assertIs(" ", 0x00AF);
        assertNone(0x00B0);
        assertNone(0x00B1);
        assertIs("2", 0x00B2);
        assertIs("3", 0x00B3);
        assertNone(0x00B4);
        assertNone(0x00B5);
        assertNone(0x00B6);
        assertIs(".", 0x00B7);
        assertIs(" ", 0x00B8);
        assertIs("1", 0x00B9);
        assertIs("o", 0x00BA);
        assertIs("\"", 0x00BB);
        assertIs("1/4", 0x00BC);
        assertIs("1/2", 0x00BD);
        assertIs("3/4", 0x00BE);
        assertIs("?", 0x00BF);
        alphanumericSingles(0x00C0, 0x00FF, 0x00C6, 0x00D0, 0x00D7, 0x00DE, 0x00E6, 0x00F0, 0x00F7, 0x00FE);
        assertIs("AE", 0x00C6);
        assertNone(0x00D0);
        assertIs("*", 0x00D7);
        assertNone(0x00DE);
        assertIs("ae", 0x00E6);
        assertNone(0x00F0);
        assertIs("/", 0x00F7);
        assertNone(0x00FE);
    }

    @Test
    @Override
    public void testLatinExtendedA() {
        alphanumericSingles(0x0100, 0x017F,
                0x0132, 0x0133, 0x0149, 0x014A, 0x014B, 0x0152, 0x0153);
        assertIs("i", 0x0131);
        assertIs("IJ", 0x0132);
        assertIs("ij", 0x0133);
        assertIs("q", 0x0138);
        assertIs("'n", 0x0149);
        assertIs("NG", 0x014A);
        assertIs("ng", 0x014B);
        assertIs("OE", 0x0152);
        assertIs("oe", 0x0153);
    }

    @Test
    @Override
    public void testLatinExtendedB() {
        alphanumericSingles(0x0180, 0x024F,
                0x0184, 0x0185, 0x018D, 0x0194, 0x0195, 0x0196, 0x19B, 0x01A2, 0x01A3, 0x01A6, 0x01A7,
                0x0A18, 0x01A9, 0x01AA, 0x01B1, 0x01B7, 0x01B8, 0x01B9, 0x01BA, 0x01BC, 0x01BD, 0x01BE, 0x01BF, 0x01C0,
                0x01C1, 0x01C2, 0x01C3, 0x01C4, 0x01C5, 0x01C6, 0x01C7, 0x01C8, 0x01C9, 0x01CA, 0x01CB, 0x01CC, 0x01E3,
                0x01F1, 0x01F2, 0x01F3, 0x01F6, 0x01F7, 0x01FC, 0x01FD, 0x021C, 0x021D, 0x0222, 0x0223, 0x0238,
                0x0241, 0x0242);
        assertNone(0x0184, 0x0185, 0x018D, 0x0194, 0x0196, 0x19B, 0x01A7, 0x0A18, 0x01A9, 0x01AA,
                0x01B1, 0x01F6, 0x01F7, 0x021C, 0x021D, 0x0241, 0x0242);
        assertEmptyRange(0x01B7, 0x01C2);
        assertIs("O", 0x0186);
        assertIs("D", 0x0189);
        assertIs("hv", 0x0195);
        assertIs("M", 0x019C);
        assertIs("OI", 0x01A2);
        assertIs("oi", 0x01A3);
        assertIs("z", 0x01A6);
        assertIs("!", 0x01C3);
        assertIs("DZ", 0x01C4);
        assertIs("Dz", 0x01C5);
        assertIs("dz", 0x01C6);
        assertIs("LJ", 0x01C7);
        assertIs("Lj", 0x01C8);
        assertIs("lj", 0x01C9);
        assertIs("NJ", 0x01CA);
        assertIs("Nj", 0x01CB);
        assertIs("nj", 0x01CC);
        assertIs("e", 0x01DD);
        assertIs("ae", 0x01E3);
        assertIs("DZ", 0x01F1);
        assertIs("Dz", 0x01F2);
        assertIs("dz", 0x01F3);
        assertIs("AE", 0x01FC);
        assertIs("ae", 0x01FD);
        assertIs("OU", 0x0222);
        assertIs("ou", 0x0223);
        assertIs("j", 0x0237);
        assertIs("db", 0x0238);
        assertIs("qp", 0x0239);
        assertIs("V", 0x0245);
        assertIs("Q", 0x024A);
    }

    @Test
    @Override
    public void testLatinExtendedC() {
        alphanumericSingles(0x2C60, 0x2C7F,
                0x2C6D, 0x2C70, 0x2C77);
        assertNone(0x2C6D, 0x2C70, 0x2C77);
        assertIs("E", 0x2C7B);
    }

    @Test
    @Override
    public void testLatinExtendedD() {
        assertNone(0xA720, 0xA721, 0xA722, 0xA723, 0xA724, 0xA725, 0xA726, 0xA727, 0xA72A,
                0xA72B, 0xA72C, 0xA72D, 0xA72E, 0xA72F);
        alphanumericSingles(0xA73E, 0xA7FF,
                0xA74E, 0xA74F);
        assertNone(0xA75C, 0xA75D, 0xA788, 0xA78C, 0xA78F, 0xA7B3, 0xA7B4, 0xA7B5, 0xA7B6, 0xA7B7, 0xA7C0, 0xA7C1);
        assertEmptyRange(0xA764, 0xA769);
        assertEmptyRange(0xA76E, 0xA777);
        assertIs("TZ", 0xA728);
        assertIs("tz", 0xA729);
        assertIs("F", 0xA730);
        assertIs("S", 0xA731);
        assertIs("AA", 0xA732);
        assertIs("aa", 0xA733);
        assertIs("AO", 0xA734);
        assertIs("ao", 0xA735);
        assertIs("AU", 0xA736);
        assertIs("au", 0xA737);
        assertIs("AV", 0xA738);
        assertIs("av", 0xA739);
        assertIs("AV", 0xA73A);
        assertIs("av", 0xA73B);
        assertIs("AY", 0xA73C);
        assertIs("ay", 0xA73D);
        assertIs("OO", 0xA74E);
        assertIs("oo", 0xA74F);
        assertIs("VY", 0xA760);
        assertIs("vy", 0xA761);
        assertIs("ET", 0xA76A);
        assertIs("IS", 0xA76C);
        assertIs("is", 0xA76D);
        assertIs("um", 0xA778);
        assertIs(":", 0xA789);
        assertIs("AE", 0xA79A);
        assertIs("ae", 0xA79B);
        assertIs("OE", 0xA79C);
        assertIs("oe", 0xA79D);
        assertIs("UE", 0xA79E);
        assertIs("ue", 0xA79F);
        assertIs("Q", 0xA7AF);
    }

    @Test
    @Override
    public void testLatinExtendedE() {
        alphanumericSingles(0xAB30, 0xAB6F, 0xAB30, 0xAB40, 0xAB41, 0xAB42, 0xAB4D, 0xAB53,
                0xAB54, 0xAB55, 0xAB5B, 0xAB5C, 0xAB60, 0xAB62, 0xAB63, 0xAB64, 0xAB65, 0xAB66, 0xAB67);
        assertNone(0xAB30, 0xAB4D, 0xAB53, 0xAB54, 0xAB55, 0xAB5B, 0xAB5C, 0xAB60, 0xAB64,
                0xAB65, 0xAB6A, 0xAB6B, 0xAB6C, 0xAB6D, 0xAB6E);
        assertIs("l", 0xAB37);
        assertIs("oe", 0xAB40);
        assertIs("oe", 0xAB41);
        assertIs("oe", 0xAB42);
        assertIs("R", 0xAB46);
        assertIs("ui", 0xAB50);
        assertIs("ui", 0xAB51);
        assertIs("l", 0xAB5D);
        assertIs("oe", 0xAB62);
        assertIs("uo", 0xAB63);
        assertIs("dz", 0xAB66);
        assertIs("tx", 0xAB67);
    }

    @Test
    @Override
    public void testLatinExtendedAdditional() {
        alphanumericSingles(0x1E00, 0x1EFF,
                0x1E9F, 0x1EFA, 0x1EFB);
        assertNone(0x1E9F);
        assertIs("LL", 0x1EFA);
        assertIs("ll", 0x1EFB);
        assertIs("V", 0x1EFC);
        assertIs("v", 0x1EFD);
    }

    @Test
    @Override
    public void testHalfWidthAndFullwidth() {
        assertNone(0xFF00);
        for (int cp = 0xFF01; cp <= 0xFF60; cp++) {
            // fullwidth ASCII
            assertEquals(1, this.encoder.apply(cp).length);
        }
        alphanumericSingles(0xFF10, 0xFF19);
        // Based on category
        assertIs(".", 0xFF61);
        assertIs("(", 0xFF62);
        assertIs(")", 0xFF63);
        assertIs(",", 0xFF64);
        assertEmptyRange(0xFF65, 0xFFE2);
        // Based on category
        assertIs(" ", 0xFFE3);
        assertEmptyRange(0xFFE4, 0xFFEF);
    }

    @Test
    @Override
    public void testIPAExtensions() {
        alphanumericSingles(0x0250, 0x02AF,
                0x0251, 0x252, 0x0259, 0x025A, 0x0263, 0x0264, 0x0267, 0x0269, 0x026E, 0x0276, 0x0277,
                0x0278, 0x0283, 0x0285, 0x0286, 0x028A, 0x0292, 0x0293, 0x0294, 0x0295, 0x0296, 0x0298, 0x02A1,
                0x02A2, 0x02A3, 0x02A4, 0x02A5, 0x02A6, 0x02A7, 0x02A8, 0x02A9, 0x02AA, 0x02AC, 0x02AD);
        assertNone(0x0251, 0x02520, 0x0263, 0x0264, 0x0267, 0x0269, 0x026E, 0x0277, 0x0278, 0x0283, 0x0285,
                0x0286, 0x028A, 0x0292, 0x0293, 0x0294, 0x0295, 0x0296, 0x0298, 0x02A1, 0x02A2, 0x02A4, 0x02A7,
                0x02A9, 0x02AC, 0x02AD);
        assertIs("e", 0x0259);
        assertIs("e", 0x025A);
        assertIs("oe", 0x0276);
        assertIs("dz", 0x02A3);
        assertIs("dz", 0x02A5);
        assertIs("ts", 0x02A6);
        assertIs("tc", 0x02A8);
        assertIs("ls", 0x02AA);
        assertIs("lz", 0x02AB);
    }

    @Test
    @Override
    public void testPhoneticExtensions() {
        alphanumericSingles(0x1D2C, 0x1D7F,
                0x1D29, 0x1D2A, 0x1D2B, 0x1D2D, 0x1D2F, 0x1D3B, 0x1D3D, 0x1D45, 0x1D46, 0x1D4A, 0x1D4E,
                0x1D51, 0x1D5C, 0x1D5D, 0x1D5E, 0x1D5F, 0x1D60, 0x1D61, 0x1D66, 0x1D67, 0x1D68, 0x1D69, 0x1D6A, 0x1D6B,
                0x1D78, 0x1D7A, 0x1D7B, 0x1D7C, 0x1D7E, 0x1D7F);
        assertNone(0x1D29, 0x1D2A, 0x1D2B, 0x1D45, 0x1D51, 0x1D5C, 0x1D5D, 0x1D5E, 0x1D5F, 0x1D60, 0x1D61,
                0x1D66, 0x1D67, 0x1D69, 0x1D6A, 0x1D70A, 0x1D78, 0x1D7B, 0x1D7C, 0x1D7F);
        assertIs("AE", 0x1D01);
        assertIs("ae", 0x1D02);
        assertIs("oe", 0x1D14);
        assertIs("OU", 0x1D15);
        assertIs("AE", 0x1D2D);
        assertIs("B", 0x1D2F);
        assertIs("N", 0x1D3B);
        assertIs("OU", 0x1D3D);
        assertIs("ae", 0x1D46);
        assertIs("e", 0x1D4A);
        assertIs("i", 0x1D4E);
        assertIs("ue", 0x1D6B);
        assertIs("th", 0x1D7A);
        assertIs("u", 0x1D7E);
    }

    @Test
    @Override
    public void testPhoneticExtensionsSupplement() {
        alphanumericSingles(0x1D80, 0x1DBF,
                0x1D8B, 0x1D90, 0x1D95, 0x1D98, 0x1D9A, 0x1A9B, 0x1D9E, 0x1DA5, 0x1DA7, 0x1DB2, 0x1DB4,
                0x1DB7, 0x1DBE, 0x1DBF);
        assertNone(0x1D8B, 0x1D90, 0x1D98, 0x1D9A, 0x1A9B, 0x1D9E, 0x1DA5, 0x1DB2, 0x1DB4, 0x1DB7, 0x1DBE, 0x1DBF);
        assertIs("e", 0x1D95);
        assertIs("i", 0x1DA7);
    }

    /**
     * ■□▢▣▤▥▦▧▨▩▪▫▬▭▮▯▰▱▲△▴▵▶▷▸▹►▻▼▽▾▿◀◁◂◃◄◅◆◇◈◉◊○◌◍◎●◐◑◒◓◔◕◖◗◘◙◚◛◜◝◞◟◠◡◢◣◤◥◦◧◨◩◪◫◬◭◮◯◰◱◲◳◴◵◶◷◸◹◺◻◼◽◾◿
     */
    @Test
    @Override
    public void testGeometricShapes() {
        assertEmptyRange(0x2580, 0x259F);    // Geometric Shapes
        assertEmptyRange(0x1F780, 0x1F7AE);  // Geometric Shapes Extended
        assertAll('*', IntStream.rangeClosed(0x1F8AF, 0x1F7BF));
        assertEmptyRange(0x1F7C0, 0x1F7FF);  // Geometric Shapes Extended
    }

    @Test
    @Override
    public void testGameSymbols() {
        assertEmptyRange(0x2600, 0x266C);   // Chess, Checkers/Draughts, Japanese chess, card suits
        assertIs("b", 0x266D); // Musical flat sign
        assertNone(0x266E);             // Musical natural sign
        assertIs("#", 0x266F); // Musical sharp sign
        assertEmptyRange(0x2670, 0x269F);   // Chess, Checkers/Draughts, Japanese chess, card suits
        assertIs("!", 0x26A0); // Warning sign
        assertEmptyRange(0x26A1, 0x26B8);   // Chess, Checkers/Draughts, Japanese chess, card suits
        assertIs("*", 0x26B9); // Sextile
        assertEmptyRange(0x26BA, 0x26FF);   // Chess, Checkers/Draughts, Japanese chess, card suits

        assertEmptyRange(0x1FA00, 0x1FA6F); // Chess Symbols
        assertEmptyRange(0x1F030, 0x1F09F); // Domino Tiles
        assertEmptyRange(0x1F000, 0x1F02F); // Mahjong Tiles
        assertEmptyRange(0x1F0A0, 0x1F0FF); // Playing Cards
    }

    @Test
    @Override
    public void testMiscellaneousSymbolsAndArrows() {
        assertEmptyRange(0x2B00, 0x2B3F);
        assertIs("=", 0x2B40);
        assertIs("~", 0x2B41);
        assertEmptyRange(0x2B42, 0x2B46);
        assertIs("~", 0x2B47);
        assertNone(0x2B48);
        assertIs("~", 0x2B49);
        assertNone(0x2B4A);
        assertIs("~", 0x2B4B);
        assertIs("~", 0x2B4C);
        assertEmptyRange(0x2B4D, 0x2BF8);
        assertIs("=", 0x2BF9);
        assertEmptyRange(0x2BFA, 0x2BFF);
    }

    @Test
    @Override
    public void testSupplementalArrows() {
        assertEmptyRange(0x27F0, 0x27FF);   // Supplemental Arrows-A

        assertEmptyRange(0x2900, 0x2970);   // Supplemental Arrows-B
        assertIs("=", 0x2971);
        assertIs("~", 0x2972);
        assertIs("~", 0x2973);
        assertIs("~", 0x2974);
        assertEmptyRange(0x2975, 0x297F);   // Supplemental Arrows-B

        assertEmptyRange(0x1F800, 0x1F8FF); // Supplemental Arrows-C
    }
}
