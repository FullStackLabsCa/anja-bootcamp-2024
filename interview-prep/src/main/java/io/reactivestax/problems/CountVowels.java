package io.reactivestax.problems;

import java.util.Set;

public class CountVowels {
    public static void main(String[] args) {
        String str = "Anant Jain";
        Set<Character> vowels = Set.of('a', 'e', 'i', 'o', 'u');

        System.out.println(str.toLowerCase().chars().filter(character -> vowels.contains((char) character)).count());
    }
}
