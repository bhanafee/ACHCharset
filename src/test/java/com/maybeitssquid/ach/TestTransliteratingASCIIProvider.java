package com.maybeitssquid.ach;

import org.junit.jupiter.api.Test;

import java.nio.charset.Charset;
import java.nio.charset.spi.CharsetProvider;
import java.util.Iterator;

import static org.junit.jupiter.api.Assertions.*;

public class TestTransliteratingASCIIProvider {
    final String[] canonicals = {"X-ACH", "X-ACH-Newlines", "X-ACH-Aggressive", "X-US-ASCII-Transliterating"};

    @Test
    public void testCharsets() {
        final CharsetProvider provider = new TransliteratingASCIIProvider();
        Iterator<Charset> test = provider.charsets();
        assertNotNull(test);
        int count = 0;
        while (test.hasNext()) {
            final Charset charset = test.next();
            count += 1;
        }
        assertTrue(count >= 4, "Not enough predefined CharSets");
    }

    @Test
    public void testCharsetForName() {
        final CharsetProvider provider = new TransliteratingASCIIProvider();

        for (final String name: canonicals) {
            final Charset charset = provider.charsetForName(name);
            assertNotNull(charset);
            assertFalse(charset.isRegistered());
            assertTrue(charset.canEncode());
        }

        final Charset aliased = provider.charsetForName("ACH");
        assertNotNull(aliased);
        assertEquals(Charset.forName("X-ACH"), aliased);

        assertNull(provider.charsetForName(""));
        assertNull(provider.charsetForName("foo"));
    }

    @Test
    public void testSPIResource() {
        for (final String name: canonicals) {
            assertNotNull(Charset.forName(name));
        }

        final Charset aliased = Charset.forName("ACH");
        assertNotNull(aliased);
        assertEquals(Charset.forName("X-ACH"), aliased);
    }
}
