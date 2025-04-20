package io.reactivestax;

import java.util.Arrays;
import java.util.List;

public class VowelsJava8 {
    public static void main(String[] args) {
        String str = "mansi marshal fds fad etr";
        List<String> strList = Arrays.asList(str.split(" "));
        System.out.println(strList.stream());
        List<String> list = List.of("a", "e", "i", "o", "u");

        int size = strList.stream().filter(list::contains).toList().size();
        System.out.println(size);
    }
}
