package com.hstrobel.lsfplan.classes;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by Henry on 04.12.2015.
 */
public class PlanGroup {
    public String name;
    public List<PlanItem> items = new LinkedList<>();

    public PlanGroup() {
    }

    public PlanGroup(String name) {
        this.name = name;
    }

    public static class PlanItem {
        public String URL;
        public String name;

        public PlanItem() {
        }

        public PlanItem(String name, String url) {
            this.name = name;
            this.URL = url;
        }
    }
}