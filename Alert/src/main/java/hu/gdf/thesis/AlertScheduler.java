package hu.gdf.thesis;

import com.google.gson.JsonParseException;
import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.PathNotFoundException;
import hu.gdf.thesis.backend.FileHandler;
import hu.gdf.thesis.backend.OperationHandler;
import hu.gdf.thesis.backend.RestClient;
import hu.gdf.thesis.model.*;
import lombok.extern.slf4j.Slf4j;
import net.minidev.json.JSONArray;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClientRequestException;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.util.List;

@Component
@Slf4j
public class AlertScheduler {
    @Autowired
    FileHandler fileHandler;
    @Autowired
    RestClient restClient;
    @Autowired
    SmtpMailSender mailSender;

    //Starts a scheduled task in order to periodically send e-mails to all addresses specified in all available config files.

    @Scheduled(fixedDelayString = "${scheduled.alert.timer}")
    public void alerting() {
        log.info("Scheduling...");
        if (!fileHandler.listFilesInDirectory().isEmpty() || fileHandler.listFilesInDirectory() != null) {
            for (String fileName : fileHandler.listFilesInDirectory()) {
                try {
                    Config config = fileHandler.deserialize(fileName);
                    log.info(fileName);
                    try {
                        if (config.getAddresses().isEmpty()) {
                            log.info("No alert e-mail addresses found in " + fileName + ". Skipping alert.");
                            continue;
                        }
                        //Iterate through categories array in server object
                        for (Category category : config.getCategories()) {

                            //Iterate through entry array in category object
                            for (Endpoint endpoint : category.getEndpoints()) {
                                if (!endpoint.isAlert()) {
                                    log.info("Alerting is turned off for this entry, skipping alerts.");
                                    continue;
                                }
                                try {
                                    //JSON response requested from the server
                                    String url = config.getHost() + ":" + config.getPort().toString() + endpoint.getRestURL();
                                    String responseJson = restClient.getRequest(url);

                                    if (endpoint.getFields().isEmpty()) {
                                        log.info("No fields declared for alerting, skipping alert");
                                        continue;
                                    }
                                    //Iterate through restField array in entry object
                                    for (Field fieldName : endpoint.getFields()) {
                                        if (fieldName.getOperations().isEmpty()) {
                                            log.info("No operations for field: " + fieldName.getFieldPath() + ". Skipping alert.");
                                            continue;
                                        }
                                        try {
                                            //Using JsonPath to find singular fields in response JSON
                                            DocumentContext jsonContext = JsonPath.parse(responseJson);

                                            if (!(jsonContext.read(fieldName.getFieldPath()) instanceof JSONArray)) {

                                                //Handle field's value as String
                                                String fieldValue = jsonContext.read(fieldName.getFieldPath());

                                                //Checks operations array inside restField object, and if needed, send e-mails with handleEmails() method
                                                checkAllOperations(config, category, endpoint.getRestURL(), fieldName, fieldValue);

                                                //Using JsonPath to find multiple fields in response JSON
                                            } else if (jsonContext.read(fieldName.getFieldPath()) instanceof JSONArray) {

                                                List<Object> fieldValues = jsonContext.read(fieldName.getFieldPath());

                                                for (Object fieldValueAsObject : fieldValues) {

                                                    String fieldValue = String.valueOf(fieldValueAsObject);

                                                    //Checks operations array inside restField object, and if needed, send e-mails with handleEmails() method
                                                    checkAllOperations(config, category, endpoint.getRestURL(), fieldName, fieldValue);

                                                }
                                            }
                                            //When field path is not found in response skips alerting.
                                        } catch (PathNotFoundException pathEx) {
                                            log.warn("Unable to find field(s) in response, skipping alert: " + fieldName.getFieldPath());
                                        }
                                    }

                                    //Catch blocks that handle HTTP client and server response errors.
                                } catch (WebClientResponseException ex) {
                                    log.error("Unable to get response from server: " + "http://" + config.getHost() + ":"
                                            + config.getPort().toString() + endpoint.getRestURL(), ex.getLocalizedMessage());

                                    handleEmails(config, category.getType(), endpoint.getRestURL(), "-Did not receive response from the server -!"
                                            , ex.getLocalizedMessage(), "N/A", "N/A");
                                }
                            }
                        }
                    } catch (WebClientRequestException ex) {
                        log.error("Unable to get response from server: " + "http://" + config.getHost() + ":"
                                + config.getPort().toString(), ex.getLocalizedMessage());

                        handleEmails(config, "HTTP Get Request Error", "No available endpoints", "-Resource unavailable-!"
                                , "-Server Unreachable-", "N/A", "N/A");
                    }


                } catch (JsonParseException | NullPointerException ex) {
                    log.error(ex.getLocalizedMessage());
                    log.error("Error when reading from config file, please check the integrity of the file:  " + fileName);
                }
            }
        } else {
            log.warn("No available config files found in directory.");
        }

    }


    //Checks operations array inside restField object, and if needed, send e-mails with handleEmails() method
    private void checkAllOperations(Config config, Category category, String restURL, Field field, String fieldValue) {
        if (field.getOperations() == null) {
            log.info("No operations found, skipping alert");
        } else {

            for (Operation operation : field.getOperations()) {

                OperationHandler operationHandler = new OperationHandler();
                operationHandler.checkOperation(operation, fieldValue);

                if (!operationHandler.isEmailState()) {
                    log.info("Alerting turned off for this operation, skipping alert");

                } else {
                    handleEmails(config, category.getType(),
                            restURL, field.getFieldPath(),
                            fieldValue, operation.getOperator(), operation.getValue());
                }
            }
        }

    }

    //Send e-mails to a list of e-mail addresses
    private void handleEmails(Config config, String type, String restURL,
                              String fieldName, String fieldValue, String operator, String operationValue) {

        AlertEmailContent content = new AlertEmailContent();

        content.setServerHost(config.getHost() + " : " + config.getPort());
        content.setCategory(type);
        content.setRestURL(restURL);
        content.setFieldPath(fieldName);
        content.setFieldValue(fieldValue);
        content.setOperator(operator);
        content.setValue(operationValue);
        mailSender.sendEmail(content, config.getAddresses());
    }
}
