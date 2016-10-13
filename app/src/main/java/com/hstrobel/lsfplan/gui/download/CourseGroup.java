package com.hstrobel.lsfplan.gui.download;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by Henry on 04.12.2015.
 */
public class CourseGroup {
    public String name;
    public List<Course> items = new LinkedList<>();


    public CourseGroup(String name) {
        this.name = name;
    }

    public static class Course {
        public String URL;
        public String name;

        public Course(String name, String url) {
            this.name = name;
            this.URL = url;
        }
    }
}