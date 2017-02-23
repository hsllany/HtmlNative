package com.mozz.remoteview.parser.reader;

import java.io.EOFException;

/**
 * Created by Yang Tao on 17/2/21.
 */

public interface CodeReader {
    char nextCh() throws EOFException;

    int line();

    char current();
}
