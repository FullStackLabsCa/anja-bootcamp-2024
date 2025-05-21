package io.reactivestax.streams;

import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class MiddleCharacterOfString {
    public static void main(String[] args) {
        String s = "Educaation";
//        String s = "Travel";

        System.out.println(IntStream.range(0, s.length()).filter(index -> s.length() % 2 == 0 ?
                s.length() / 2 - 1 == index || s.length() / 2 == index :
                s.length() / 2 == index).mapToObj(index -> s.charAt(index)).collect(StringBuilder::new,
                (sb, ch) -> sb.append(ch),
                (sb1, sb2)-> sb1.append(sb2)).toString());
    }
}
