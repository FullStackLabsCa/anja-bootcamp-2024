package io.reactivestax.service;

import io.reactivestax.util.factory.BeanFactory;
import io.reactivestax.repository.JournalEntryRepository;
import io.reactivestax.repository.LookupSecuritiesRepository;
import io.reactivestax.repository.PositionsRepository;
import io.reactivestax.repository.TradePayloadRepository;
import io.reactivestax.util.database.TransactionUtil;
import io.reactivestax.util.messaging.MessageReceiver;
import io.reactivestax.util.messaging.TransactionRetryer;
import jakarta.persistence.OptimisticLockException;
import org.hibernate.HibernateException;

import java.io.IOException;
import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;
import java.util.logging.Logger;

public class FileTradeProcessor implements Callable<Void>, TradeProcessor {
    Logger logger = Logger.getLogger(FileTradeProcessor.class.getName());
    String queueName;
    private final TradePayloadRepository tradePayloadRepository;
    private final TransactionUtil transactionUtil;
    private final LookupSecuritiesRepository lookupSecuritiesRepository;
    private final JournalEntryRepository journalEntryRepository;
    private final PositionsRepository positionsRepository;
    private final TransactionRetryer transactionRetryer;
    private final CountDownLatch latch = new CountDownLatch(1);

    int count = 0;

    public FileTradeProcessor(String queueName) {
        this.queueName = queueName;
        this.transactionUtil = BeanFactory.getTransactionUtil();
        this.tradePayloadRepository = BeanFactory.getTradePayloadRepository();
        this.lookupSecuritiesRepository = BeanFactory.getLookupSecuritiesRepository();
        this.journalEntryRepository = BeanFactory.getJournalEntryRepository();
        this.positionsRepository = BeanFactory.getPositionsRepository();
        this.transactionRetryer = BeanFactory.getTransactionRetryer();
    }

    @Override
    public Void call() throws InterruptedException {
        try {
            MessageReceiver messageReceiver = BeanFactory.getMessageReceiver();
            while (count == 0) {
                String tradeId = messageReceiver.receiveMessage(queueName);
                if (!tradeId.isEmpty()) {
                    processTrade(tradeId);
                } else {
                    logger.info("No trade ID received. Waiting for messages...");
                }
            }
        } catch (IOException | InterruptedException e) {
            logger.warning("Exception detected in Trade Processor.");
            Thread.currentThread().interrupt();
        }
        latch.await();
        return null;
    }


}
