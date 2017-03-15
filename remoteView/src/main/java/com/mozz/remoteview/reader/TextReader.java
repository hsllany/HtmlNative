package com.mozz.remoteview.reader;

import java.io.EOFException;

/**
 * Created by Yang Tao on 17/2/21.
 */

public interface TextReader {
    char nextCh() throws EOFException;

    long line();

    long column();

    char current();

    void close();
}
