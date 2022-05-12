package hu.gdf.thesis.utils.dialogs;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.component.textfield.TextField;
import hu.gdf.thesis.backend.FileHandler;
import hu.gdf.thesis.model.config.*;
import hu.gdf.thesis.utils.notifications.CustomNotification;
import hu.gdf.thesis.utils.selects.ActionSelect;
import hu.gdf.thesis.utils.selects.OperatorSelect;
import org.springframework.beans.factory.annotation.Autowired;

public class OperationDialog extends Dialog {

    private Operation operation = new Operation();
    private boolean saveState = false;

    public OperationDialog(String fileName, Config config, Category category, Entry entry, RestField restField, @Autowired FileHandler fileHandler) {

        OperatorSelect operatorSelect = new OperatorSelect();
        operatorSelect.setLabel("Operator Select");

        TextField valueTF = new TextField("Value");

        Select alertSelect = new Select(true, false);
        alertSelect.setLabel("Set Alert");

        ActionSelect actionSelect = new ActionSelect();
        actionSelect.setLabel("Action Select");

        Button cancelButton = new Button("Cancel" , e -> this.close());
        Button saveButton = new Button("Save to Config");
        saveButton.addClickListener( buttonClickEvent -> {
            if(operatorSelect.isEmpty() || valueTF.getValue().isEmpty() || actionSelect.isEmpty()) {
                CustomNotification errorNotification = new CustomNotification("Save failed - Invalid or empty Input.");
                errorNotification.open();
            } else {
                operation.setOperator(String.valueOf(operatorSelect.getValue()));
                operation.setValue(valueTF.getValue());
                operation.setAction(String.valueOf(actionSelect.getValue()));
                operation.setAlert((Boolean) alertSelect.getValue());

                restField.getOperation().add(operation);

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
        VerticalLayout dialogContentLayout = new VerticalLayout(operatorSelect, valueTF, actionSelect, alertSelect, buttonLayout);
        this.add(dialogContentLayout);
    }

    public Operation getOperation() {
        return operation;
    }

    public boolean isSaveState() {
        return saveState;
    }
}
