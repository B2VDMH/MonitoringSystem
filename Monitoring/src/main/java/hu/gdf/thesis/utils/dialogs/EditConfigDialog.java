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
import org.springframework.beans.factory.annotation.Autowired;

public class  EditConfigDialog extends Dialog {
    private boolean saveState;

    public EditConfigDialog(String fileName, @Autowired FileHandler fileHandler) {
        TextField serverHostTF = new TextField("Server Host");
        serverHostTF.setHelperText("Enter the name of the server host you wish to monitor.");

        IntegerField portField = new IntegerField("Port Number");
        portField.setHelperText("Enter the port number");
        portField.setMin(1);
        portField.setMax(65535);

        IntegerField timerField = new IntegerField("Refresh Timer");
        timerField.setHelperText("This value is in seconds.");
        timerField.setMin(10);
        timerField.setMax(600);

        Button cancelButton = new Button("Cancel", buttonClickEvent -> {this.close();});

        Button saveButton = new Button("Save");
        saveButton.addClickListener(buttonClickEvent -> {
            if(serverHostTF.getValue().isEmpty() || portField.getValue() == null ||timerField.getValue() == null) {
                CustomNotification errorNotification = new CustomNotification("Save failed - Invalid or empty Input.");
                errorNotification.open();
            }
            Config config = fileHandler.deserializeJsonConfig(fileHandler.readFromFile(fileName), Config.class);
            config.getServer().setHost(serverHostTF.getValue());
            config.getServer().setPort(portField.getValue());
            config.getServer().setRefreshTimer(timerField.getValue());
            fileHandler.writeConfigToFile(fileName, fileHandler.serializeJsonConfig(config));
            saveState=true;
            this.close();
        });

        Button deleteButton = new Button("Delete");
        deleteButton.addClickListener(buttonClickEvent -> {
            ConfirmDialog confirmDialog = new ConfirmDialog(fileName);
            confirmDialog.open();
            confirmDialog.addDetachListener(detachEvent -> {
                if(confirmDialog.isDeleteState()) {
                    fileHandler.deleteFile(fileName);
                    saveState=true;
                    this.close();
                }
            });
        });

        HorizontalLayout buttonLayout = new HorizontalLayout(cancelButton, saveButton, deleteButton);
        VerticalLayout dialogContentLayout = new VerticalLayout(serverHostTF, portField, timerField, buttonLayout);
        this.add(dialogContentLayout);
    }
    public boolean isSaveState() {
        return saveState;
    }
}
