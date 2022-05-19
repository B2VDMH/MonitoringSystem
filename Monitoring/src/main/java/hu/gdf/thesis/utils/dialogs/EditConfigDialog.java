package hu.gdf.thesis.utils.dialogs;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.select.data.SelectListDataView;
import com.vaadin.flow.component.textfield.EmailField;
import com.vaadin.flow.component.textfield.IntegerField;
import com.vaadin.flow.component.textfield.TextField;
import hu.gdf.thesis.backend.FileHandler;
import hu.gdf.thesis.model.config.Address;
import hu.gdf.thesis.model.config.Category;
import hu.gdf.thesis.model.config.Config;
import hu.gdf.thesis.utils.notifications.CustomNotification;
import hu.gdf.thesis.utils.selects.CustomSelect;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

@Slf4j
public class EditConfigDialog extends Dialog {

    @Getter
    private boolean saveState;

    private static final Logger LOGGER = LoggerFactory.getLogger(EditConfigDialog.class);

    public EditConfigDialog(String fileName, @Autowired FileHandler fileHandler) {
        Config config = fileHandler.deserializeJsonConfig(fileHandler.readFromFile(fileName), Config.class);

        TextField serverHostTF = new TextField("Server Host");
        serverHostTF.setHelperText("Enter the name of the server host you wish to monitor.");
        serverHostTF.setWidth("350px");
        serverHostTF.setValue(config.getServer().getHost());

        IntegerField portField = new IntegerField("Port Number");
        portField.setHelperText("Enter the port number");
        portField.setMin(1);
        portField.setMax(65535);
        portField.setWidth("350px");
        portField.setValue(config.getServer().getPort());

        IntegerField timerField = new IntegerField("Refresh Timer");
        timerField.setHelperText("This value is in seconds.");
        timerField.setMin(10);
        timerField.setMax(600);
        timerField.setWidth("350px");
        timerField.setValue(config.getServer().getRefreshTimer());

        CustomSelect addressSelect = new CustomSelect("Alert Addresses");
        addressSelect.setWidth("350px");
        addressSelect.setHelperText("Already declared e-mail addresses for alerting");
        SelectListDataView<Address> addressDataView = (SelectListDataView<Address>) addressSelect.setItems(config.getServer().getAddresses());

        EmailField addressEmailField = new EmailField("Alert Address");
        addressEmailField.setHelperText("Enter an e-mail address you wish to send alerts to.");
        addressEmailField.setWidth("350px");
        addressEmailField.setErrorMessage("Please enter a valid email address");
        addressEmailField.addValueChangeListener(emailFieldStringComponentValueChangeEvent -> {
            addressSelect.setEnabled(addressEmailField.isEmpty());
        });


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

                config.getServer().setHost(serverHostTF.getValue().trim());
                config.getServer().setPort(portField.getValue());
                config.getServer().setRefreshTimer(timerField.getValue());
                if(!addressEmailField.getValue().isEmpty()) {
                    Address address = new Address();
                    address.setAddress(addressEmailField.getValue());
                    config.getServer().getAddresses().add(address);
                }
                fileHandler.writeConfigToFile(fileName, fileHandler.serializeJsonConfig(config));
                saveState = true;
                this.close();
            } catch (Exception ex) {
                LOGGER.error("Config Editor Dialog produced error, when trying to save config", ex);
            }
        });

        Button deleteAddressButton = new Button("Delete Address");
        deleteAddressButton.addClickListener(buttonClickEvent -> {
            try {
                if(addressSelect.isEmpty()) {
                    CustomNotification errorNotification = new CustomNotification("Please select an e-mail address you want to delete.");
                    errorNotification.open();
                } else if (!addressSelect.isEmpty()) {
                    Address address = (Address) addressSelect.getValue();
                    ConfirmDialog confirmDialog = new ConfirmDialog(address.getAddress());
                    confirmDialog.open();
                    confirmDialog.addDetachListener(detachEvent -> {
                        if (confirmDialog.isDeleteState()) {
                            addressDataView.removeItem(address);
                            fileHandler.deleteAddress(fileName, config, address);
                        }
                    });
                }


            } catch (Exception ex) {
                LOGGER.error("Config Editor Dialog produced error, when trying to delete address.", ex);
            }
        });

        Button deleteConfigButton = new Button("Delete");
        deleteConfigButton.addClickListener(buttonClickEvent -> {
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

        HorizontalLayout buttonLayout = new HorizontalLayout(cancelButton, saveButton, deleteConfigButton);
        VerticalLayout dialogContentLayout = new VerticalLayout(serverHostTF, portField, timerField, addressEmailField, addressSelect, deleteAddressButton, buttonLayout);

        this.add(dialogContentLayout);
    }

}
