package hu.gdf.thesis.backend;

import hu.gdf.thesis.model.config.Entry;
import hu.gdf.thesis.model.config.Server;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;


@Service
public class RestClient {
	private static final Logger LOGGER = LoggerFactory.getLogger(RestClient.class);
	private static final String HTTP_TEXT = "http://";

	public String restCall (Server server, Entry entry) {
		try {
			StringBuilder stringBuilder = new StringBuilder(HTTP_TEXT);
			stringBuilder.append(server.getHost());
			stringBuilder.append(":");
			stringBuilder.append(server.getPort());
			stringBuilder.append(entry.getRestURL());

			RestTemplate restTemplate = new RestTemplate();
			//Responseba milyen http kód van, ha nem 200-at dob, akkor lekezelni

			LOGGER.info("Attemtping REST request: " + String.valueOf(stringBuilder));
			return restTemplate.getForObject(String.valueOf(stringBuilder), String.class);

		} catch (HttpStatusCodeException ex) {
			LOGGER.error("Http error occured: "+ ex.getLocalizedMessage());
			return null;
		}
	}

}