package com.maybeitssquid.ach;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class TestNormalizing extends TestASCIIFilter {
    @BeforeEach
    protected void setUp() {
        this.encoder = new Normalizing();
    }

    @Test
    @Override
    public void testDecimalDigits() {
        super.testDecimalDigits();
        testDecimalDigits(0xFF10); // Fullwidth
    }

    @Test
    public void testEllipses() {
        assertIs("...", 0x2026); // Horizontal ellipsis
        assertIs("...", 0xFE19); // Vertical ellipsis
    }

    @Test
    public void testLigatures() {
        assertIs("IJ", 0x0132);
        assertIs("ij", 0x0133);

        assertIs("ff", 0xFB00);
        assertIs("fi", 0xFB01);
        assertIs("fl", 0xFB02);
        assertIs("ffi", 0xFB03);
        assertIs("ffl", 0xFB04);
        assertIs("st", 0xFB05);
        assertIs("st", 0xFB06);
    }

    @Test
    @Override
    public void testLatin1Supplement() {
        assertEmptyRange(0x0080, 0x009F);
        assertIs(" ", 0x00A0);
        assertEmptyRange(0x00A1, 0x009F);
    }

    @Test
    @Override
    public void testLatinExtendedA() {
        alphanumericSingles(0x0100, 0x017F,
                0x0110, 0x0132, 0x0133);
        assertNone(0x0110);
        assertIs("IJ", 0x0132);
        assertIs("ij", 0x0133);
    }

    @Test
    @Override
    public void testLatinExtendedB() {
        alphanumericSingles(0x0180, 0x024F,
                0x0180, 0x0184, 0x0185, 0x0186, 0x0189, 0x018D, 0x0194, 0x0195, 0x0196, 0x19B, 0x01A2,
                0x01A3, 0x01A6, 0x01A7, 0x0A18, 0x01A9, 0x01AA, 0x01B1, 0x01B7, 0x01B8, 0x01B9, 0x01BA, 0x01BC, 0x01BD,
                0x01BE, 0x01BF, 0x01C0, 0x01C1, 0x01C2, 0x01C3, 0x01C4, 0x01C5, 0x01C6, 0x01C7, 0x01C8, 0x01C9, 0x01CA,
                0x01CB, 0x01CC, 0x01E3, 0x01F1, 0x01F2, 0x01F3, 0x01F6, 0x01F7, 0x01FC, 0x01FD, 0x021C, 0x021D, 0x0222,
                0x0223, 0x0238, 0x0241, 0x0242);
        assertNone(0x0180, 0x0184, 0x0185, 0x0186, 0x0189, 0x018D, 0x0194, 0x0195, 0x0196, 0x19B, 0x19C,
                0x01A2, 0x01A3, 0x01A6, 0x01A7, 0x0A18, 0x01A9, 0x01AA, 0x01B1, 0x01DC3, 0x01DD, 0x01E3, 0x01F6, 0x01F7,
                0x01FC, 0x01FD, 0x021C, 0x021D, 0x0222, 0x0223, 0x0237, 0x0238, 0x0239, 0x0241, 0x0242, 0x024A);
        assertEmptyRange(0x01B7, 0x01C2);
        assertIs("DZ", 0x01C4);
        assertIs("Dz", 0x01C5);
        assertIs("dz", 0x01C6);
        assertIs("LJ", 0x01C7);
        assertIs("Lj", 0x01C8);
        assertIs("lj", 0x01C9);
        assertIs("NJ", 0x01CA);
        assertIs("Nj", 0x01CB);
        assertIs("nj", 0x01CC);
        assertIs("DZ", 0x01F1);
        assertIs("Dz", 0x01F2);
        assertIs("dz", 0x01F3);
    }

    @Test
    @Override
    public void testLatinExtendedC() {
        alphanumericSingles(0x2C60, 0x2C7F,
                0x02C60, 0x2C6D, 0x2C70, 0x2C77);
        assertNone(0x02C60, 0x2C6D, 0x2C70, 0x2C77);
    }

    @Test
    @Override
    public void testLatinExtendedAdditional() {
        alphanumericSingles(0x1E00, 0x1EFF,
                0x1E9C, 0x1E9F, 0x1EFA, 0x1EFB, 0x01EFC, 0x01EFD);
        assertNone(0x01E9C, 0x01EFA, 0x01EFB, 0x01EFC, 0x01EFD, 0x1E9F);
    }

    @Test
    @Override
    public void testHalfWidthAndFullwidth() {
        assertNone(0xFF00);
        for (int cp = 0xFF01; cp <= 0xFF5E; cp++) {
            // fullwidth ASCII
            assertEquals(1, this.encoder.apply(cp).length, String.format("Wrong length for %04X", cp));
        }
        assertEmptyRange(0xFF5F, 0xFFE2);
        assertIs(" ", 0xFFE3);
        assertEmptyRange(0xFFE4, 0xFFEF);
    }

    @Test
    @Override
    public void testPhoneticExtensions() {
        assertEmptyRange(0x1D00, 0x1D2B);
        alphanumericSingles(0x1D2C, 0x1D7F,
                0x1D29, 0x1D2A, 0x1D2B, 0x1D2D, 0x1D2F, 0x1D3B, 0x1D3D, 0x1D45, 0x1D46, 0x1D4A,
                0x1D4E, 0x1D51, 0x1D5C, 0x1D5D, 0x1D5E, 0x1D5F, 0x1D60, 0x1D61, 0x1D66, 0x1D67, 0x1D68, 0x1D69, 0x1D6A,
                0x1D6B, 0x1D78, 0x1D7A, 0x1D7B, 0x1D7C, 0x1D7E, 0x1D7F);
        assertNone(0x1D29, 0x1D2A, 0x1D2B, 0x1D2D, 0x1D2F, 0x1D3B,  0x1D3D, 0x1D45, 0x1D46, 0x1D4A,
                0x1D4E, 0x1D51, 0x1D5C, 0x1D5D, 0x1D5E, 0x1D5F, 0x1D60, 0x1D61, 0x1D66, 0x1D67, 0x1D68, 0x1D69, 0x1D6A,
                0x1D6B, 0x1D70A, 0x1D78, 0x1D7B, 0x1D7C, 0x1D7E, 0x1D7F);
    }

    @Test
    @Override
    public void testPhoneticExtensionsSupplement() {
        assertNone(0x1D80);
        alphanumericSingles(0x1D80, 0x1DBF,
                0x1D80, 0x1D8B, 0x1D90, 0x1D95, 0x1D98, 0x1D9A, 0x1A9B, 0x1D9E, 0x1DA5, 0x1DA7, 0x1DB2,
                0x1DB4, 0x1DB7, 0x1DBE, 0x1DBF);
        assertNone(0x1D8B, 0x1D90, 0x1D95, 0x1D98, 0x1D9A, 0x1A9B, 0x1D9E, 0x1DA5, 0x1DA7, 0x1DB2, 0x1DB4,
                0x1DB7, 0x1DBE, 0x1DBF);
    }

    @Test
    @Override
    public void testSuperscriptsAndSubscripts() {
        assertIs("0", 0x2070);
        assertIs("i", 0x2071);
        assertNone(0x2072, 0x2073);
        alphanumericSingles(0x2074, 0x209F,
                0x207A, 0x208F, 0x2094);
        assertNone(0x208F, 0x2094, 0x209D, 0x209E, 0x209F);

    }

    /**
     * Latin ligatures
     */
    @Test
    public void testAlphabeticPresentationForms() {
        assertIs("ff", 0xFB00);
        assertIs("fi", 0xFB01);
        assertIs("fl", 0xFB02);
        assertIs("ffi", 0xFB03);
        assertIs("ffl", 0xFB04);
        assertIs("st", 0xFB05);
        assertIs("st", 0xFB06);
        assertEmptyRange(0xFB07, 0xFB28);
        assertIs("+", 0xFB29); // HEBREW LETTER ALTERNATIVE PLUS SIGN
        assertEmptyRange(0xFB2A, 0xFB4F);
    }
}