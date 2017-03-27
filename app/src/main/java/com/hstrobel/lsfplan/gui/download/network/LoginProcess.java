package com.hstrobel.lsfplan.gui.download.network;

import android.os.AsyncTask;
import android.util.Log;

import com.hstrobel.lsfplan.GlobalState;
import com.hstrobel.lsfplan.gui.download.NativeSelector;
import com.hstrobel.lsfplan.model.Utils;

import org.jsoup.Jsoup;

/**
 * Created by Henry on 07.12.2015.
 */


public class LoginProcess extends AsyncTask<String, String, String> {
    private static final String TAG = "LSF";
    private NativeSelector context = null;

    public LoginProcess(NativeSelector c) {
        context = c;
    }

    //1. post, 2. handle answer (cookie or a fail message), 3.return cookie?
    /*
    POST /qisserver/rds?state=user&type=1&category=auth.login&re=last&startpage=portal.vm&breadCrumbSource=portal HTTP/1.1
    Host: lsf.htwg-konstanz.de
    Connection: keep-alive
    Content-Length: 45
    Cache-Control: max-age=0
    Accept: text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,;q=0.8
Origin: https://lsf.htwg-konstanz.de
Upgrade-Insecure-Requests: 1
User-Agent: Mozilla/5.0 (Windows NT 6.1; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/48.0.2564.23 Safari/537.36
Content-Type: application/x-www-form-urlencoded
Referer: https://lsf.htwg-konstanz.de/qisserver/rds?state=wlogin&login=in&breadCrumbSource=
Accept-Encoding: gzip, deflate
Accept-Language: de-DE,de;q=0.8,en-US;q=0.6,en;q=0.4
Cookie: JSESSIONID=2A21ACB9733CC49DE90888A6BEF91DFD.lsf; download-complete=true; JSESSIONID=C4382B1EAC218C0D8532FAB32C7DB724.lsf

DATA:
asdf:henstrob
fdsa:xx_pw_xx
submit:Anmelden

Successfull response
HTTP/1.1 302 Found
Date: Mon, 07 Dec 2015 18:40:39 GMT
Server: Apache/2.2.22 (Ubuntu)
Strict-Transport-Security: 86400; includeSubDomains
Set-Cookie: JSESSIONID=35611D98B400C611BAA52972FA250B2A.lsf; Path=/qisserver/; Secure; HttpOnly
Location: https://lsf.htwg-konstanz.de/qisserver/rds?state=wplan&act=stg&pool=stg&show=plan&P.vx=kurz&r_zuordabstgv.semvonint=1&r_zuordabstgv.sembisint=1&missing=allTerms&k_parallel.parallelid=&k_abstgv.abstgvnr=4511&r_zuordabstgv.phaseid=&chco=y
Content-Length: 0
Keep-Alive: timeout=5, max=99
Connection: Keep-Alive



*/


    @Override
    protected String doInBackground(String... params) {
        try {
            String user = params[0];
            String pw = params[1];


            org.jsoup.Connection connection = Jsoup.connect(Utils.getLoginUrl(context, GlobalState.getInstance().getCollege()))
                    .data("asdf", user)
                    .data("fdsa", pw)
                    .data("submit", "Anmelden")
                    .timeout(NativeSelector.TIMEOUT)
                    .userAgent("Mozilla");
            // and other hidden fields which are being passed in post request.
            connection.post();
            org.jsoup.Connection.Response response = connection.response();
            Log.d("LSF", String.valueOf(response.method()));

            //get is successfull because it will redirect. if it fails we get a modified post response.
            if (response.method() == org.jsoup.Connection.Method.GET) {
                //yay
                for (String key : response.cookies().keySet()) {
                    Log.d("LSF", String.format("%s : %s", key, response.cookie(key)));
                    if (key.equals("JSESSIONID")) {
                        return response.cookie(key);
                    }
                }
            }

            return null;

        } catch (Exception ex) {
            Log.e(TAG, "Login failed ", ex);
        }
        return "";
    }

    @Override
    protected void onPostExecute(String result) {
        super.onPostExecute(result);
        context.loginCallback(result);
    }

}