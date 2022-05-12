package hu.gdf.thesis.alert;

import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;
import hu.gdf.thesis.backend.FileHandler;
import hu.gdf.thesis.backend.OperationHandler;
import hu.gdf.thesis.backend.RestClient;
import hu.gdf.thesis.model.AlertEmailContent;
import hu.gdf.thesis.model.config.*;
import net.minidev.json.JSONArray;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class AlertScheduler {
    @Autowired
    FileHandler fileHandler;
    @Autowired
    RestClient restClient;
    @Autowired
    SmtpMailSender mailSender;

    static Config config = new Config();
    @Scheduled (fixedDelayString = "${scheduled.alert.timer}")
    public void alerting() {

        for(String fileName: fileHandler.listFilesInDirectory()) {
            config = fileHandler.deserializeJsonConfig(fileHandler.readFromFile(fileName),Config.class);
            Server server = config.getServer();

            for(Category category : server.getCategories()) {

                for (Entry entry : category.getEntries()) {

                    String responseJson = restClient.restCall(server, entry);

                    for (RestField restField : entry.getRestFields()) {

                        DocumentContext jsonContext = JsonPath.parse(responseJson);
                        if (!(jsonContext.read(restField.getFieldPath()) instanceof JSONArray)) {
                            String fieldValue = jsonContext.read(restField.getFieldPath());

                            for (Operation operation : restField.getOperation()) {
                                OperationHandler operationHandler = new OperationHandler();
                                operationHandler.checkOperation(operation, fieldValue);
                                if (operationHandler.isEmailState()) {
                                    for(Address address: operation.getAddresses()) {
                                        AlertEmailContent content = new AlertEmailContent();
                                        content.setServerHost(server.getHost() + " : " + server.getPort());
                                        content.setCategory(category.getType());
                                        content.setRestURL(entry.getRestURL());
                                        content.setFieldPath(restField.getFieldPath());
                                        content.setFieldValue(fieldValue);
                                        content.setOperator(operation.getOperator());
                                        content.setValue(operation.getValue());
                                        mailSender.sendEmail(content,address);
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

}
