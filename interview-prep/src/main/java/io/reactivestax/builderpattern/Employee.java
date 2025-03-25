package io.reactivestax.builderpattern;

public class Employee {
    private String name;
    private String address;
    private Integer age;

    private Employee(EmployeeBuilder builder) {
        this.name = builder.name;
        this.address = builder.address;
        this.age = builder.age;
    }

    public String getName() {
        return name;
    }

    public String getAddress() {
        return address;
    }

    public Integer getAge() {
        return age;
    }

    public static class EmployeeBuilder {
        private String name;
        private String address;
        private Integer age;

        public EmployeeBuilder name(String name) {
            this.name = name;
            return this;
        }

        public EmployeeBuilder address(String address) {
            this.address = address;
            return this;
        }

        public EmployeeBuilder age(int age) {
            this.age = age;
            return this;
        }

        public Employee build() {
            return new Employee(this);
        }
    }

    @Override
    public String toString() {
        return "Employee{" +
                "name='" + name + '\'' +
                ", address='" + address + '\'' +
                ", age=" + age +
                '}';
    }
}
