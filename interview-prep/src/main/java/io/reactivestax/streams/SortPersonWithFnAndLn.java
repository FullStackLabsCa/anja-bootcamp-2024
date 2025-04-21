package io.reactivestax.streams;

import java.util.Comparator;
import java.util.List;

public class SortPersonWithFnAndLn {
    static class Person {
        private String firstName;
        private String lastName;

        public Person(String firstName, String lastName) {
            this.firstName = firstName;
            this.lastName = lastName;
        }

        public String getFirstName() {
            return firstName;
        }

        public String getLastName() {
            return lastName;
        }

        @Override
        public String toString() {
            return "Person{" +
                    "firstName='" + firstName + '\'' +
                    ", lastName='" + lastName + '\'' +
                    '}';
        }
    }

    public static void main(String[] args) {
        List<Person> people = List.of(
                new Person("anant", "jain"),
                new Person("akshita", "joshi"),
                new Person("ankit", "joshi")
        );

        System.out.println(people.stream().sorted(Comparator.comparing(Person::getFirstName).thenComparing(Person::getLastName)).toList());
    }
}
