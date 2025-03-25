package io.reactivestax;

import java.math.BigInteger;

public class FibonacciRecursion {
    static BigInteger a = BigInteger.valueOf(0);
    static BigInteger b = BigInteger.valueOf(1);

    public static void main(String[] args) {
        printFibonacci(9);
    }

    public static void printFibonacci(int n) {
        System.out.println(a);
        BigInteger temp = a;
        a = b;
        b = temp.add(b);
        if (n > 1) {
            printFibonacci(n-1);
        }
    }
}
