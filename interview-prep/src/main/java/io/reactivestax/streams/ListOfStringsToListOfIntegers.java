package io.reactivestax.streams;

import java.util.List;

public class ListOfStringsToListOfIntegers {

    public static void main(String[] args) {
        List<String> stringList = List.of("abc", "123", "456", "xyz");

        System.out.println(stringList.stream().filter(st -> st.matches("[0-9]+")).map(st -> Integer.parseInt(st)).toList());
    }
}
