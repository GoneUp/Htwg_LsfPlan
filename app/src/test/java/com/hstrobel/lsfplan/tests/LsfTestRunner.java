package com.hstrobel.lsfplan.tests;

/**
 * Created by Henry on 30.09.2017.
 */

import org.junit.runners.model.InitializationError;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;


public class LsfTestRunner extends RobolectricTestRunner {

    public LsfTestRunner(Class<?> testClass) throws InitializationError {
        super(testClass);
    }


    @Override
    protected Config buildGlobalConfig() {
        return new Config.Builder()
                .setSdk(22)
                .setManifest("src/main/AndroidManifest.xml")
                .build();
    }
}