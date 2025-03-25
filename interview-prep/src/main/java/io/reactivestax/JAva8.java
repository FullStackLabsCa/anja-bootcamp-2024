package io.reactivestax;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

public class JAva8 {

    public static void main(String[] args) {

        Map<Integer, Student> studentMap = new HashMap<>();

        Stream<Student> streamStudent = studentMap.values().stream();
        Stream<Course> courseStream = streamStudent.flatMap(student -> student.getCourses().stream());
        courseStream.forEach(course-> System.out.println(course.getName()));

    }
}
