package com.maybeitssquid.ach;

/**
 * Character set that allows only the ACH-safe subset of US-ASCII. It allows characters in the range 0x1F to 0x7F,
 * exclusive, plus linefeed (0x0A).
 */
public class ACHCharset extends ASCIISubset {

    /**
     * Initializes a new ACH charset.
     */
    public ACHCharset() {
        super("X-ACH", new String[] {"ACH"}, false, '\n');
    }
}
