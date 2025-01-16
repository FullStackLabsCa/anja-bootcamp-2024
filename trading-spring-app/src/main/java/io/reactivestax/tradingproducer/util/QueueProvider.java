package io.reactivestax.tradingproducer.util;

import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.concurrent.LinkedBlockingQueue;

@Component
@Setter
@Getter
public class QueueProvider {

    private final ApplicationPropertiesUtils applicationPropertiesUtils;
    private LinkedBlockingQueue<String> chunkQueue;

    @Autowired
    public QueueProvider(ApplicationPropertiesUtils applicationPropertiesUtils) {
        this.applicationPropertiesUtils = applicationPropertiesUtils;
        chunkQueue = new LinkedBlockingQueue<>(applicationPropertiesUtils.getNumberOfChunks());
    }
}
