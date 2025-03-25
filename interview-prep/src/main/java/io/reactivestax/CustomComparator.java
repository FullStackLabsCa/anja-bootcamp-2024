package io.reactivestax;

import io.reactivestax.builderpattern.Employee;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class CustomComparator {
    public static void main(String[] args) {
//        10. Custom Comparator with thenComparing
//        Problem:
//        Sort a list of Person objects first by age and then by name using Java 8's Comparator.thenComparing().
        List<Employee> list = new ArrayList<>();
        list.add(new Employee.EmployeeBuilder().name("name").address("address").age(10).build());
        list.add(new Employee.EmployeeBuilder().name("fain").address("address").age(9).build());
        list.add(new Employee.EmployeeBuilder().name("nlfjaolame").address("address").age(340).build());
        list.add(new Employee.EmployeeBuilder().name("namoajnofine").address("address").age(1).build());

        list.sort(Comparator.comparing((Employee emp) -> emp.getAge()).thenComparing((Employee emp) -> emp.getName()));
    }
}
