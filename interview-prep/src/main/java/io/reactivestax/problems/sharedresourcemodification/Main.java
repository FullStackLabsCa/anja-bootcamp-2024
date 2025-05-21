package io.reactivestax.problems.sharedresourcemodification;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.stream.IntStream;

public class Main {
    public static void main(String[] args) throws InterruptedException {
        BankAccount bankAccount = new BankAccount();

        ExecutorService depositService = Executors.newFixedThreadPool(5);
        ExecutorService withdrawService = Executors.newFixedThreadPool(5);

        IntStream.range(0, 5).forEach(i -> depositService.submit(new Deposit(bankAccount)));
        IntStream.range(0, 5).forEach(i -> withdrawService.submit(new Withdraw(bankAccount)));

        depositService.shutdown();
        withdrawService.shutdown();

        withdrawService.awaitTermination(30, TimeUnit.SECONDS);
        depositService.awaitTermination(30, TimeUnit.SECONDS);

        System.out.println(bankAccount.getBalance());
    }
}
