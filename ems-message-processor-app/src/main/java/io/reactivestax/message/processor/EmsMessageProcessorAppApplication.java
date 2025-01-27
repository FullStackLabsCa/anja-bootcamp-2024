package io.reactivestax.message.processor;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.retry.annotation.EnableRetry;

@EnableRetry
@SpringBootApplication
public class EmsMessageProcessorAppApplication {

	public static void main(String[] args) {
		SpringApplication.run(EmsMessageProcessorAppApplication.class, args);
	}

}
