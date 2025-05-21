package io.reactivestax.problems.sharedresourcemodification;

import java.util.concurrent.atomic.AtomicInteger;

public class BankAccount {
    private final AtomicInteger balance = new AtomicInteger(1000);

    void deposit(int amount) {
        balance.getAndUpdate(bal -> bal + amount);
    }

    void withdraw(int amount) {
        balance.getAndUpdate(bal -> bal -amount);
    }

    int getBalance(){
        return balance.get();
    }
}
