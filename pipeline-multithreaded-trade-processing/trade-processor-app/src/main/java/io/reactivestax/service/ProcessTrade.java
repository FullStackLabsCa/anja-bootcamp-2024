package io.reactivestax.service;

public interface ProcessTrade {
    void processTrade(String tradeId) throws InterruptedException;
}
