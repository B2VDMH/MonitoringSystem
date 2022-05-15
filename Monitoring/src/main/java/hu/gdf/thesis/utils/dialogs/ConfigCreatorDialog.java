package hu.gdf.thesis.utils.dialogs;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.IntegerField;
import com.vaadin.flow.component.textfield.TextField;
import hu.gdf.thesis.backend.FileHandler;
import hu.gdf.thesis.model.config.Config;
import hu.gdf.thesis.model.config.Server;
import hu.gdf.thesis.utils.notifications.CustomNotification;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

public class ConfigCreatorDialog extends Dialog {
    private boolean saveState;
    private static final Logger LOGGER = LoggerFactory.getLogger(ConfigCreatorDialog.class);

    public ConfigCreatorDialog(@Autowired FileHandler fileHandler) {

        this.getElement().setAttribute("aria-label", "Create new config");

        TextField fileNameField = new TextField("File Name");
        fileNameField.setHelperText("Enter the name of the configuration file you wish to create.");
        fileNameField.setWidth("300px");

        TextField serverHostTF = new TextField("Server Host");
        serverHostTF.setHelperText("Enter the name of the server host you wish to monitor.");
        serverHostTF.setWidth("300px");

        IntegerField portField = new IntegerField("Port Number");
        portField.setHelperText("Enter the port number");
        portField.setMin(1);
        portField.setMax(65535);
        portField.setWidth("300px");

        IntegerField timerField = new IntegerField("Refresh Timer");
        timerField.setHelperText("This value is in seconds.");
        timerField.setMin(10);
        timerField.setMax(600);
        timerField.setWidth("300px");

        Button cancelButton = new Button("Cancel", e -> this.close());
        Button saveButton = new Button("Save to Config");
        saveButton.addClickListener(buttonClickEvent -> {
            try {
                if (fileNameField.getValue().isEmpty() || serverHostTF.getValue().isEmpty()
                        || portField.getValue() == null || timerField.getValue() == null) {
                    CustomNotification errorNotification = new CustomNotification("Save failed - Invalid or empty Input.");
                    errorNotification.open();
                } else {
                    Config config = new Config();
                    Server server = new Server();

                    server.setHost(serverHostTF.getValue());
                    server.setPort(portField.getValue());
                    server.setRefreshTimer(timerField.getValue());
                    config.setServer(server);

                    fileHandler.createFile(fileNameField.getValue());

                    fileHandler.writeConfigToFile(fileNameField.getValue() + ".json", fileHandler.serializeJsonConfig(config));
                    saveState = true;
                    this.close();
                }

            } catch (Exception ex) {
                LOGGER.error("Config Creator Dialog produced error, when trying to save", ex);
            }
        });
        this.setCloseOnOutsideClick(false);
        HorizontalLayout buttonLayout = new HorizontalLayout(cancelButton, saveButton);
        VerticalLayout dialogContentLayout = new VerticalLayout(fileNameField, serverHostTF, portField, timerField, buttonLayout);
        this.add(dialogContentLayout);
    }

    public boolean isSaveState() {
        return saveState;
    }
}
