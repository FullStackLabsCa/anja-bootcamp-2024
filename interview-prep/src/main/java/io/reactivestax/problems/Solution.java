package io.reactivestax.problems;

import java.util.*;

class Solution {

    public static void main(String[] args) {
//        String encode = encode(List.of(""));
//        System.out.println(encode);
//        List<String> decode = decode(encode);
//        System.out.println(decode.size());
//        System.out.println(decode);

//        System.out.println(isPalindrome(""0P""));
//        System.out.println(isValid("]"));
//        System.out.println(wordBreak("aaaaaaa", List.of("aaaa", "aaa")));
        System.out.println(maxProduct(new int[]{-3,-1,-1}));
    }

    public static int maxProduct(int[] nums) {
        int[] product = new int[nums.length];
        product[nums.length-1] = nums[nums.length-1];
        for(int i=nums.length -2; i>=0; i--) {
            product[i] = Math.max(nums[i], nums[i] * product[i+1]);
        }

        return Arrays.stream(product).sorted().toArray()[product.length-1];
    }

    public static boolean wordBreak(String s, List<String> wordDict) {
        List<String> words = wordDict.stream().sorted(Comparator.comparing(String::length).reversed()).toList();
        for (String word : words) {
            s = s.replaceAll(word, "");
        }

        return s.isEmpty();

//        int left=s.length()-1;
//        int right=s.length();
//        while(left >= 0){
//            if(wordDict.contains(s.substring(left, right))) {
//                right = left;
//            }
//            left--;
//        }
//
//        if(right == left+1){
//            return true;
//        }
//
//        return false;
    }

    public static boolean isValid(String s) {
        Stack<String> stack = new Stack<>();

        List<String> list = Arrays.stream(s.split("")).toList();

        for (String str : list) {
            switch (str) {
                case "(", "{", "[":
                    stack.push(str);
                    break;
                case ")":
                    if (!stack.isEmpty() && Objects.equals(stack.peek(), "(")) stack.pop();
                    else stack.push(str);
                    break;
                case "}":
                    if (!stack.isEmpty() && Objects.equals(stack.peek(), "{")) stack.pop();
                    else stack.push(str);
                    break;
                case "]":
                    if (!stack.isEmpty() && Objects.equals(stack.peek(), "[")) stack.pop();
                    else stack.push(str);
                    break;
                default:
            }
        }

        return stack.isEmpty();

    }

    public static int removeDuplicates(int[] nums) {
        return 0;
    }

    public static String encode(List<String> strs) {

        String string = String.valueOf(strs);
        System.out.println(string);
//        (List<String>) string;
        if (strs.isEmpty()) return "";
        StringBuilder sb = new StringBuilder();

        strs.forEach(st -> {
            sb.append(st);
            sb.append("joinerjoining");
        });

        return sb.toString();
    }

    public static List<String> decode(String str) {
//        if(str.equals("joining")) return List.of("");
        if (str.isEmpty()) return List.of();
        System.out.println(str.split("joiner").length);
        return Arrays.asList(str.split("joiner"));
    }

    public static boolean isPalindrome(String s) {
        String string = s.replaceAll("[^a-zA-Z]+", "");

        StringBuilder stringBuilder = new StringBuilder(string);
        String string1 = stringBuilder.reverse().toString();

        return string1.equals(string);
    }


}