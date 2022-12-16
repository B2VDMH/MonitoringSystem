package hu.gdf.thesis;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@Slf4j
@SpringBootApplication
public class MonitoringApplication {
	public static void main(String[] args) {
		if(args.length<1 ||
				!args[0].startsWith("--spring.config.location=")) {
			log.info("Please add the full path to your " +
					"Spring Boot configuration file as argument.");
			System.exit(1);
		}
		SpringApplication.run(MonitoringApplication.class, args);
	}
}