package com.maybeitssquid.ach;

import java.nio.charset.Charset;
import java.nio.charset.spi.CharsetProvider;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

/**
 * Provider for preconfigured versions of {@link TransliteratingASCII}:
 *
 * <dl>
 *     <dt>X-ACH-Filter</dt>
 *     <dd>A strict interpretation of characters valid for ACH files. Allows for 0x20 through 0x7E, inclusive.
 *     Control characters, including newlines, are reported as unmappable.</dd>
 *     <dt>X-ACH-Newlines</dt>
 *     <dd>Allows the same characters as {@code X-ACH-Filter} and also allows linefeed {@code 0x0A} and carriage
 *     return {@code 0x0D}.</dd>
 *     <dt>X-ACH-Aggressive</dt>
 *     <dd>Decodes the same characters as {@code X-ACH-Newlines} and encodes the same characters with aggressive
 *     transliteration using {@link Naming}.</dd>
 *     <dt>X-US-ASCII-Aggressive</dt>
 *     <dd>Decodes as plain US-ASCII and encodes with aggressive transliteration using {@link Naming}.</dd>
 * </dl>
 */
public class TransliteratingASCIIProvider extends CharsetProvider {

    private Charset achFilter;

    private Charset achNewlines;

    private Charset achAggressive;

    private Charset usAsciiAggressive;

    private Charset getACHFilter() {
        if (achFilter == null) {
            Filtering transliterator = new Filtering().blockControls();
            achFilter = new TransliteratingASCII("X-ACH", new String[] {"ACH"}, transliterator);
        }
        return achFilter;
    }

    private Charset getACHNewlines() {
        if (achNewlines == null) {
            Filtering transliterator = new Filtering().blockControls()
                    .encode(0x0A, '\n')
                    .encode(0x0D, '\r');
            achNewlines = new TransliteratingASCII("X-ACH-Newlines", new String[0], transliterator);
        }
        return achNewlines;
    }

    private Charset getACHAggressive() {
        if (achAggressive == null) {
            Filtering transliterator = new Naming().blockControls()
                    .encode(0x0A, '\n')
                    .encode(0x0D, '\r');
            achAggressive = new TransliteratingASCII("X-ACH-Aggressive", new String[0], transliterator);
        }
        return this.achAggressive;
    }

    private Charset getUSASCIIAggressive() {
        if (usAsciiAggressive == null) {
            Filtering transliterator = new Naming();
            usAsciiAggressive = new TransliteratingASCII("X-US-ASCII-transliterating", new String[0], transliterator);
        }
        return usAsciiAggressive;
    }

    private List<Charset> charsets;

    @Override
    public Iterator<Charset> charsets() {
        synchronized (this) {
            if (charsets == null) {
                charsets = Arrays.asList(getACHFilter(), getACHNewlines(), getACHAggressive(), getUSASCIIAggressive());
            }
        }
        return charsets.iterator();
    }

    @Override
    public Charset charsetForName(String charsetName) {
        switch (charsetName) {
            case "ACH":
            case "X-ACH": return getACHFilter();
            case "X-ACH-Newlines": return getACHNewlines();
            case "X-ACH-Aggressive": return getACHAggressive();
            case "X-US-ASCII": return getUSASCIIAggressive();
        }
        return null;
    }
}
