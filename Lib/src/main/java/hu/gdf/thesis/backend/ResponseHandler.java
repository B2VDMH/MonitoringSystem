package hu.gdf.thesis.backend;

import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.JsonPathException;
import com.jayway.jsonpath.PathNotFoundException;
import hu.gdf.thesis.model.Response;
import hu.gdf.thesis.model.config.*;
import lombok.extern.java.Log;
import net.minidev.json.JSONArray;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.ResourceAccessException;

import java.util.ArrayList;
import java.util.List;

public class ResponseHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(ResponseHandler.class);

    public void buildResponseList(Config config, RestClient restClient, List<Response> responseList) {
        try {
            responseList.clear();
            Server server = config.getServer();

            for (Category category : server.getCategories()) {

                for (Entry entry : category.getEntries()) {

                    try {

                        String responseJson = restClient.restCall(server, entry);

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

                            } catch (PathNotFoundException ex) {
                                LOGGER.warn("Unable to find field(s) in response: " + restField.getFieldPath());

                                Response response = new Response();
                                buildResponse(response, server, category, entry, restField, "-Field Not Found-");
                                response.setColor("colorPurple");
                                responseList.add(response);

                            }
                        }
                    } catch (HttpStatusCodeException ex) {
                        LOGGER.warn("Unable to get response from server" + "http://" + server.getHost() + ":"
                                + server.getPort().toString() + entry.getRestURL(), ex.getLocalizedMessage());
                        Response response = new Response();
                        response.setColor("colorRed");
                        buildErrorResponse(response, server, category, entry, "-Resource unavailable-!", String.valueOf(ex.getStatusCode()));
                        responseList.add(response);

                    } catch (ResourceAccessException ex) {
                        LOGGER.error("Error when building response list, server unreachable:  " + ex.getLocalizedMessage());
                        Response response = new Response();
                        response.setColor("colorRed");
                        buildErrorResponse(response, server, category, entry, "-Resource unavailable-!", "-Server not responding-");
                        responseList.add(response);
                    }
                }
            }
        } catch (Exception ex) {
            LOGGER.error("Error when building response list" + ex.getLocalizedMessage());
        }
    }

    private void buildResponse(Response response, Server server, Category category, Entry entry, RestField restField, String fieldValue) {
        try {
            response.setHostName(server.getHost() + " : " + server.getPort());
            response.setCategoryType(category.getType());
            response.setRestURL(entry.getRestURL());
            response.setFieldPath(restField.getFieldPath());
            response.setFieldValue(String.valueOf(fieldValue));
        } catch (NullPointerException ex) {
            LOGGER.error("Error when building response: ", ex);
        }

    }

    private void buildErrorResponse(Response response, Server server, Category category, Entry entry,
                                    String fieldName, String fieldValue) {
        try {
            response.setHostName(server.getHost() + " : " + server.getPort());
            response.setCategoryType(category.getType());
            response.setRestURL(entry.getRestURL());
            response.setFieldPath(fieldName);
            response.setFieldValue(fieldValue);
        } catch (NullPointerException ex) {
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
