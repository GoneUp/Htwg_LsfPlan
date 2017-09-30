package com.hstrobel.lsfplan.tests;

import android.content.Context;

import com.hstrobel.lsfplan.BuildConfig;
import com.hstrobel.lsfplan.model.CryptoUtils;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;

import static org.junit.Assert.assertEquals;

/**
 * Created by Henry on 29.09.2017.
 */

@RunWith(RobolectricTestRunner.class)
@Config(manifest = "src/main/AndroidManifest.xml", sdk = 24, constants = BuildConfig.class)
public class CryptoUtilsTest {
    private Context appContext = RuntimeEnvironment.application;

    @Test
    public void getSetStoreField_Vaild() throws Exception {
        String input = "1234";

        CryptoUtils.setStoreField(appContext, "Valid", input);
        String decrypt = CryptoUtils.getStoreField(appContext, "Valid");
        assertEquals(input, decrypt);
    }

    @Test
    public void setStoreField() throws Exception {

    }

}