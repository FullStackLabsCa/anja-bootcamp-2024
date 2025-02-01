package io.reactivestax.active.life.canada;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class ActiveLifeCanadaAppApplication {

	public static void main(String[] args) {
		SpringApplication.run(ActiveLifeCanadaAppApplication.class, args);
	}

}
