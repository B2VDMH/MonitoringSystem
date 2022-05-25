package hu.gdf.thesis.alert;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableConfigurationProperties
@EnableScheduling
@ComponentScan({"hu.gdf.thesis"})
@Slf4j
public class AlertService {
	public static void main(String[] args) {
		/*if (args.length<1) {
			log.error("Unable to start application due to missing argument of file path for .properties config file. ");
			log.info("Please add your .properties file as the following argument: --spring.config.location=/absolute/path/to/your.properties");
			System.exit(1);
		}*/
		SpringApplication.run(AlertService.class, args);
	}
}