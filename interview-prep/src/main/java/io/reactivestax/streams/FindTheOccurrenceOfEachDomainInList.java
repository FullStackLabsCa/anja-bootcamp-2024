package io.reactivestax.streams;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

public class FindTheOccurrenceOfEachDomainInList {
    static class Employee {
        private String name;
        private String email;

        public Employee(String name, String email) {
            this.name = name;
            this.email = email;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }

        @Override
        public String toString() {
            return "Employee{" +
                    "name='" + name + '\'' +
                    ", email='" + email + '\'' +
                    '}';
        }
    }

    public static void main(String[] args) {
        List<Employee> employees = List.of(new Employee("Anant", "anant@gmail.com"), new Employee("Ankit", "email@gmail.com"), new Employee(
                "Hagu", "email@yahoo.com"));

        System.out.println(employees.stream().map(emp -> emp.email.split("@")[1]).collect(Collectors.groupingBy(Function.identity(),
                Collectors.counting())));
    }
}
