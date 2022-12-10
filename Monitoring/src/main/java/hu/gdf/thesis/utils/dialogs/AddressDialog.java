package hu.gdf.thesis.utils.dialogs;


import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.select.data.SelectListDataView;
import com.vaadin.flow.component.textfield.EmailField;
import hu.gdf.thesis.backend.FileHandler;
import hu.gdf.thesis.model.Address;
import hu.gdf.thesis.model.Config;
import hu.gdf.thesis.utils.notifications.CustomNotification;
import hu.gdf.thesis.utils.selects.CustomSelect;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;

public class AddressDialog extends Dialog {

    private static CustomNotification notification = new CustomNotification();

    private Address address = new Address();

    private boolean saveState;

    public AddressDialog(String fileName, Config config, FileHandler fileHandler) {

        CustomSelect addressSelect = new CustomSelect("Alert Addresses");
        addressSelect.setWidth("350px");
        addressSelect.setEmptySelectionAllowed(false);
        addressSelect.setHelperText("Already declared e-mail addresses for alerting");
        SelectListDataView<Address> addressDataView =
                (SelectListDataView<Address>) addressSelect.
                setItems(new ArrayList<>(config.getAddresses()));

        EmailField addressField = new EmailField("Alert Address");
        addressField.setHelperText("Enter an e-mail address you wish to send alerts to.");
        addressField.setWidth("350px");
        addressField.setErrorMessage("Please enter a valid email address");

        Button cancelButton = new Button("Close");
        cancelButton.addClickListener(buttonClickEvent -> this.close());
        Button saveButton = new Button("Save Address");
        saveButton.addClickListener(buttonClickEvent -> {
            if (addressField.isEmpty() || addressField.getValue()==null || addressField.isInvalid()) {
                notification.setText("Please write a valid e-mail address.");
                notification.open();
            } else {
                address.setAddress(addressField.getValue());
                addressDataView.addItem(address);
                fileHandler.addAddress(fileName, config, address);
                addressField.clear();

                notification.setText("Successfully saved : " + address.getAddress());
                notification.open();
            }
        });
        Button deleteButton = new Button("Delete Address");
        deleteButton.addClickListener(buttonClickEvent -> {
            if(addressSelect.isEmpty() || addressSelect.getValue()==null) {
                notification.setText("Please select an e-mail address to delete.");
                notification.open();
            } else {
                address.setAddress(String.valueOf(addressSelect.getValue()));
                addressDataView.removeItem(address);

                notification.setText("Successfully deleted : " + address.getAddress());
                notification.open();

                fileHandler.deleteAddress(fileName, config, address);

            }
        });
        VerticalLayout dialogContentLayout = new VerticalLayout(addressSelect, addressField);
        HorizontalLayout buttonLayout = new HorizontalLayout(saveButton, deleteButton, cancelButton);
        this.add(dialogContentLayout,buttonLayout);
    }
}
