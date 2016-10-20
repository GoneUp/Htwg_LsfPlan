package com.hstrobel.lsfplan.model;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;

/**
 * Created by Henry on 20.10.2016.
 */

public class FileUtils {
    public static String streamToString(InputStream inputStream, String encoding) throws IOException {
        ByteArrayOutputStream result = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int length;
        while ((length = inputStream.read(buffer)) != -1) {
            result.write(buffer, 0, length);
        }
        return result.toString(encoding);
    }

    public static InputStream stringToInputstream(String input) throws IOException {
        return new ByteArrayInputStream(input.getBytes(Charset.forName("UTF-8")));
    }
}
