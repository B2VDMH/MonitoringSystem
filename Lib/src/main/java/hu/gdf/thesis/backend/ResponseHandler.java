package hu.gdf.thesis.backend;

import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.PathNotFoundException;
import hu.gdf.thesis.model.Response;
import hu.gdf.thesis.model.config.*;
import net.minidev.json.JSONArray;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class ResponseHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(ResponseHandler.class);

    public void buildResponseList (Config config, RestClient restClient, List<Response> responseList) {
        Server server = config.getServer();

        for (Category category : server.getCategories()) {
            for (Entry entry : category.getEntries()) {

                String responseJson = restClient.restCall(server, entry);

                if (responseJson == null) {
                    LOGGER.warn("Response JSON was empty on REST call: " + "http://" + server.getHost() + ":"
                            + server.getPort().toString() + entry.getRestURL());
                    continue;
                }
                for (RestField restField : entry.getRestFields()) {
                    try {
                        DocumentContext jsonContext = JsonPath.parse(responseJson);
                        if (!(jsonContext.read(restField.getFieldPath()) instanceof JSONArray)) {
                            LOGGER.info("Found field on path: " + restField.getFieldPath());
                            Object fieldValueAsObject = jsonContext.read(restField.getFieldPath());
                            String fieldValue = String.valueOf(fieldValueAsObject);
                            Response response = new Response();

                            buildResponse(response, server, category, entry, restField, fieldValue);
                            doColoring(response, restField, fieldValue);
                            responseList.add(response);

                        } else if (jsonContext.read(restField.getFieldPath()) instanceof JSONArray) {
                            List<Object> fieldValues = jsonContext.read(restField.getFieldPath());
                            for (Object fieldValueAsObject : fieldValues) {
                                LOGGER.info("Found field on path: " + restField.getFieldPath());
                                String fieldValue = String.valueOf(fieldValueAsObject);
                                Response response = new Response();
                                buildResponse(response, server, category, entry, restField, fieldValue);
                                doColoring(response, restField, fieldValue);
                                responseList.add(response);
                            }
                        }

                    } catch (PathNotFoundException pathEx) {
                        LOGGER.warn("Unable to find field(s) in response: " + restField.getFieldPath());
                        Response response = new Response();
                        buildResponse(response, server, category, entry, restField, "-Field Not Found-");
                        response.setColor("colorPurple");
                        responseList.add(response);
                    }
                }
            }
        }
    }
    private void buildResponse(Response response, Server server, Category category, Entry entry, RestField restField, String fieldValue) {
        try {
            response.setHostName(server.getHost() + " : " + server.getPort());
            response.setCategoryType(category.getType());
            response.setRestURL(entry.getRestURL());
            response.setFieldPath(restField.getFieldPath());
            response.setFieldValue(String.valueOf(fieldValue));
        }  catch (Exception ex) {
            LOGGER.error("Error when building response: ", ex);
        }

    }

    private void doColoring(Response response, RestField restField, String fieldValue) {
        try {
            for (Operation operation : restField.getOperation()) {
                OperationHandler operationHandler = new OperationHandler();
                operationHandler.checkOperation(operation, fieldValue);
                if (operationHandler.isCheckState()) {
                    response.setColor(operationHandler.getColor());
                }
            }
        } catch (Exception ex) {
            LOGGER.error("Error when setting color: ", ex);
        }
    }
}
