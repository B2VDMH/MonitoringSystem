package hu.gdf.thesis;
import com.vaadin.flow.theme.Theme;
import com.vaadin.flow.theme.lumo.Lumo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@Slf4j
@SpringBootApplication
public class MonitoringApplication {
	public static void main(String[] args) {
		/*if (args.length<1) {
			log.error("Unable to start application due to missing argument of file path for .properties config file. ");
			log.info("Please add your .properties file as the following argument: --spring.config.location=/absolute/path/to/your.properties");
			System.exit(1);
		}*/
		SpringApplication.run(MonitoringApplication.class, args);
	}
}