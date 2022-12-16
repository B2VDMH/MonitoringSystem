package hu.gdf.thesis.utils.dialogs;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.IntegerField;
import com.vaadin.flow.component.textfield.TextField;
import hu.gdf.thesis.backend.FileHandler;
import hu.gdf.thesis.model.Config;
import hu.gdf.thesis.utils.notifications.CustomNotification;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ConfigDialog extends Dialog {

    private static CustomNotification notification = new CustomNotification();

    @Getter
    @Setter
    private boolean saveState;

    public ConfigDialog(FileHandler fileHandler) {

        TextField fileNameTF = new TextField("File Name");
        fileNameTF.setHelperText("Enter the name of the configuration file you wish to create, without file extension.");
        fileNameTF.setWidth("350px");

        TextField serverHostTF = new TextField("Server Host");
        serverHostTF.setHelperText("Enter the address of the server host you wish to monitor.");
        serverHostTF.setWidth("350px");

        IntegerField portField = new IntegerField("Port Number");
        portField.setHelperText("Enter the port number");
        portField.setMin(1025);
        portField.setMax(65535);
        portField.setWidth("350px");

        IntegerField timerField = new IntegerField("Refresh Timer");
        timerField.setHelperText("This value is in seconds.");
        timerField.setMin(10);
        timerField.setMax(600);
        timerField.setWidth("350px");


        Button cancelButton = new Button("Cancel", e -> this.close());
        Button saveButton = new Button("Save to Config");
        saveButton.addClickListener(buttonClickEvent -> {
            try {
                if (fileHandler.fileExists(fileNameTF.getValue())) {

                    notification.setText("Save failed - File already exists.");
                    notification.open();

                } else if (fileNameTF.getValue().isEmpty() || serverHostTF.getValue().isEmpty()
                        || portField.getValue() == null || timerField.getValue() == null || portField.getValue() < 1024
                        || portField.getValue() > 65536 || timerField.getValue() < 10 || timerField.getValue() > 600) {

                    notification.setText("Save failed - Invalid or empty Input.");
                    notification.open();

                } else {
                    Config config = new Config();
                    config.setHost(serverHostTF.getValue().trim());
                    config.setPort(portField.getValue());
                    config.setRefreshTimer(timerField.getValue());

                    if(!fileHandler.fileExists(fileNameTF.getValue().trim())) {
                        fileHandler.createFile(fileNameTF.getValue().trim());

                        fileHandler.writeConfigToFile(fileNameTF.getValue().trim()+ ".json", config);
                        setSaveState(true);
                        this.close();
                    } else {
                        notification.setText("Config file already exists.");
                        notification.open();
                    }

                }

            } catch (NullPointerException ex) {
                log.error("Config Creator Dialog produced error, when trying to save", ex);
            }
        });
        this.setCloseOnOutsideClick(false);
        HorizontalLayout buttonLayout = new HorizontalLayout(cancelButton, saveButton);
        VerticalLayout dialogContentLayout = new VerticalLayout(fileNameTF, serverHostTF, portField, timerField, buttonLayout);
        this.add(dialogContentLayout);
    }

}
