package io.reactivestax.service;

import java.io.IOException;

public interface ProcessTrade {
    void processTrade(String tradeId) throws InterruptedException, IOException;
}
