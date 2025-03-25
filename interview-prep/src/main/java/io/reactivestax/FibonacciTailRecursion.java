package io.reactivestax;

import java.math.BigInteger;

public class FibonacciTailRecursion {
    public static void main(String[] args) {
        int n = 9;
        System.out.println("Fibonacci Number at position " + n + " is: " + fib(n, BigInteger.ZERO, BigInteger.ONE));
    }

    public static BigInteger fib(int n, BigInteger a, BigInteger b) {
        if (n == 0) return a;
        if (n == 1) return b;
        System.out.println(a);
        return fib(n - 1, b, a.add(b)); // Tail recursion
    }
}
