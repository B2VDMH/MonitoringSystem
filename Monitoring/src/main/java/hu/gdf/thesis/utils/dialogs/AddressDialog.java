package hu.gdf.thesis.utils.dialogs;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import hu.gdf.thesis.backend.FileHandler;
import hu.gdf.thesis.model.config.*;
import hu.gdf.thesis.utils.notifications.CustomNotification;
import org.springframework.beans.factory.annotation.Autowired;

public class AddressDialog extends Dialog {
    private Address address = new Address();
    private boolean saveState = false;
    public AddressDialog(String fileName, Config config, Category category, Entry entry, RestField restField, Operation operation, @Autowired FileHandler fileHandler) {

        TextField addressTF = new TextField("Add an e-mail Address");
        addressTF.setHelperText("Alert e-mails will be sent to this address");
        Button cancelButton = new Button("Cancel" , e -> this.close());

        Button saveButton = new Button("Save to Config");
        saveButton.addClickListener(buttonClickEvent -> {
            if(addressTF.getValue().isEmpty()) {
                CustomNotification errorNotification = new CustomNotification("Save failed - Invalid or empty Input.");
                errorNotification.open();
            } else {
                address.setAddress(addressTF.getValue());
                operation.getAddresses().add(address);

                int operationIndex = restField.getOperation().indexOf(operation);
                restField.getOperation().set(operationIndex, operation);

                int restFieldIndex = entry.getRestFields().indexOf(restField);
                entry.getRestFields().set(restFieldIndex, restField);

                int entryIndex = category.getEntries().indexOf(entry);
                category.getEntries().set(entryIndex,entry);

                int categoryIndex = config.getServer().getCategories().indexOf(category);
                config.getServer().getCategories().set(categoryIndex, category);

                fileHandler.writeConfigToFile(fileName, fileHandler.serializeJsonConfig(config));
                saveState=true;
                this.close();
            }
        });

        HorizontalLayout buttonLayout = new HorizontalLayout(cancelButton, saveButton);
        VerticalLayout dialogLayout = new VerticalLayout(addressTF, buttonLayout);
        this.add(dialogLayout);
    }

    public Address getAddress() {
        return address;
    }

    public void setAddress(Address address) {
        this.address = address;
    }

    public boolean isSaveState() {
        return saveState;
    }

}
