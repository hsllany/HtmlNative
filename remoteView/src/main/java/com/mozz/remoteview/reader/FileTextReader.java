package com.mozz.remoteview.reader;

import android.support.annotation.NonNull;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Created by Yang Tao on 17/2/21.
 */

public class FileTextReader extends StreamReader {

    public FileTextReader(@NonNull File file) throws FileNotFoundException {
        super(new FileReader(file));
    }

    public FileTextReader(@NonNull InputStream stream) {
        super(new InputStreamReader(stream));
    }
}
