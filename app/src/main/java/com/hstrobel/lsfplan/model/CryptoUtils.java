package com.hstrobel.lsfplan.model;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.Log;

import com.hstrobel.lsfplan.Constants;
import com.hstrobel.lsfplan.GlobalState;
import com.yakivmospan.scytale.Crypto;
import com.yakivmospan.scytale.KeyProps;
import com.yakivmospan.scytale.Store;

import java.math.BigInteger;
import java.security.KeyPair;
import java.util.Calendar;

import javax.security.auth.x500.X500Principal;

/**
 * Created by Henry on 29.09.2017.
 */

public class CryptoUtils {
    private static final String TAG = "LSF_CRYPTO";

    private final static int encryptionBlockSize = Constants.CRYPTO_KEY_SIZE / 8 - 11; // as specified for RSA/ECB/PKCS1Padding keys
    private final static int decryptionBlockSize = Constants.CRYPTO_KEY_SIZE / 8; // as specified for RSA/ECB/PKCS1Padding keys

    private final static Crypto crypto = new Crypto("RSA/ECB/PKCS1Padding", encryptionBlockSize, decryptionBlockSize);


    @Nullable
    public static String getStoreField(Context context, String name) {
        try {
            GlobalState state = GlobalState.getInstance();
            KeyPair key = initKeystore(context);

            String encryptedData = state.settings.getString(getPrefFieldName(name), null);

            if (encryptedData == null || key == null) return "";
            String decryptedData = crypto.decrypt(encryptedData, key.getPrivate(), false);

            Log.d(TAG, "getStoreField: decrypt " + decryptedData);
            return decryptedData;
        } catch (Exception ex) {
            Log.e(TAG, "getStoreField: ", ex);
        }

        return null;
    }

    public static void setStoreField(Context context, String name, String value) {
        try {
            GlobalState state = GlobalState.getInstance();
            KeyPair key = initKeystore(context);
            if (key == null) return;

            String encryptedData = crypto.encrypt(value, key.getPublic(), false);
            state.settings.edit().putString(getPrefFieldName(name), encryptedData).apply();
            String decryptedData = crypto.decrypt(encryptedData, key.getPrivate(), false);

            Log.d(TAG, "getStoreField: encrypt " + decryptedData);
        } catch (Exception ex) {
            Log.e(TAG, "getStoreField: ", ex);
        }
    }

    private static String getPrefFieldName(String name) {
        return String.format("ENC_%s_%s", Constants.CRYPTO_KEY_NAME, name);
    }

    @Nullable
    private static KeyPair initKeystore(Context context) {
        try {
            // Create and save key
            final Calendar start = Calendar.getInstance();
            final Calendar end = Calendar.getInstance();
            end.add(Calendar.YEAR, 25);

            Store store = new Store(context);
            if (!store.hasKey(Constants.CRYPTO_KEY_NAME)) {
                KeyProps keyProps = new KeyProps.Builder()
                        .setAlias(Constants.CRYPTO_KEY_NAME)
                        .setPassword(null)
                        .setKeySize(Constants.CRYPTO_KEY_SIZE)
                        .setKeyType("RSA")
                        .setSerialNumber(BigInteger.ONE)
                        .setSubject(new X500Principal("CN=" + Constants.CRYPTO_KEY_NAME + " CA Certificate"))
                        .setStartDate(start.getTime())
                        .setEndDate(end.getTime())
                        .setBlockModes("ECB")
                        .setEncryptionPaddings("PKCS1Padding")
                        .setSignatureAlgorithm("SHA256WithRSAEncryption")
                        .build();

                KeyPair key = store.generateAsymmetricKey(keyProps);
                Log.d(TAG, "initKeystore: new keys created");
                return key;
            }

            return store.getAsymmetricKey(Constants.CRYPTO_KEY_NAME, null);


        } catch (Exception ex) {
            Log.e(TAG, "initKeystore: ", ex);
        }
        return null;
    }


}
