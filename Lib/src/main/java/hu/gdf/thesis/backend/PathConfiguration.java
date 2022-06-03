package hu.gdf.thesis.backend;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@ConfigurationProperties(prefix = "config.directory")
@Configuration("path")
@Data
public class PathConfiguration {
    private String path;

}
