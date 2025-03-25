package io.reactivestax;

import io.reactivestax.builderpattern.Employee;

import java.util.List;
import java.util.stream.Collectors;

public class GroupingData {

    public static void main(String[] args) {
//        5. Collectors - Grouping Data
//        Problem:
//        Given a list of Employee objects (id, name, department, salary), write a Java 8
//        program to group employees by department and count the number of employees in each department.
        Employee build = new Employee.EmployeeBuilder().name("hgfyu").age(45).address("fgjgb").build();
        List<Employee> list = List.of(build);

        list.stream().collect(Collectors.groupingBy(Employee::getAddress, Collectors.counting()));
    }
}
