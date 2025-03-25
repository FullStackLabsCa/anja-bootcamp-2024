package io.reactivestax;

import java.util.List;

public class ConsonantsJava8 {
    public static void main(String[] args) {
        List<Character> vowels = List.of('a', 'e', 'i', 'o', 'u');
        String str = "Mansi Ma454#$$$rshal";
        long count = str.toLowerCase().chars().filter(ch -> Character.isLetter(ch) && !vowels.contains((char) ch)).count();
        System.out.println(count);
    }
}
