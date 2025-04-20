package io.reactivestax;

import java.util.*;
import java.util.stream.Collectors;

public class RemoveDuplicates {
    public static void main(String[] args) {
        String str = "The the quick brown fox jumped over the lazy dog. It it didn't see that that the dog was actually awake. The fox fox looked back back, surprised surprised by the sudden movement. It was was a strange strange morning in the the forest.";

        System.out.println(removeDupsWithJava8(str));
        System.out.println(removeDups(str));
    }

    private static String removeDupsWithJava8(String str){
        return Arrays.stream(str.toLowerCase().split(" ")).distinct().collect(Collectors.joining(" "));
    }

    private static String removeDups(String str){
        String[] stArray = str.toLowerCase().split(" ");
        List<String> stringList = new ArrayList<>();

        StringBuilder stringBuilder = new StringBuilder();
        for (String st: stArray) {
            if(!stringList.contains(st.replaceAll("[^a-z]", ""))) {
                stringList.add(st);
                stringBuilder.append(st).append(" ");
            }
        }

        return stringBuilder.toString().trim();
    }
}
