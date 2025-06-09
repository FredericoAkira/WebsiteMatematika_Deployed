package dev.fredericoAkira.FtoA;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.scheduling.annotation.EnableScheduling;

import dev.fredericoAkira.FtoA.Util.CloudinaryProperties;

@EnableConfigurationProperties(CloudinaryProperties.class)
@SpringBootApplication
@EnableScheduling
public class FtoAApplication {

	public static void main(String[] args) {
		SpringApplication.run(FtoAApplication.class, args);
	}

}
