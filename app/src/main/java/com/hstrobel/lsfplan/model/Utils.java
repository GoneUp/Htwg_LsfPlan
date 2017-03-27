package com.hstrobel.lsfplan.model;

import android.content.Context;

import com.hstrobel.lsfplan.Constants;
import com.hstrobel.lsfplan.R;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;

/**
 * Created by Henry on 20.10.2016.
 */

public class Utils {

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

    private static String getBaseUrl(Context c, int mode) {
        if (mode == Constants.MODE_HTWG) {
            return c.getString(R.string.misc_baseUrl_HTWG);
        } else if (mode == Constants.MODE_UNI_KN) {
            return c.getString(R.string.misc_baseUrl_UNI_KN);
        }
        return "";
    }

    public static String getLoginUrl(Context c, int mode) {
        return getBaseUrl(c, mode) + c.getString(R.string.misc_personalLoginURL);
    }

    public static String getPersonalPlanUrl(Context c, int mode) {
        return getBaseUrl(c, mode) + c.getString(R.string.misc_personalPlanURL);
    }

    public static String getCoursesOverviewUrl(Context c, int mode) {
        return getBaseUrl(c, mode) + c.getString(R.string.misc_coursesOverviewURL);
    }
}
