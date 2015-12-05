package com.hstrobel.lsfplan;

import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.hstrobel.lsfplan.classes.Globals;
import com.hstrobel.lsfplan.classes.ICSLoader;
import com.hstrobel.lsfplan.classes.PlanGroup;
import com.hstrobel.lsfplan.frags.AbstractWebSelector;

import org.apache.commons.lang.exception.ExceptionUtils;
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

        mList = (ExpandableListView) findViewById(R.id.listView);
        mAdapter = new PlanListAdapter(this);
        mList.setAdapter(mAdapter);

        loadOverview();

        mList.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            private View lastHighlight = null;

            public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
                v.setBackgroundResource(R.color.orange);
                if (lastHighlight != null) lastHighlight.setBackgroundColor(Color.WHITE);
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
            loadOverview();
        }

        return super.onOptionsItemSelected(item);
    }

    private void loadExportUrl() {
        Log.d("LSF", "loadExportUrl");
        if (lastItem == null) return;

        spinner = (ProgressBar) findViewById(R.id.progressBarHtml);
        spinner.setVisibility(View.VISIBLE);
        mList.setEnabled(false);
        String planURL = lastItem.URL;
        exportLoader = new PlanExportLoader();
        exportLoader.execute(planURL);
    }

    private void exportCallback(String url) {
        if (url == null) {
            Toast.makeText(this, "Download failed! Check your Connection", Toast.LENGTH_LONG);
            return;
        }
        Log.d("LSF", "exportCallback");
        Globals.loader = new ICSLoader(this, new Handler());
        Globals.loader.execute(url);
    }

    private void loadOverview() {
        Log.d("LSF", "loadOverview");
        spinner = (ProgressBar) findViewById(R.id.progressBarHtml);
        spinner.setVisibility(View.VISIBLE);
        mList.setEnabled(false);

        String savedURL = Globals.mSettings.getString("URL", "missing");
        overviewLoader = new PlanOverviewLoader();
        overviewLoader.execute(savedURL);
    }

    private void overviewCallback(List<PlanGroup> results) {
        if (results == null) {
            Toast.makeText(this, "Download failed! Check your Connection", Toast.LENGTH_LONG);
            return;
        }
        Log.d("LSF", "overviewCallback");

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

    /*       <tr >
                <td class="mod_n_odd">
					<a class="regular" href="https://lsf.htwg-konstanz.de/qisserver/rds?state=verpublish&amp;publishContainer=stgContainer&amp;publishid=4511">Angewandte Informatik (Bachelor) </a>
				</td>
				<td class="mod_n_odd">
					<a class="normal" href="https://lsf.htwg-konstanz.de/qisserver/rds?state=wplan&amp;act=stg&amp;pool=stg&amp;show=plan&amp;P.vx=kurz&amp;r_zuordabstgv.semvonint=1&amp;r_zuordabstgv.sembisint=1&amp;missing=allTerms&amp;k_parallel.parallelid=&amp;k_abstgv.abstgvnr=4511&amp;r_zuordabstgv.phaseid=">1. Semester</a>
	*/

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
                    Log.d("LSF", courseURL.text());
                    group = new PlanGroup(courseURL.text());

                    //course semesters
                    for (Element ele : columns.get(1).children()) {
                        if (ele.tagName() != "a") continue;
                        Log.d("LSF", ele.text());
                        Log.d("LSF", ele.attr("href"));
                        item = new PlanGroup.PlanItem(ele.text(), ele.attr("href"));
                        group.items.add(item);
                    }
                    //course everthing
                    Element allURL = columns.get(2).child(0); //row 0 --> a class --> inner text
                    Log.d("LSF", allURL.text());
                    Log.d("LSF", allURL.attr("href"));
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
                Document doc = Jsoup.parse(new URL(params[0]), 5000);

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
