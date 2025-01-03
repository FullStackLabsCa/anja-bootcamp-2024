package io.reactivestax;

import io.reactivestax.configuration.AppConfig;
import io.reactivestax.service.CacheServiceImpl;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import java.io.File;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class Main {
    static {
        File projectHome = new File(System.getProperty("user.dir"));
        System.setProperty("log4j.configuration", "file:" + projectHome.getAbsolutePath() + "/src/main/resources/default.log4j.properties");
    }

    private static final ExecutorService executorService = Executors.newFixedThreadPool(3);
    private static final Log log = LogFactory.getLog(Main.class);

    public static void main(String[] args) throws Exception {

        log.debug("Application started.");
        ApplicationContext context = new AnnotationConfigApplicationContext(AppConfig.class);
        CacheServiceImpl cacheService = context.getBean(CacheServiceImpl.class);
        log.debug("running thread 1");
        executorService.submit(() -> log.debug(cacheService.processTrade("TN1", "AAPL")));
        log.debug("running thread 2");
        executorService.submit(() -> log.debug(cacheService.processTrade("TN1", "AAPL")));
        log.debug("running thread 3");
        executorService.submit(() -> log.debug(cacheService.processTrade("TN2", "AAPL")));
        executorService.shutdown();
        boolean terminated = executorService.awaitTermination(100, TimeUnit.MILLISECONDS);
        if (terminated) {
            log.debug(cacheService.getObject("TN1", "AAPL"));
            cacheService.processTrade("TN1", "AAPL");
            cacheService.processTrade("TN1", "AAPL");
            cacheService.processTrade("TN1", "AAPL");
//            Thread.sleep(60000);
            cacheService.processTrade("TN1", "AAPL");
        }
    }
}
