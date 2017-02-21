package com.mozz.remoteview.json.parser.reader;

import java.io.EOFException;
import java.io.IOException;

/**
 * Created by Yang Tao on 17/2/21.
 */

public interface CodeReader {
    char nextCh() throws EOFException;

    int line();

    char current();
}
