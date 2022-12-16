package hu.gdf.thesis;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
@ComponentScan({"hu.gdf.thesis"})
@Slf4j
public class AlertService {
	public static void main(String[] args) {
		if(args.length<1 ||
				!args[0].startsWith("--spring.config.location=")) {
			log.info("Please add the full path to your " +
					"Spring Boot configuration file as argument.");
			System.exit(1);
		}
		SpringApplication.run(AlertService.class, args);
	}
}