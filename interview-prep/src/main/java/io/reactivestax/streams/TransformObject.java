package io.reactivestax.streams;

import java.util.List;

public class TransformObject {
    static class Employee {
        private int id;
        private String name;

        public Employee(int id, String name) {
            this.id = id;
            this.name = name;
        }

        public int getId() {
            return id;
        }

        public String getName() {
            return name;
        }

        @Override
        public String toString() {
            return "Employee{" +
                    "id=" + id +
                    ", name='" + name + '\'' +
                    '}';
        }
    }

    static class EmployeeDTO {
        private int id;
        private String name;

        public EmployeeDTO(int id, String name) {
            this.id = id;
            this.name = name;
        }

        public int getId() {
            return id;
        }

        public String getName() {
            return name;
        }

        @Override
        public String toString() {
            return "EmployeeDTO{" +
                    "id=" + id +
                    ", name='" + name + '\'' +
                    '}';
        }
    }

    public static void main(String[] args) {
        List<Employee> employees = List.of(
                new Employee(1, "anant"),
                new Employee(2, "ankit")
        );

        System.out.println(employees.stream().map(employee -> new EmployeeDTO(employee.getId(), employee.getName())).toList());
    }
}
