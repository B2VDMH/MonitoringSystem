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
public class EditConfigDialog extends Dialog {
    private static CustomNotification notification = new CustomNotification();
    @Getter
    @Setter
    private boolean saveState;
    public EditConfigDialog(String fileName, FileHandler fileHandler) {
        Config config = fileHandler.deserialize(fileName);

        TextField serverHostTF = new TextField("Server Host");
        serverHostTF.setHelperText("Enter the name of the server host you wish to monitor.");
        serverHostTF.setWidth("350px");
        serverHostTF.setValue(config.getHost());

        IntegerField portField = new IntegerField("Port Number");
        portField.setHelperText("Enter the port number");
        portField.setMin(1);
        portField.setMax(65535);
        portField.setWidth("350px");
        portField.setValue(config.getPort());

        IntegerField timerField = new IntegerField("Refresh Timer");
        timerField.setHelperText("This value is in seconds.");
        timerField.setMin(10);
        timerField.setMax(600);
        timerField.setWidth("350px");
        timerField.setValue(config.getRefreshTimer());

        Button cancelButton = new Button("Cancel", buttonClickEvent -> {
            this.close();
        });

        Button saveButton = new Button("Save");
        saveButton.addClickListener(buttonClickEvent -> {
            try {
                if (serverHostTF.getValue().isEmpty() || portField.getValue() == null
                        || timerField.getValue() == null || portField.getValue() <= 0
                        || portField.getValue() > 65535 || timerField.getValue() < 10 || timerField.getValue() > 600) {
                    notification.setText("Save failed - Invalid or empty Input.");
                    notification.open();
                }
                config.setHost(serverHostTF.getValue().trim());
                config.setPort(portField.getValue());
                config.setRefreshTimer(timerField.getValue());

                fileHandler.writeConfigToFile(fileName, config);
                saveState = true;
                this.close();
            } catch (Exception ex) {
                log.error("Config Editor Dialog produced error, when trying to save config", ex);
            }
        });



        Button deleteConfigButton = new Button("Delete");
        deleteConfigButton.addClickListener(buttonClickEvent -> {
            try {
                ConfirmDeleteDialog confirmDialog = new ConfirmDeleteDialog(fileName);
                confirmDialog.open();
                confirmDialog.addDetachListener(detachEvent -> {
                    if (confirmDialog.isDeleteState()) {
                        saveState = true;
                        fileHandler.deleteFile(fileName);
                        this.close();
                    }
                });
            } catch (NullPointerException ex) {
                log.error("Config Editor Dialog produced error, when trying to delete config", ex);
            }
        });
        this.setCloseOnOutsideClick(false);

        HorizontalLayout buttonLayout = new HorizontalLayout(cancelButton, saveButton, deleteConfigButton);
        VerticalLayout dialogContentLayout = new VerticalLayout(serverHostTF, portField, timerField, buttonLayout);

        this.add(dialogContentLayout);
    }

}
