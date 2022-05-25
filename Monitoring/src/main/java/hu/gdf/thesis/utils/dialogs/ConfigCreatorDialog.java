package hu.gdf.thesis.utils.dialogs;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.EmailField;
import com.vaadin.flow.component.textfield.IntegerField;
import com.vaadin.flow.component.textfield.TextField;
import hu.gdf.thesis.backend.FileHandler;
import hu.gdf.thesis.model.config.Address;
import hu.gdf.thesis.model.config.Config;
import hu.gdf.thesis.model.config.Server;
import hu.gdf.thesis.utils.notifications.CustomNotification;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.nio.file.Files;
import java.nio.file.Path;

@Slf4j
public class ConfigCreatorDialog extends Dialog {

    @Getter
    private boolean saveState;

    public ConfigCreatorDialog(@Autowired FileHandler fileHandler) {

        TextField fileNameTF = new TextField("File Name");
        fileNameTF.setHelperText("Enter the name of the configuration file you wish to create, without file extension.");
        fileNameTF.setWidth("350px");

        TextField serverHostTF = new TextField("Server Host");
        serverHostTF.setHelperText("Enter the address of the server host you wish to monitor.");
        serverHostTF.setWidth("350px");

        IntegerField portField = new IntegerField("Port Number");
        portField.setHelperText("Enter the port number");
        portField.setMin(1);
        portField.setMax(65535);
        portField.setWidth("350px");

        IntegerField timerField = new IntegerField("Refresh Timer");
        timerField.setHelperText("This value is in seconds.");
        timerField.setMin(10);
        timerField.setMax(600);
        timerField.setWidth("350px");

        EmailField addressEmailField = new EmailField("Alert Address");
        addressEmailField.setHelperText("Enter an e-mail address you wish to send alerts to.");
        addressEmailField.setWidth("350px");
        addressEmailField.setErrorMessage("Please enter a valid email address");

        Button cancelButton = new Button("Cancel", e -> this.close());
        Button saveButton = new Button("Save to Config");
        saveButton.addClickListener(buttonClickEvent -> {
            try {
                if (fileNameTF.getValue().isEmpty() || serverHostTF.getValue().isEmpty()
                        || portField.getValue() == null || timerField.getValue() == null || portField.getValue() <= 0
                        || portField.getValue() > 65535 || timerField.getValue() < 10 || timerField.getValue() > 600) {
                    CustomNotification errorNotification = new CustomNotification("Save failed - Invalid or empty Input.");
                    errorNotification.open();
                } else if (Files.exists(Path.of(fileNameTF.getValue() + "+json"))) {
                    CustomNotification errorNotification = new CustomNotification("Save failed - File already exists.");
                    errorNotification.open();
                } else {
                    Config config = new Config();
                    Server server = new Server();
                    server.setHost(serverHostTF.getValue().trim());
                    server.setPort(portField.getValue());
                    server.setRefreshTimer(timerField.getValue());
                    if(!addressEmailField.getValue().isEmpty()) {
                            Address address = new Address();
                            address.setAddress(addressEmailField.getValue());
                            server.getAddresses().add(address);
                    }
                    config.setServer(server);
                    if(!fileHandler.fileExists(fileNameTF.getValue().trim())) {
                        fileHandler.createFile(fileNameTF.getValue().trim());

                        fileHandler.writeConfigToFile(fileNameTF.getValue().trim()+ ".json", fileHandler.serializeJsonConfig(config));
                        saveState = true;
                        this.close();
                    } else {
                        CustomNotification errorNotification = new CustomNotification("Config file already exists.");
                        errorNotification.open();
                    }

                }

            } catch (NullPointerException ex) {
                log.error("Config Creator Dialog produced error, when trying to save", ex);
            }
        });
        this.setCloseOnOutsideClick(false);
        HorizontalLayout buttonLayout = new HorizontalLayout(cancelButton, saveButton);
        VerticalLayout dialogContentLayout = new VerticalLayout(fileNameTF, serverHostTF, portField, timerField, addressEmailField, buttonLayout);
        this.add(dialogContentLayout);
    }

}
