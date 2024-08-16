package com.maybeitssquid.ach;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.charset.Charset;
import java.nio.charset.CharsetEncoder;
import java.nio.charset.CodingErrorAction;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class TestTransliterate {

    ByteArrayOutputStream bytes;
    Writer writer;

    @BeforeEach
    protected void setUp() {
        bytes = new ByteArrayOutputStream();
        final Charset charset = new Transliterate();
        final CharsetEncoder encoder = charset.newEncoder().onUnmappableCharacter(CodingErrorAction.IGNORE);
        writer = new OutputStreamWriter(bytes, encoder);
    }

    private String transliterate(final String input) {
        try {
            bytes.reset();
            writer.write(input);
            writer.flush();
            return bytes.toString(StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void test(final String skip, final String keep, final String expected) {
        for (int i = 0; i < skip.length(); i++) {
            final String ch = String.valueOf(skip.charAt(i));
            assertEquals("", transliterate(ch), String.format("Should be skipped '%s' (%x) at %d", ch, (int) ch.charAt(0), i));
        }

        assertEquals("", transliterate(skip));
        final String transliterated = transliterate(keep);
        for (int i = 0; i < keep.length(); i++) {
            assertEquals(expected.charAt(i), transliterated.charAt(i));
        }
    }

    @Test
    public void testBasicLatin() {
        final String fox = "The quick brown fox jumps over the lazy dog";
        test("", fox, fox);
        final String more = "!\"#$%&'()*+,-./0123456789:;<=>?@ABCDEFGHIJKLMNOPQRSTUVWXYZ[\\]^_`abcdefghijklmnopqrstuvwxyz{|}~";
        test("", more, more);
    }


    @Test
    public void testIPAExtensions() {
        final String skip = "ɐɑɒɓɔɕɖɗɘəɚɛɜɝɞɟɠɡɢɣɤɥɦɧɨɩɪɫɬɭɮɯɰɱɲɳɴɵɶɷɸɹɺɻɼɽɾɿʀʁʂʃʄʅʆʇʈʉʊʋʌʍʎʏʐʑʒʓʔʕʖʗʘʙʚʛʜʝʞʟʠʡʢʣʤʥʦʧʨʩʪʫʬʭ";
        test(skip, "", "");
    }

    @Test
    public void testCyrillic() {
        final String skip = "ЀЁЂЃЄЅІЇЈЉЊЋЌЍЎЏАБВГДЕЖЗИЙКЛМНОПРСТУФХЦЧШЩЪЫЬЭЮЯабвгдежзийклмнопрстуфхцчшщъыьэюяѐёђѓєѕіїјљњћќѝўџѠѡѢѣѤѥѦѧѨѩѪѫѬѭѮѯѰѱѲѳѴѵѶѷѸѹѺѻѼѽѾѿ";
        final String keep = "...";
        final String expected = "...";
        test(skip, keep, expected);

        final String supplementarySkip = "ԀԁԂԃԄԅԆԇԈԉԊԋԌԍԎԏ";
        test(supplementarySkip, "", "");
    }

//    @Test
//    public void testEnclosedAlphanumerics() {
//        final String skip = "①②③④⑤⑥⑦⑧⑨⑩⑪⑫⑬⑭⑮⑯⑰⑱⑲⑳⑴⑵⑶⑷⑸⑹⑺⑻⑼⑽⑾⑿⒀⒁⒂⒃⒄⒅⒆⒇⒈⒉⒊⒋⒌⒍⒎⒏⒐⒑⒒⒓⒔⒕⒖⒗⒘⒙⒚⒛⒜⒝⒞⒟⒠⒡⒢⒣⒤⒥⒦⒧⒨⒩⒪⒫⒬⒭⒮⒯⒰⒱⒲⒳⒴⒵ⒶⒷⒸⒹⒺⒻⒼⒽⒾⒿⓀⓁⓂⓃⓄⓅⓆⓇⓈⓉⓊⓋⓌⓍⓎⓏⓐⓑⓒⓓⓔⓕⓖⓗⓘⓙⓚⓛⓜⓝⓞⓟ...";
//        test(skip, "", "");
//    }

    @Test
    public void testOpticalCharacterRecognition() {
        final String skip = "⑀⑁⑂⑃⑄⑅⑆⑇⑈⑉⑊";
        test(skip, "", "");
    }

    @Test
    public void testBoxDrawing() {
        final String skip = "─━│┃┄┅┆┇┈┉┊┋┌┍┎┏┐┑┒┓└┕┖┗┘┙┚┛├┝┞┟┠┡┢┣┤┥┦┧┨┩┪┫┬┭┮┯┰┱┲┳┴┵┶┷┸┹┺┻┼┽┾┿╀╁╂╃╄╅╆╇╈╉╊╋╌╍╎╏═║╒╓╔╕╖╗╘╙╚╛╜╝╞╟╠╡╢╣╤╥╦╧╨╩╪╫╬╭╮╯╰╱╲╳╴╵╶╷╸╹╺╻╼╽╾╿";
        test(skip, "", "");
    }

    @Test
    public void testBlockElements() {
        final String skip = "▀▁▂▃▄▅▆▇█▉▊▋▌▍▎▏▐░▒▓▔▕▖▗▘▙▚▛▜▝▞▟";
        test(skip, "", "");
    }

    @Test
    public void testGeometricShapes() {
        final String skip = "■□▢▣▤▥▦▧▨▩▪▫▬▭▮▯▰▱▲△▴▵▶▷▸▹►▻▼▽▾▿◀◁◂◃◄◅◆◇◈◉◊○◌◍◎●◐◑◒◓◔◕◖◗◘◙◚◛◜◝◞◟◠◡◢◣◤◥◦◧◨◩◪◫◬◭◮◯◰◱◲◳◴◵◶◷◸◹◺◻◼◽◾◿";
        test(skip, "", "");
    }

    @Test
    public void testMiscellaneousSymbols() {
        final String skip = "☀☁☂☃☄★☆☇☈☉☊☋☌☍☎☏☐☑☒☓☖☗☙☚☛☜☝☞☟☠☡☢☣☤☥☦☧☨☩☪☫☬☭☮☯☰☱☲☳☴☵☶☷☸☹☺☻☼☽☾☿♀♁♂♃♄♅♆♇♈♉♊♋♌♍♎♏♐♑♒♓♔♕♖♗♘♙♚♛♜♝♞♟♠♡♢♣♤♥♦♧♨♩♪♫♬♭♮♰♱♲♳♴♵♶♷♸♹♺♻♼♽⚀⚁⚂⚃⚄";
        final String keep = "♯...";
        final String expected = "#...";
        test(skip, keep, expected);
    }

    @Test
    public void testMiscellaneousMathematicalSymbolsA() {
        final String skip = "⟐⟑⟒⟓⟔⟕⟖⟗⟘⟙⟚⟛⟜⟝⟞⟟⟠⟡⟢⟣⟤⟥";
        final String keep = "⟦⟧⟨⟩⟪⟫";
        final String expected = "[]()()";
        test(skip, keep, expected);
    }

    @Test
    public void testSupplementalArrowsA() {
        final String skip = "⟰⟱⟲⟳⟴⟵⟶⟷⟸⟹⟺⟻⟼⟽⟾⟿";
        test(skip, "", "");
    }

    @Test
    public void testBraillePatterns() {
        final String skip = "⠀⠁⠂⠃⠄⠅⠆⠇⠈⠉⠊⠋⠌⠍⠎⠏⠐⠑⠒⠓⠔⠕⠖⠗⠘⠙⠚⠛⠜⠝⠞⠟⠠⠡⠢⠣⠤⠥⠦⠧⠨⠩⠪⠫⠬⠭⠮⠯⠰⠱⠲⠳⠴⠵⠶⠷⠸⠹⠺⠻⠼⠽⠾⠿⡀⡁⡂⡃⡄⡅⡆⡇⡈⡉⡊⡋⡌⡍⡎⡏⡐⡑⡒⡓⡔⡕⡖⡗⡘⡙⡚⡛⡜⡝⡞⡟⡠⡡⡢⡣⡤⡥⡦⡧⡨⡩⡪⡫⡬⡭⡮⡯⡰⡱⡲⡳⡴⡵⡶⡷⡸⡹⡺⡻⡼⡽⡾⡿";
        test(skip, "...", "...");
    }

    @Test
    public void testDashPunctuation() {
        final String keep = "\u002D\u058A\u05BE\u1400\u1806\u2010\u2011\u2012\u2013\u2014\u2015\u2E17\u2E1A" +
                "\u2E3B\u2E40\u2E5D\u301C\u3030\u30A0\uFE31\uFE32\uFE58\uFE63\uFF0D";

        test("", keep, "-".repeat(keep.length()));
        assertEquals("-", transliterate(Character.toString(0x10EAD)));
    }

    @Test
    public void testConnectorPunctuation() {
        final String keep = "\u005F\u203F\u2040\u2054\uFE33\uFE34\uFE4D\uFE4E\uFE4F\uFF3F";
        test("", keep, "_".repeat(keep.length()));
    }

    @Test
    public void testSpaceSeparator() {
        final String keep = "\u0020\u00A0\u1680\u2000\u2001\u2002\u2003\u2004\u2005\u2006\u2007\u2008\u2009" +
                "\u200A\u202F\u205F\u3000";
        test("", keep, " ".repeat(keep.length()));
    }

    @Test
    public void testApostrophe() {
        final String left = "\u201B\u201B\u2039\u2039";
        final String right = "\u2019\u203A";
        final String keep = left + right;
        test("", keep, "'".repeat(keep.length()));
    }

    @Test
    public void testQuote() {
        final String left = "\u00AB\u201C\u201F\u2E02\u2E04\u2E09\u2E0C\u2E1C\u2E20";
        final String right = "\u00BB\u201D\u2E03\u2E05\u2E0A\u2E0D\u2E1D\u2E21";
        final String keep = left + right;
        test("", keep, "\"".repeat(keep.length()));
    }

    @Test
    public void testStartPunctuation() {
        final String parenthesis = "\u0028\u0F3A\u0F3C\u169B\u201A\u201E\u207D\u208D\u2308\u230A\u2329\uFF62";
        test("", parenthesis, "(".repeat(parenthesis.length()));
        final String square = "\u005B\u2045\u27E6\u298B\u298D\u298F";
        test("", square, "[".repeat(square.length()));
        final String curly = "\u007B\u2774\u2983";
        test("", curly, "{".repeat(curly.length()));
    }

    @Test
    public void testEndPunctuation() {
        final String parenthesis = "\u0029\u0F3B\u0F3D\u169C\u207E\u208E\u2309\u230B\u232A\uFF63";
        test("", parenthesis, ")".repeat(parenthesis.length()));
        final String square = "\u005D\u2046\u27E7\u298C\u298E\u2990";
        test("", square, "]".repeat(square.length()));
        final String curly = "\u007D\u2775\u2984";
        test("", curly, "}".repeat(curly.length()));
    }

    @Test
    public void testSeparator() {
        final String keep = "\u2028\u2029";
        test("", keep, "\n\n");
    }

    @Test
    public void testReplacement() {
        final String keep = "\uFFFD";
        test("", keep, "?");
    }

    @Test
    public void testDecimalDigit() {
        final String ascii = "0123456789";
        test("", ascii, ascii);
        final String arabic = "\u0660\u0661\u0662\u0663\u0664\u0665\u0666\u0667\u0668\u0669";
        test("", arabic, ascii);
        final String extendedArabic = "\u06F0\u06F1\u06F2\u06F3\u06F4\u06F5\u06F6\u06F7\u0668\u06F9";
        test("", extendedArabic, ascii);
        final String thai = "\u0E50\u0E51\u0E52\u0E53\u0E54\u0E55\u0E56\u0E57\u0E58\u0E59";
        test("", thai, ascii);
        final String sudanese = "\u1BB0\u1BB1\u1BB2\u1BB3\u1BB4\u1BB5\u1BB6\u1BB7\u1BB8\u1BB9";
        test("", sudanese, ascii);
        final String fullwidth = "\uFF10\uFF11\uFF12\uFF13\uFF14\uFF15\uFF16\uFF17\uFF18\uFF19";
        test("", fullwidth, ascii);

        assertEquals("0", transliterate(Character.toString(0x10D30)));
    }

    @Test
    public void testEllipses() {
        assertEquals("...", transliterate(Character.toString(0x1801))); // Mongolian ellipsis
        assertEquals("...", transliterate(Character.toString(0x2026))); // Horizontal ellipsis
        assertEquals("...", transliterate(Character.toString(0xFE19))); // Vertical ellipsis
    }
}
