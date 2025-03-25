package io.reactivestax;

import java.util.ArrayList;
import java.util.List;

public class Student {
    List<Course> courses = new ArrayList<>();

    public List<Course> getCourses() {
        return courses;
    }
}
