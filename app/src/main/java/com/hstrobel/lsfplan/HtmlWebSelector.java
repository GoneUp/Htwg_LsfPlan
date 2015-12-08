package com.hstrobel.lsfplan;

import android.app.Dialog;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.util.Log;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.hstrobel.lsfplan.classes.Globals;
import com.hstrobel.lsfplan.classes.ICSLoader;
import com.hstrobel.lsfplan.classes.LoginProcess;
import com.hstrobel.lsfplan.classes.PlanGroup;
import com.hstrobel.lsfplan.classes.PlanListAdapter;
import com.hstrobel.lsfplan.frags.AbstractWebSelector;

import org.apache.commons.lang.exception.ExceptionUtils;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.net.URL;
import java.util.LinkedList;
import java.util.List;

public class HtmlWebSelector extends AbstractWebSelector {

    PlanExportLoader exportLoader = null;
    PlanOverviewLoader overviewLoader = null;
    LoginProcess loginProcess = null;
    private HtmlWebSelector local;
    private ExpandableListView mList;
    private PlanListAdapter mAdapter;
    private ProgressBar spinner;
    private PlanGroup.PlanItem lastItem = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_html_web_selector);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loadExportUrl();
            }
        });
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        local = this;
        mList = (ExpandableListView) findViewById(R.id.listView);
        mAdapter = new PlanListAdapter(this);
        mList.setAdapter(mAdapter);

        spinner = (ProgressBar) findViewById(R.id.progressBarHtml);

        loadOverview();

        mList.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            private View lastHighlight = null;

            public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
                TypedValue typedValue = new TypedValue();
                getTheme().resolveAttribute(R.attr.background, typedValue, true);

                v.setBackgroundResource(R.color.orange);
                if (lastHighlight != null) lastHighlight.setBackgroundColor(typedValue.data);
                lastHighlight = v;

                int index = parent.getFlatListPosition(ExpandableListView
                        .getPackedPositionForChild(groupPosition, childPosition));
                lastItem = (PlanGroup.PlanItem) parent.getItemAtPosition(index);
                return true;
            }

        });
    }

    @Override
    protected void onStop() {
        super.onStop();

        if (exportLoader != null) exportLoader.cancel(true);
        if (overviewLoader != null) overviewLoader.cancel(true);
        if (loginProcess != null) loginProcess.cancel(true);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_html_web, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_refresh) {
            Globals.cachedPlans = null;
            loadOverview();
        }

        return super.onOptionsItemSelected(item);
    }


    private void loadExportUrl() {
        Log.d("LSF", "loadExportUrl");
        if (lastItem == null) return;
        String planURL = "";

        if (lastItem.URL.equals("#LOGIN#")) {
            showLoginForm();
        } else {
            spinner.setVisibility(View.VISIBLE);
            mList.setEnabled(false);
            planURL = lastItem.URL;

            exportLoader = new PlanExportLoader();
            exportLoader.execute(planURL);
        }
    }

    public void loginCallback(String loginCookie) {
        if (loginCookie == null) {
            Toast.makeText(this, "Login failed. Check your username/password.", Toast.LENGTH_LONG).show();
            spinner.setVisibility(View.GONE);
            mList.setEnabled(true);
            return;
        }

        exportLoader = new PlanExportLoader();
        exportLoader.execute(getString(R.string.misc_personalPlanURL), loginCookie);
    }

    private void exportCallback(String url) {
        if (url == null) {
            Toast.makeText(this, "Download failed! Check your Connection", Toast.LENGTH_LONG).show();
            return;
        }
        Log.d("LSF", "exportCallback");
        Globals.loader = new ICSLoader(this, new Handler());
        Globals.loader.execute(url);
    }

    private void loadOverview() {
        Log.d("LSF", "loadOverview");

        if (Globals.cachedPlans != null) {
            // got a saved version
            overviewCallback(Globals.cachedPlans);
        } else {
            spinner.setVisibility(View.VISIBLE);
            mList.setEnabled(false);

            String savedURL = Globals.mSettings.getString("URL", "missing");
            overviewLoader = new PlanOverviewLoader();
            overviewLoader.execute(savedURL);
        }
    }

    private void overviewCallback(List<PlanGroup> results) {
        if (results == null) {
            Toast.makeText(this, "Download failed! Check your Connection", Toast.LENGTH_LONG).show();
            return;
        }
        Log.d("LSF", "overviewCallback");
        Globals.cachedPlans = results;

        mAdapter.clear();
        mAdapter.addPlanGroup(getString(R.string.html_login_head));
        mAdapter.addPlanItem(getString(R.string.html_login_head), getString(R.string.html_login_sub), "#LOGIN#");

        for (PlanGroup group : results) {
            mAdapter.addPlanGroup(group.name);
            for (PlanGroup.PlanItem item : group.items) {
                mAdapter.addPlanItem(group.name, item.name, item.URL);
            }
        }
        mList.invalidateViews();
        mList.setEnabled(true);
        spinner.setVisibility(View.GONE);
    }


    private void showLoginForm() {
        // Create Object of Dialog class
        final Dialog login = new Dialog(this);
        // Set GUI of login screen
        login.setContentView(R.layout.login_dialog);
        login.setTitle("Enter HTWG Login Data");

        // Init button of login GUI
        final CheckBox box = (CheckBox) login.findViewById(R.id.checkSave);
        final Button btnLogin = (Button) login.findViewById(R.id.btnLogin);
        final Button btnCancel = (Button) login.findViewById(R.id.btnCancel);
        final EditText txtUsername = (EditText) login.findViewById(R.id.txtUsername);
        final EditText txtPassword = (EditText) login.findViewById(R.id.txtPassword);

        boolean autoSave = Globals.mSettings.getBoolean("loginAutoSave", true);
        box.setChecked(autoSave);
        if (autoSave) {
            String user = Globals.mSettings.getString("loginUser", "");
            String pw = Globals.mSettings.getString("loginPassword", "");
            if (!user.equals("")) {
                user = new String(Base64.decode(user, Base64.DEFAULT));
                pw = new String(Base64.decode(pw, Base64.DEFAULT));
            }

            txtUsername.setText(user);
            txtPassword.setText(pw);
        }


        // Attached listener for login GUI button
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String user = txtUsername.getText().toString().trim();
                String pw = txtPassword.getText().toString().trim();

                if ((user.length() > 0) && (pw.length() > 0)) {
                    login.dismiss();
                    //login
                    spinner.setVisibility(View.VISIBLE);
                    mList.setEnabled(false);

                    loginProcess = new LoginProcess(local, new Handler());
                    loginProcess.execute(user, pw);
                }

                SharedPreferences.Editor editor = Globals.mSettings.edit();
                editor.putBoolean("loginAutoSave", box.isChecked());
                if (box.isChecked()) {
                    editor.putString("loginUser", Base64.encodeToString(user.getBytes(), Base64.DEFAULT));
                    editor.putString("loginPassword", Base64.encodeToString(pw.getBytes(), Base64.DEFAULT));
                }
                editor.apply();
            }
        });
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                login.dismiss();
            }
        });

        // Make dialog box visible.
        login.show();
    }

    public class PlanOverviewLoader extends AsyncTask<String, String, List<PlanGroup>> {
        public PlanOverviewLoader() {
        }

        @Override
        protected List<PlanGroup> doInBackground(String... params) {
            // Making HTTP request
            List<PlanGroup> list = new LinkedList<>();
            PlanGroup group;
            PlanGroup.PlanItem item;

            try {

                Document doc = Jsoup.parse(new URL(params[0]), 5000);

                Elements tableRows = doc.select("tr");
                for (Element row : tableRows) {
                    if (row.children().size() != 3) continue; //skip head row
                    Elements columns = row.children();

                    //course name
                    Element courseURL = columns.get(0).child(0); //row 0 --> a class --> inner text
                    if (Globals.DEBUG) Log.d("LSF", courseURL.text());
                    group = new PlanGroup(courseURL.text());

                    //course semesters
                    for (Element ele : columns.get(1).children()) {
                        if (ele.tagName() != "a") continue;
                        if (Globals.DEBUG) Log.d("LSF", String.format("%s : %s", ele.text(), ele.attr("href")));
                        item = new PlanGroup.PlanItem(ele.text(), ele.attr("href"));
                        group.items.add(item);
                    }
                    //course everthing
                    Element allURL = columns.get(2).child(0); //row 0 --> a class --> inner text
                    if (Globals.DEBUG) Log.d("LSF", String.format("%s : %s", allURL.text(), allURL.attr("href")));
                    item = new PlanGroup.PlanItem(allURL.text(), allURL.attr("href"));
                    group.items.add(item);

                    list.add(group);
                }

            } catch (Exception ex) {
                Log.e("LSF", "FAIL DL:\n " + ExceptionUtils.getCause(ex));
                Log.e("LSF", "FAIL DL ST:\n " + ExceptionUtils.getFullStackTrace(ex));
                list = null;
            }

            return list;
        }

        @Override
        protected void onPostExecute(List<PlanGroup> result) {
            super.onPostExecute(result);
            overviewCallback(result);
        }
    }

    public class PlanExportLoader extends AsyncTask<String, String, String> {
        public PlanExportLoader() {
        }

        @Override
        protected String doInBackground(String... params) {
            // Making HTTP request
            String url = null;
            try {
                Connection con = Jsoup.connect(params[0]);
                if (params.length > 1) con.cookie("JSESSIONID", params[1]);
                con.userAgent("LSF APP");
                Document doc = con.get();


                Elements images = doc.select("img");
                for (Element imgs : images) {
                    if (imgs.hasAttr("src")) {
                        if (imgs.attr("src").equals("/QIS/images//calendar_go.gif")) {
                            //found export ;)
                            Log.d("LSF", imgs.parent().attr("href"));
                            url = imgs.parent().attr("href");
                            break;
                        }
                    }
                }

            } catch (Exception ex) {
                System.out.println("FAIL DL:\n " + ExceptionUtils.getCause(ex));
                System.out.println("FAIL DL ST:\n " + ExceptionUtils.getFullStackTrace(ex));
                url = null;
            }
            return url;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            exportCallback(result);
        }
    }


}
