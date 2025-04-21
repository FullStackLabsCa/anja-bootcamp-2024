package io.reactivestax.streams;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class PersonObjectToOneStringOfNames {
    static class Employee{
        private String name;
        private String email;
        private int age;
        private String gender;

        public Employee(String name, String email, int age, String gender) {
            this.name = name;
            this.email = email;
            this.age = age;
            this.gender = gender;
        }

        public String getName() {
            return name;
        }

        public String getEmail() {
            return email;
        }

        public int getAge() {
            return age;
        }

        public String getGender() {
            return gender;
        }

        @Override
        public String toString() {
            return "Employee{" +
                    "name='" + name + '\'' +
                    ", email='" + email + '\'' +
                    ", age=" + age +
                    ", gender='" + gender + '\'' +
                    '}';
        }
    }
    public static void main(String[] args) {
        List<Employee> employees = List.of(
                new Employee("Anant", "email@gmail.com", 10, "M"),
                new Employee("Ankit", "email@gmail.com", 20, "M"),
                new Employee("Hagu", "email@yahoo.com", 30, "F"),
                new Employee("Hagu", "email@yahoo.com", 40, "F")
        );

        System.out.println(employees.stream().map(Employee::getName).collect(Collectors.joining(" | ")));

        System.out.println(employees.stream().map(Employee::getName).collect(Collectors.groupingBy(name -> name.charAt(0), Collectors.toList())));

        System.out.println(employees.stream().collect(Collectors.groupingBy(Employee::getEmail, Collectors.toList())));

        System.out.println(employees.stream().collect(Collectors.groupingBy(employee -> employee.gender, Collectors.averagingInt(Employee::getAge))));

        System.out.println(employees.stream().map(Employee::getAge).min(Comparator.comparing(num -> num)));
    }
}
