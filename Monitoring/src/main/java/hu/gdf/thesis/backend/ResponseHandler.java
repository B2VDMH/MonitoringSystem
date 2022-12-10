package hu.gdf.thesis.backend;

import com.google.gson.JsonSyntaxException;
import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.PathNotFoundException;
import hu.gdf.thesis.model.*;
import lombok.extern.slf4j.Slf4j;
import net.minidev.json.JSONArray;
import org.springframework.web.reactive.function.client.WebClientRequestException;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.util.List;

@Slf4j
public class ResponseHandler {

    //Builds list of Response objects, this list is used for building the monitoring grid's data view
    public void buildResponseList(Config config, RestClient restClient, List<Response> responseList) {
        try {
            responseList.clear();
            try {
                for (Category category : config.getCategories()) {
                    for (Endpoint endpoint : category.getEndpoints()) {

                        //JSON response requested from the server
                        try {
                            String url = config.getHost() + ":" + config.getPort().toString() + endpoint.getRestURL();
                            String responseJson = restClient.getRequest(url);

                            for (Field field : endpoint.getFields()) {
                                try {
                                    //Using JsonPath to find singular fields in JSON
                                    DocumentContext jsonContext = JsonPath.parse(responseJson);
                                    if (!(jsonContext.read(field.getFieldPath()) instanceof JSONArray)) {

                                        //Handle field's value as String
                                        Object fieldValueAsObject = jsonContext.read(field.getFieldPath());
                                        String fieldValue = String.valueOf(fieldValueAsObject);

                                        Response response = new Response();
                                        buildResponse(response, config, category.getType(), endpoint.getRestURL(), field.getFieldPath(), fieldValue);

                                        if (!field.getOperations().isEmpty()) {
                                            executeAction(response, field, fieldValue);
                                        }
                                        responseList.add(response);

                                        //Using JsonPath to find list of fields in JSON
                                    } else if (jsonContext.read(field.getFieldPath()) instanceof JSONArray) {

                                        int indexOfArrayElement = 0;
                                        List<Object> fieldValues = jsonContext.read(field.getFieldPath());

                                        for (Object fieldValueAsObject : fieldValues) {

                                            //Handle field's value as String, index the field name's for better readability
                                            String fieldValue = String.valueOf(fieldValueAsObject);
                                            String fieldPath = String.valueOf(indexOfArrayElement) + ". " + field.getFieldPath();

                                            indexOfArrayElement++;

                                            Response response = new Response();
                                            buildResponse(response, config, category.getType(), endpoint.getRestURL(), fieldPath, fieldValue);

                                            if (!field.getOperations().isEmpty()) {
                                                executeAction(response, field, fieldValue);
                                            }
                                            responseList.add(response);
                                        }
                                    }
                                    //When fields are not found, handle exception, and to responseList
                                } catch (PathNotFoundException ex) {
                                    //Build individual Response object
                                    Response response = new Response();
                                    buildResponse(response, config, category.getType(), endpoint.getRestURL(), field.getFieldPath(), "-Field(s) Not Found-");
                                    response.setAction("colorPurple");
                                    responseList.add(response);
                                    log.warn("Unable to find field(s) in response: " + field.getFieldPath());
                                }
                            }

                            //HTTP Error exception handling
                        } catch (WebClientResponseException ex) {
                            Response response = new Response();
                            response.setAction("colorRed");
                            buildResponse(response, config, category.getType(), endpoint.getRestURL(), "-Resource unavailable-!", ex.getStatusCode().toString());
                            responseList.add(response);
                            log.error("Error when building response list:  " + ex.getLocalizedMessage());
                        }
                    }
                }
            } catch (WebClientRequestException ex) {
                Response response = new Response();
                response.setAction("colorRed");
                buildResponse(response, config, "Error", "No available endpoint", "-Server Unavailable-!", "-No server found on this address-");
                responseList.add(response);
                log.error("Error when building response list:  " + ex.getLocalizedMessage());
            }
        } catch (NullPointerException | JsonSyntaxException ex) {
            log.error("Error when building response list, please check the integrity of your config files: " + ex.getMessage());
        }
    }

    //Sets the response object's attributes with setter methods
    private void buildResponse(Response response, Config config, String type, String restURL, String fieldName, String fieldValue) {
        try {
            response.setHostName(config.getHost() + " : " + config.getPort());
            response.setCategoryType(type);
            response.setRestURL(restURL);
            response.setFieldPath(fieldName);
            response.setFieldValue(String.valueOf(fieldValue));
        } catch (NullPointerException ex) {
            log.error("Error when building individual response: " + ex.getLocalizedMessage());
        }

    }

    //If operation conditions are met, sets color variable of response object
    private void executeAction(Response response, Field field, String fieldValue) {
        try {
            for (Operation operation : field.getOperations()) {
                OperationHandler operationHandler = new OperationHandler();
                operationHandler.checkOperation(operation, fieldValue);
                if (operationHandler.isCheckState()) {
                    response.setAction(operationHandler.getAction());
                }
            }
        } catch (Exception ex) {
            log.error("Error when setting color: " + ex.getLocalizedMessage());
        }
    }

}
