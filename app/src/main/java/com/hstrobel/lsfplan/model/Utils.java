package com.hstrobel.lsfplan.model;

import android.content.Context;
import android.util.Log;

import com.hstrobel.lsfplan.Constants;
import com.hstrobel.lsfplan.R;

import org.jsoup.Connection;
import org.jsoup.Jsoup;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.security.KeyStore;
import java.security.cert.Certificate;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.ArrayList;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManagerFactory;

import static com.hstrobel.lsfplan.GlobalState.TAG;


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

    public static Connection setupAppConnection(String url, Context c) {
        return Jsoup.connect(url)
                .sslSocketFactory(generateSocketFactory(c))
                .userAgent(Constants.NETWORK_USERAGENT)
                .timeout(Constants.NETWORK_TIMEOUT);
    }


    public static SSLSocketFactory generateSocketFactory(Context c) {

        try {
            CertificateFactory cf = CertificateFactory.getInstance("X.509");


            InputStream is = c.getResources().openRawResource(R.raw.cert_chain);
            BufferedInputStream bis = new BufferedInputStream(is);
            ArrayList<Certificate> caList = new ArrayList<>();

            while (bis.available() > 0) {
                Certificate cert = cf.generateCertificate(bis);
                Log.i(TAG, "ca=" + ((X509Certificate) cert).getSubjectDN());
                caList.add(cert);
            }
            bis.close();

// Create a KeyStore containing our trusted CAs
            String keyStoreType = KeyStore.getDefaultType();
            KeyStore keyStore = KeyStore.getInstance(keyStoreType);
            keyStore.load(null, null);
            for (Certificate certificate : caList) {
                keyStore.setCertificateEntry(certificate.getPublicKey().toString(), certificate);
            }


// Create a TrustManager that trusts the CAs in our KeyStore
            String tmfAlgorithm = TrustManagerFactory.getDefaultAlgorithm();
            TrustManagerFactory tmf = TrustManagerFactory.getInstance(tmfAlgorithm);
            tmf.init(keyStore);

// Create an SSLContext that uses our TrustManager
            SSLContext context = SSLContext.getInstance("TLS");
            context.init(null, tmf.getTrustManagers(), null);

            return context.getSocketFactory();
        } catch (Exception ex) {
            Log.e(TAG, "SSL failed ", ex);
            return null;
        }
    }

}
