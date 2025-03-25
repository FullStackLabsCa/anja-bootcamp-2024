package io.reactivestax;

import java.util.Arrays;
import java.util.List;

public class VowelsJava8 {
    public static void main(String[] args) {
        String str = "Mansi Marshal";
        List<String> strList = Arrays.asList(str.split(""));
        List<String> list = List.of("a", "e", "i", "o", "u");

        int size = strList.stream().filter(list::contains).toList().size();
        System.out.println(size);
    }
}
