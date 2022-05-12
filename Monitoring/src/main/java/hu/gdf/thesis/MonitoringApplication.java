package hu.gdf.thesis;
import com.vaadin.flow.theme.Theme;
import com.vaadin.flow.theme.lumo.Lumo;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class MonitoringApplication {
	public static void main(String[] args) {
		//if (args.length<1) {
			//Log.info("Unable to start application due to missing argument file path for config");
			//System.exit(1);
		//}
		//Log.info(System.getProperty("log4j.configuration"));
		//FileHandler.FILE_PATH = args[0];
		SpringApplication.run(MonitoringApplication.class, args);
	}
}