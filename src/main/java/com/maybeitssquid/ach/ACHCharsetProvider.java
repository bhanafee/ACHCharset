package com.maybeitssquid.ach;

import java.nio.charset.Charset;
import java.nio.charset.spi.CharsetProvider;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

@SuppressWarnings("unused")
public class ACHCharsetProvider extends CharsetProvider {

    private static final Charset ACH = new ACHCharset();
    private static final List<Charset> ACH_LIST = Collections.singletonList(ACH);

    /**
     * {@inheritDoc}
     *
     * @return {@inheritDoc}
     */
    @Override
    public Iterator<Charset> charsets() {
        return ACH_LIST.iterator();
    }

    /**
     * Retrieves a charset for the given charset name.
     *
     * @param charsetName {@inheritDoc}
     * @return {@inheritDoc}
     */
    @Override
    public Charset charsetForName(final String charsetName) {
        if (ACH.name().equals(charsetName) || ACH.aliases().contains(charsetName)) return ACH;
        else return null;
    }
}
