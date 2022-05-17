package hu.gdf.thesis.backend;

import hu.gdf.thesis.model.config.Entry;
import hu.gdf.thesis.model.config.Server;

import lombok.extern.log4j.Log4j2;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.net.http.HttpConnectTimeoutException;
import java.net.http.HttpTimeoutException;


@Service

public class RestClient {
    private static final Logger LOGGER = LoggerFactory.getLogger(RestClient.class);

    private static final String HTTP_TEXT = "http://";

    public String restCall(Server server, Entry entry) {

        StringBuilder stringBuilder = new StringBuilder(HTTP_TEXT);
        stringBuilder.append(server.getHost());
        stringBuilder.append(":");
        stringBuilder.append(server.getPort());
        stringBuilder.append(entry.getRestURL());

        RestTemplate restTemplate = new RestTemplate();

        LOGGER.info("Attempting REST request: " + String.valueOf(stringBuilder));

        return restTemplate.getForObject(String.valueOf(stringBuilder), String.class);


    }

}