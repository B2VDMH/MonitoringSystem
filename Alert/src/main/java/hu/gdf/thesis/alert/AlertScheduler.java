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
    static Config config = new Config();

    @Scheduled (fixedDelayString = "${scheduled.alert.timer}")
    public void alerting() {
        try {

            for(String fileName: fileHandler.listFilesInDirectory()) {

                config = fileHandler.deserializeJsonConfig(fileHandler.readFromFile(fileName),Config.class);
                Server server = config.getServer();

                for(Category category : server.getCategories()) {

                    for (Entry entry : category.getEntries()) {

                        String responseJson = restClient.restCall(server, entry);

                        if(responseJson == null) {
                            LOGGER.warn("Response JSON was empty on REST call: " + "http://" + server.getHost() + ":"
                                    + server.getPort().toString() +"/" +"/"+ entry.getRestURL());
                            continue;
                        }

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
                            }
                        }
                    }
                }
            }
        } catch (Exception ex) {
            LOGGER.error("Error in alerting", ex);
        }
    }

    private void buildAlertEmailContent (AlertEmailContent content, Server server, Category category, Entry entry,
                                         RestField restField, String fieldValue, Operation operation) {
        content.setServerHost(server.getHost() + " : " + server.getPort());
        content.setCategory(category.getType());
        content.setRestURL(entry.getRestURL());
        content.setFieldPath(restField.getFieldPath());
        content.setFieldValue(fieldValue);
        content.setOperator(operation.getOperator());
        content.setValue(operation.getValue());
    }
}
