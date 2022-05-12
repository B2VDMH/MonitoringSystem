package hu.gdf.thesis.backend;

import hu.gdf.thesis.model.config.Entry;
import hu.gdf.thesis.model.config.Server;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class RestClient {
	private static final String URL = "http://";

	public String restCall (Server server, Entry entry) {
		StringBuilder stringBuilder = new StringBuilder(URL);
		stringBuilder.append(server.getHost());
		stringBuilder.append(":");
		stringBuilder.append(server.getPort());
		stringBuilder.append(entry.getRestURL());

		RestTemplate restTemplate = new RestTemplate();
		return restTemplate.getForObject(String.valueOf(stringBuilder), String.class);

	}

}