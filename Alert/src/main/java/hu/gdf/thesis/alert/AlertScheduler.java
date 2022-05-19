package hu.gdf.thesis.alert;

import com.google.gson.JsonParseException;
import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.PathNotFoundException;
import hu.gdf.thesis.backend.FileHandler;
import hu.gdf.thesis.backend.OperationHandler;
import hu.gdf.thesis.backend.RestClient;
import hu.gdf.thesis.model.config.*;
import lombok.extern.slf4j.Slf4j;
import net.minidev.json.JSONArray;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.ResourceAccessException;

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

    private static Config config = new Config();

    //Starts a scheduled task in order to periodically send e-mails to all addresses specified in all available config files.
    @Scheduled(fixedDelayString = "${scheduled.alert.timer}")
    public void alerting() {
        log.info("Scheduling...");
        if (!fileHandler.listFilesInDirectory().isEmpty() || fileHandler.listFilesInDirectory() != null) {
            for (String fileName : fileHandler.listFilesInDirectory()) {

                try {
                    config = fileHandler.deserializeJsonConfig(fileHandler.readFromFile(fileName), Config.class);
                    Server server = config.getServer();

                    if (server.getAddresses().isEmpty()) {
                        log.info("No alert e-mail addresses found in " + fileName + ". Skipping alert.");
                        continue;
                    }
                    //Iterate through categories array in server object
                    for (Category category : server.getCategories()) {

                        //Iterate through entry array in category object
                        for (Entry entry : category.getEntries()) {
                            if (!entry.isAlert()) {
                                log.info("Alerting is turned off for this entry, skipping alerts.");
                                continue;
                            }
                            try {
                                //JSON response requested from the server
                                String responseJson = restClient.restCall(server, entry);

                                if (entry.getRestFields().isEmpty()) {
                                    log.info("No fields declared for alerting, skipping alert");
                                    continue;
                                }
                                //Iterate through restField array in entry object
                                for (RestField restField : entry.getRestFields()) {
                                    if (restField.getOperations().isEmpty()) {
                                        log.info("No operations for field: " + restField.getFieldPath() + ". Skipping alert.");
                                        continue;
                                    }
                                    try {
                                        //Using JsonPath to find singular fields in response JSON
                                        DocumentContext jsonContext = JsonPath.parse(responseJson);

                                        if (!(jsonContext.read(restField.getFieldPath()) instanceof JSONArray)) {

                                            //Handle field's value as String
                                            String fieldValue = jsonContext.read(restField.getFieldPath());

                                            //Checks operations array inside restField object, and if needed, send e-mails with handleEmails() method
                                            checkAllOperations(server, category, entry, restField, fieldValue);

                                            //Using JsonPath to find multiple fields in response JSON
                                        } else if (jsonContext.read(restField.getFieldPath()) instanceof JSONArray) {

                                            List<Object> fieldValues = jsonContext.read(restField.getFieldPath());

                                            for (Object fieldValueAsObject : fieldValues) {

                                                String fieldValue = String.valueOf(fieldValueAsObject);

                                                //Checks operations array inside restField object, and if needed, send e-mails with handleEmails() method
                                                checkAllOperations(server, category, entry, restField, fieldValue);

                                            }
                                        }
                                        //When field path is not found in response skips alerting.
                                    } catch (PathNotFoundException pathEx) {
                                        log.warn("Unable to find field(s) in response, skipping alert: " + restField.getFieldPath());
                                    }
                                }

                                //Catch blocks that handle HTTP client and server response errors.
                            } catch (ResourceAccessException | HttpServerErrorException ex) {
                                log.error("Unable to get response from server" + "http://" + server.getHost() + ":"
                                        + server.getPort().toString() + entry.getRestURL(), ex.getLocalizedMessage());

                                handleEmails(server, category, entry, "-Did not receive response from the server -!"
                                        , ex.getLocalizedMessage(), "N/A", "N/A");

                            } catch (HttpStatusCodeException ex) {
                                log.error("Unable to get response from server: " + "http://" + server.getHost() + ":"
                                        + server.getPort().toString() + entry.getRestURL(), ex.getLocalizedMessage());

                                handleEmails(server, category, entry, "-Resource unavailable-!"
                                        , String.valueOf(ex.getStatusCode()), "N/A", "N/A");
                                log.info("Sending E-mail to: " + server.getAddresses());

                            }
                        }
                    }
                } catch (JsonParseException | NullPointerException ex) {
                    log.error("Error when reading from config file, please check the integrity of the file:  " + fileName);
                }
            }

        } else {
            log.warn("No available config files found in directory.");
        }

    }


    //Checks operations array inside restField object, and if needed, send e-mails with handleEmails() method
    private void checkAllOperations(Server server, Category category, Entry entry, RestField restField, String fieldValue) {

        for (Operation operation : restField.getOperations()) {

            OperationHandler operationHandler = new OperationHandler();
            operationHandler.checkOperation(operation, fieldValue);

            if (!operationHandler.isEmailState()) {
                log.info("Alerting turned off for this operation, skipping alert");

            } else {
                handleEmails(server, category, entry, restField.getFieldPath(), fieldValue, operation.getOperator(), operation.getValue());
            }

        }

    }

    //Send e-mails to a list of e-mail addresses
    private void handleEmails(Server server, Category category, Entry entry,
                              String fieldName, String fieldValue, String operator, String operationValue) {

        AlertEmailContent content = new AlertEmailContent();

        content.setServerHost(server.getHost() + " : " + server.getPort());
        content.setCategory(category.getType());
        content.setRestURL(entry.getRestURL());
        content.setFieldPath(fieldName);
        content.setFieldValue(fieldValue);
        content.setOperator(operator);
        content.setValue(operationValue);

        mailSender.sendEmail(content, server.getAddresses());
    }
}
