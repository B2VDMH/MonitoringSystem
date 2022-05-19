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
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;


@Slf4j
public class OperationDialog extends Dialog {

    @Getter
    private Operation operation = new Operation();
    @Getter
    private boolean saveState = false;

    public OperationDialog(String fileName, Config config, Category category, Entry entry, RestField restField, @Autowired FileHandler fileHandler) {

        OperatorSelect operatorSelect = new OperatorSelect();
        operatorSelect.setLabel("Operator Select");
        operatorSelect.setHelperText("Select a condition operator.");

        TextField valueTF = new TextField("Value");
        valueTF.setHelperText("Specify expected value for condition evaluation");
        valueTF.setWidth("300px");

        Select alertSelect = new Select(true, false);
        alertSelect.setWidth("300px");
        alertSelect.setLabel("Set Alert");

        ActionSelect actionSelect = new ActionSelect();
        actionSelect.setHelperText("Select an action to be executed if condition is met. In this case, grid rows will be colored");
        actionSelect.setLabel("Action Select");

        Button cancelButton = new Button("Cancel" , e -> this.close());
        Button saveButton = new Button("Save to Config");
        saveButton.addClickListener( buttonClickEvent -> {
            try {
                if(operatorSelect.isEmpty() || valueTF.getValue().isEmpty() || actionSelect.isEmpty()) {
                    CustomNotification errorNotification = new CustomNotification("Save failed - Invalid or empty Input.");
                    errorNotification.open();
                } else {
                    operation.setOperator(String.valueOf(operatorSelect.getValue()));
                    operation.setValue(valueTF.getValue());
                    operation.setAction(String.valueOf(actionSelect.getValue()));
                    operation.setAlert((Boolean) alertSelect.getValue());

                    restField.getOperations().add(operation);

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
            } catch (NullPointerException ex){
                log.error("Operation Dialog produced error, when trying to save", ex);
            }

        });
        this.setCloseOnOutsideClick(false);
        HorizontalLayout buttonLayout = new HorizontalLayout(cancelButton, saveButton);
        VerticalLayout dialogContentLayout = new VerticalLayout(operatorSelect, valueTF, actionSelect, alertSelect, buttonLayout);
        this.add(dialogContentLayout);
    }

}
