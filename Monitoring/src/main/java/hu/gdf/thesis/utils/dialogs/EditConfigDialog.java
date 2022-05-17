package hu.gdf.thesis.utils.dialogs;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.IntegerField;
import com.vaadin.flow.component.textfield.TextField;
import hu.gdf.thesis.backend.FileHandler;
import hu.gdf.thesis.model.config.Config;
import hu.gdf.thesis.utils.notifications.CustomNotification;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

public class EditConfigDialog extends Dialog {
    private boolean saveState;
    private Config config = new Config();
    private static final Logger LOGGER = LoggerFactory.getLogger(EditConfigDialog.class);

    public EditConfigDialog(String fileName, @Autowired FileHandler fileHandler) {
        config = fileHandler.deserializeJsonConfig(fileHandler.readFromFile(fileName), Config.class);
        if (config != null) {
            CustomNotification notification = new CustomNotification("Invalid config file");
            notification.open();
        }
        TextField serverHostTF = new TextField("Server Host");
        serverHostTF.setHelperText("Enter the name of the server host you wish to monitor.");
        serverHostTF.setWidth("300px");
        serverHostTF.setValue(config.getServer().getHost());

        IntegerField portField = new IntegerField("Port Number");
        portField.setHelperText("Enter the port number");
        portField.setMin(1);
        portField.setMax(65535);
        portField.setWidth("300px");
        portField.setValue(config.getServer().getPort());

        IntegerField timerField = new IntegerField("Refresh Timer");
        timerField.setHelperText("This value is in seconds.");
        timerField.setMin(10);
        timerField.setMax(600);
        timerField.setWidth("300px");
        timerField.setValue(config.getServer().getRefreshTimer());

        Button cancelButton = new Button("Cancel", buttonClickEvent -> {
            this.close();
        });

        Button saveButton = new Button("Save");
        saveButton.addClickListener(buttonClickEvent -> {
            try {
                if (serverHostTF.getValue().isEmpty() || portField.getValue() == null
                        || timerField.getValue() == null || portField.getValue() <= 0
                        || portField.getValue() > 65535 || timerField.getValue() < 10 || timerField.getValue() > 600) {
                    CustomNotification errorNotification = new CustomNotification("Save failed - Invalid or empty Input.");
                    errorNotification.open();
                }
                config.getServer().setHost(serverHostTF.getValue());
                config.getServer().setPort(portField.getValue());
                config.getServer().setRefreshTimer(timerField.getValue());
                fileHandler.writeConfigToFile(fileName, fileHandler.serializeJsonConfig(config));
                saveState = true;
                this.close();
            } catch (Exception ex) {
                LOGGER.error("Config Editor Dialog produced error, when trying to save config", ex);
            }
        });

        Button deleteButton = new Button("Delete");
        deleteButton.addClickListener(buttonClickEvent -> {
            try {
                ConfirmDialog confirmDialog = new ConfirmDialog(fileName);
                confirmDialog.open();
                confirmDialog.addDetachListener(detachEvent -> {
                    if (confirmDialog.isDeleteState()) {
                        fileHandler.deleteFile(fileName);
                        saveState = true;
                        this.close();
                    }
                });
            } catch (Exception ex) {
                LOGGER.error("Config Editor Dialog produced error, when trying to delete config", ex);
            }
        });
        this.setCloseOnOutsideClick(false);
        HorizontalLayout buttonLayout = new HorizontalLayout(cancelButton, saveButton, deleteButton);
        VerticalLayout dialogContentLayout = new VerticalLayout(serverHostTF, portField, timerField, buttonLayout);
        this.add(dialogContentLayout);

    }

    public boolean isSaveState() {
        return saveState;
    }
}
