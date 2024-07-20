package com.maybeitssquid.ach;

import org.junit.jupiter.api.Test;

import java.nio.charset.Charset;
import java.util.Iterator;

import static org.junit.jupiter.api.Assertions.*;

public class TestACHCharsetProvider {
    @Test
    public void testCharsets() {
        final ACHCharsetProvider provider = new ACHCharsetProvider();
        Iterator<Charset> test = provider.charsets();
        assertNotNull(test);
        assertTrue(test.hasNext());
        assertNotNull(test.next());
        assertFalse(test.hasNext());
    }

    @Test
    public void testCharsetForName() {
        final ACHCharsetProvider provider = new ACHCharsetProvider();
        final Charset charset = provider.charsetForName("X-ACH");
        assertNotNull(charset);
        assertEquals("X-ACH", charset.name());

        final Charset aliased = provider.charsetForName("ACH");
        assertNotNull(aliased);
        assertEquals("X-ACH", aliased.name());

        assertNull(provider.charsetForName(null));
        assertNull(provider.charsetForName(""));
        assertNull(provider.charsetForName("foo"));
    }
}
