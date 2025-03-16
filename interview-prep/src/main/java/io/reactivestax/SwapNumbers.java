package io.reactivestax;

/**
 * Hello world!
 */
public class SwapNumbers {
    public static void main(String[] args) {
        swapNumbers(10, 14);
    }

    public static void swapNumbers(int a, int b) {
        a = a + b;
        b = a - b;
        a = a - b;
        System.out.println(a + " " + b);
    }
}
