package io.reactivestax.active.life.canada;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class ActiveLifeCanadaAppApplication {

//    static {
//        System.setProperty("javax.net.ssl.trustStore", "/tmp/truststore.p12");
//        System.setProperty("javax.net.ssl.trustStorePassword", "changeit");
//    }

    public static void main(String[] args) {
        SpringApplication.run(ActiveLifeCanadaAppApplication.class, args);
    }
}
