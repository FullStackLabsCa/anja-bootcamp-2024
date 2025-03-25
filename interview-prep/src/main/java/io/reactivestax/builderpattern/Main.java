package io.reactivestax.builderpattern;

public class Main {
    public static void main(String[] args) {
        Employee build = new Employee.EmployeeBuilder().name("name").address("address").age(10).build();
        System.out.println(build.toString());
    }
}
