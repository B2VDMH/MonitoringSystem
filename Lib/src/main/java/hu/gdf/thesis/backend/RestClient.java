package hu.gdf.thesis.backend;

import hu.gdf.thesis.model.config.Entry;
import hu.gdf.thesis.model.config.Server;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
@Slf4j
public class RestClient {

    private static final String HTTP_TEXT = "http://";

    //Build full URL and perform HTTP GET method
    public String restCall(Server server, Entry entry) {

        //Build URL
        StringBuilder stringBuilder = new StringBuilder(HTTP_TEXT);
        stringBuilder.append(server.getHost());
        stringBuilder.append(":");
        stringBuilder.append(server.getPort());
        stringBuilder.append(entry.getRestURL());

        //HTTP Get method
        RestTemplate restTemplate = new RestTemplate();
        log.info("Attempting REST request: " + (stringBuilder));
        return restTemplate.getForObject(String.valueOf(stringBuilder), String.class);
    }

}