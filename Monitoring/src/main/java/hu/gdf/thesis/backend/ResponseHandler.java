package hu.gdf.thesis.backend;

import com.google.gson.JsonSyntaxException;
import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.PathNotFoundException;
import hu.gdf.thesis.model.Response;
import hu.gdf.thesis.model.config.*;
import lombok.extern.slf4j.Slf4j;
import net.minidev.json.JSONArray;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.ResourceAccessException;

import java.util.List;

@Slf4j
public class ResponseHandler {

    //Builds list of Response objects, this list is used for building the monitoring grid's data view
    public void buildResponseList(Config config, RestClient restClient, List<Response> responseList) {
        try {
            responseList.clear();

            Server server = config.getServer();

            for (Category category : server.getCategories()) {
                for (Entry entry : category.getEntries()) {
                    try {
                        //JSON response requested from the server
                        String responseJson = restClient.restCall(server, entry);

                        for (RestField restField : entry.getRestFields()) {
                            try {
                                //Using JsonPath to find singular fields in JSON
                                DocumentContext jsonContext = JsonPath.parse(responseJson);
                                if (!(jsonContext.read(restField.getFieldPath()) instanceof JSONArray)) {

                                    //Handle field's value as String
                                    Object fieldValueAsObject = jsonContext.read(restField.getFieldPath());
                                    String fieldValue = String.valueOf(fieldValueAsObject);

                                    Response response = new Response();
                                    buildResponse(response, server, category, entry, restField.getFieldPath(), fieldValue);

                                    if (!restField.getOperations().isEmpty()) {
                                        doColoring(response, restField, fieldValue);
                                    }
                                    responseList.add(response);

                                    //Using JsonPath to find list of fields in JSON
                                } else if (jsonContext.read(restField.getFieldPath()) instanceof JSONArray) {

                                    int indexOfArrayElement = 0;
                                    List<Object> fieldValues = jsonContext.read(restField.getFieldPath());

                                    for (Object fieldValueAsObject : fieldValues) {

                                        //Handle field's value as String, index the field name's for better readability
                                        String fieldValue = String.valueOf(fieldValueAsObject);
                                        String fieldPath = String.valueOf(indexOfArrayElement) + ". " + restField.getFieldPath();

                                        indexOfArrayElement++;

                                        Response response = new Response();
                                        buildResponse(response, server, category, entry, fieldPath, fieldValue);

                                        if (!restField.getOperations().isEmpty()) {
                                            doColoring(response, restField, fieldValue);
                                        }
                                        responseList.add(response);
                                    }
                                }
                                //When fields are not found, handle exception, and to responseList
                            } catch (PathNotFoundException ex) {
                                log.warn("Unable to find field(s) in response: " + restField.getFieldPath());

                                //Build individual Response object
                                Response response = new Response();
                                buildResponse(response, server, category, entry, restField.getFieldPath(), "-Field(s) Not Found-");
                                response.setColor("colorPurple");
                                responseList.add(response);

                            }
                        }

                        //HTTP Error exception handling
                    } catch (ResourceAccessException | HttpServerErrorException ex) {

                        Response response = new Response();
                        response.setColor("colorRed");
                        buildResponse(response, server, category, entry, "-Resource unavailable-!", "-Server not responding-");
                        responseList.add(response);

                        log.error("Error when building response list, server unreachable:  " + ex.getLocalizedMessage());
                    } catch (HttpStatusCodeException ex) {
                        Response response = new Response();
                        response.setColor("colorRed");
                        buildResponse(response, server, category, entry, "-Resource unavailable-!", String.valueOf(ex.getStatusCode()));
                        responseList.add(response);
                        log.error("Unable to get resource from server" + "http://" + server.getHost() + ":"
                                + server.getPort().toString() + entry.getRestURL(), ex.getLocalizedMessage());
                    }
                }
            }

        } catch (NullPointerException | JsonSyntaxException ex) {
            log.error("Error when building response list, please check the integrity of your config files: " + ex.getMessage());
        }
    }

    //Sets the response object's variables with setter methods
    private void buildResponse(Response response, Server server, Category category, Entry entry, String fieldName, String fieldValue) {
        try {
            response.setHostName(server.getHost() + " : " + server.getPort());
            response.setCategoryType(category.getType());
            response.setRestURL(entry.getRestURL());
            response.setFieldPath(fieldName);
            response.setFieldValue(String.valueOf(fieldValue));
        } catch (NullPointerException ex) {
            log.error("Error when building individual response: " + ex.getLocalizedMessage());
        }

    }

    //If operation conditions are met, sets color variable of response object
    private void doColoring(Response response, RestField restField, String fieldValue) {
        try {
            for (Operation operation : restField.getOperations()) {
                OperationHandler operationHandler = new OperationHandler();
                operationHandler.checkOperation(operation, fieldValue);
                if (operationHandler.isCheckState()) {
                    response.setColor(operationHandler.getColor());
                }
            }
        } catch (Exception ex) {
            log.error("Error when setting color: " + ex.getLocalizedMessage());
        }
    }

}
