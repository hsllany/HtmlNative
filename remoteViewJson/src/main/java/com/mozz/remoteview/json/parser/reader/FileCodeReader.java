package com.mozz.remoteview.json.parser.reader;

import java.io.EOFException;

/**
 * Created by Yang Tao on 17/2/21.
 */

public class FileCodeReader implements CodeReader {
    @Override
    public char nextCh() throws EOFException {
        return 0;
    }

    @Override
    public int line() {
        return 0;
    }

    @Override
    public char current() {
        return 0;
    }
}
