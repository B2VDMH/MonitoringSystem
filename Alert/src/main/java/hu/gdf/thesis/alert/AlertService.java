package hu.gdf.thesis.alert;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableConfigurationProperties
@EnableScheduling
@ComponentScan({"hu.gdf.thesis"})
public class AlertService {
	public static void main(String[] args) {
		//if (args.length<1) {
			//hu.gdf.thesis.backend.Log.info("Unable to start application due to missing argument file path for config");
			//System.exit(1);
		//}
		//hu.gdf.thesis.backend.Log.info(System.getProperty("log4j.configuration"));
		//FileHandler.FILE_PATH = args[0];
		SpringApplication.run(AlertService.class, args);
	}
}