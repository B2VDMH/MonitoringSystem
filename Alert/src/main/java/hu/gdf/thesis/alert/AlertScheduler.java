package hu.gdf.thesis.alert;

import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.PathNotFoundException;
import hu.gdf.thesis.backend.FileHandler;
import hu.gdf.thesis.backend.OperationHandler;
import hu.gdf.thesis.backend.RestClient;
import hu.gdf.thesis.model.AlertEmailContent;
import hu.gdf.thesis.model.config.*;
import net.minidev.json.JSONArray;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.ResourceAccessException;

import java.util.List;

@Component
public class AlertScheduler {
    @Autowired
    FileHandler fileHandler;
    @Autowired
    RestClient restClient;
    @Autowired
    SmtpMailSender mailSender;

    private static final Logger LOGGER = LoggerFactory.getLogger(AlertScheduler.class);
    private static Config config = new Config();

    @Scheduled(fixedDelayString = "${scheduled.alert.timer}")
    public void alerting() {
        LOGGER.info("Scheduling...");
        try {
            if (!fileHandler.listFilesInDirectory().isEmpty()) {

                for (String fileName : fileHandler.listFilesInDirectory()) {

                    config = fileHandler.deserializeJsonConfig(fileHandler.readFromFile(fileName), Config.class);
                    Server server = config.getServer();

                    for (Category category : server.getCategories()) {

                        for (Entry entry : category.getEntries()) {

                            try {
                                String responseJson = restClient.restCall(server, entry);

                                for (RestField restField : entry.getRestFields()) {
                                    try {
                                        DocumentContext jsonContext = JsonPath.parse(responseJson);

                                        if (!(jsonContext.read(restField.getFieldPath()) instanceof JSONArray)) {

                                            String fieldValue = jsonContext.read(restField.getFieldPath());

                                            for (Operation operation : restField.getOperation()) {

                                                OperationHandler operationHandler = new OperationHandler();
                                                operationHandler.checkOperation(operation, fieldValue);

                                                if (operationHandler.isEmailState()) {

                                                    for (Address address : operation.getAddresses()) {

                                                        AlertEmailContent content = new AlertEmailContent();
                                                        buildAlertEmailContent(content, server, category, entry, restField, fieldValue, operation);
                                                        LOGGER.info("Sending E-mail to: " + address.getAddress());
                                                        mailSender.sendEmail(content, address);
                                                    }
                                                }
                                            }
                                        } else if (jsonContext.read(restField.getFieldPath()) instanceof JSONArray) {

                                            List<Object> fieldValues = jsonContext.read(restField.getFieldPath());

                                            for (Object fieldValueAsObject : fieldValues) {

                                                String fieldValue = String.valueOf(fieldValueAsObject);

                                                for (Operation operation : restField.getOperation()) {

                                                    OperationHandler operationHandler = new OperationHandler();
                                                    operationHandler.checkOperation(operation, fieldValue);

                                                    if (operationHandler.isEmailState()) {

                                                        for (Address address : operation.getAddresses()) {
                                                            if (operation.getAddresses().isEmpty()) {
                                                                LOGGER.warn("No address found, e-mail was not sent.");
                                                                continue;
                                                            }
                                                            AlertEmailContent content = new AlertEmailContent();
                                                            buildAlertEmailContent(content, server, category, entry, restField, fieldValue, operation);
                                                            LOGGER.info("Sending E-mail to: " + address.getAddress());
                                                            mailSender.sendEmail(content, address);
                                                        }
                                                    }
                                                }
                                            }

                                        }
                                    } catch (PathNotFoundException pathEx) {
                                        LOGGER.warn("Unable to find field(s) in response: " + restField.getFieldPath());
                                        AlertEmailContent content = new AlertEmailContent();
                                        buildPathNotFoundEmailAlertContent(content, server, category, entry, restField);
                                        LOGGER.info("Sending E-mail to: " + content.getAddress().getAddress());
                                        mailSender.sendEmail(content, content.getAddress());
                                    }
                                }
                            } catch (HttpStatusCodeException ex) {

                            } catch (ResourceAccessException ex) {

                            }
                        }
                    }
                }
            }

        } catch (Exception ex) {
            LOGGER.error("Error in scheduling alerts. ", ex);
        }
    }

    private void buildAlertEmailContent(AlertEmailContent content, Server server, Category category, Entry entry,
                                        RestField restField, String fieldValue, Operation operation) {
        content.setServerHost(server.getHost() + " : " + server.getPort());
        content.setCategory(category.getType());
        content.setRestURL(entry.getRestURL());
        content.setFieldPath(restField.getFieldPath());
        content.setFieldValue(fieldValue);
        content.setOperator(operation.getOperator());
        content.setValue(operation.getValue());
    }

    private void buildPathNotFoundEmailAlertContent(AlertEmailContent content, Server server, Category category, Entry entry, RestField restField) {
        try {
            for (Operation operation : restField.getOperation()) {
                for (Address address : operation.getAddresses()) {
                    content.setServerHost(server.getHost() + " : " + server.getPort());
                    content.setCategory(category.getType());
                    content.setRestURL(entry.getRestURL());
                    content.setFieldPath(restField.getFieldPath());
                    content.setFieldValue("-Field(s) not found.");
                    content.setOperator(operation.getOperator());
                    content.setValue(operation.getValue());
                    content.setAddress(address);
                }
            }
        } catch (Exception ex) {
            LOGGER.error("Error at building Path not Found e-mail: " + ex.getLocalizedMessage());
        }
    }

    private void buildErrorEmailAlertContent(AlertEmailContent content, Server server, Category category, Entry entry) {
        try {
            String fieldName = "";
            for(RestField restField : entry.getRestFields()) {
                if(fieldName == restField.getFieldPath()) {

                    fieldName = restField.getFieldPath();
                }

            }
        } catch (Exception ex) {
        LOGGER.error("Error at building HTTP Error e-mail: " + ex.getLocalizedMessage());
         }
    }
}
