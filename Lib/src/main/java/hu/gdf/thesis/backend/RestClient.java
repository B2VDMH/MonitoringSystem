package hu.gdf.thesis.backend;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

@Service
@Slf4j
public class RestClient {
    @Autowired
    WebClient.Builder client;

    //HTTP prefix
    private static final String HTTP_TEXT = "http://";

    //GET request
    public String getRequest(String url) {
        String fullURL = HTTP_TEXT.concat(url);
        log.info("Attempting REST request: " + fullURL);

        return client.build().get().uri(fullURL)
                .retrieve().bodyToMono(String.class).block();
    }
}