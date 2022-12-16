package hu.gdf.thesis.utils.dialogs;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.component.textfield.TextField;
import hu.gdf.thesis.backend.FileHandler;
import hu.gdf.thesis.model.*;
import hu.gdf.thesis.utils.notifications.CustomNotification;
import hu.gdf.thesis.utils.selects.ActionSelect;
import hu.gdf.thesis.utils.selects.OperatorSelect;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;


@Slf4j
public class OperationDialog extends Dialog {

    private static CustomNotification notification = new CustomNotification();

    @Getter
    private Operation operation = new Operation();
    @Getter
    @Setter
    private boolean saveState = false;

    public OperationDialog(String fileName, Config config, Category category, Endpoint endpoint, Field field, FileHandler fileHandler) {

        OperatorSelect operatorSelect = new OperatorSelect();

        TextField valueTF = new TextField("Value");
        valueTF.setHelperText("Specify expected value for condition evaluation.");
        valueTF.setWidth("300px");

        ActionSelect actionSelect = new ActionSelect();

        Select alertSelect = new Select(true, false);
        alertSelect.setWidth("300px");
        alertSelect.setLabel("Set Alert");

        Button cancelButton = new Button("Cancel" , e -> this.close());
        Button saveButton = new Button("Save to Config");

        operatorSelect.addValueChangeListener(o -> {
            if (operatorSelect.getValue().toString().equals("true") || operatorSelect.getValue().toString().equals("false")) {
                valueTF.setValue("");
                valueTF.setEnabled(false);
            } else {
                valueTF.setEnabled(true);
            }
        });

        saveButton.addClickListener( buttonClickEvent -> {
            try {
                if(operatorSelect.isEmpty() || actionSelect.isEmpty()) {
                    notification.setText("Save failed - Invalid or empty Input.");
                    notification.open();
                } else {
                    operation.setOperator(String.valueOf(operatorSelect.getValue()));
                    operation.setValue(valueTF.getValue());
                    operation.setAction(String.valueOf(actionSelect.getValue()));
                    operation.setAlert((Boolean) alertSelect.getValue());

                    fileHandler.addOperation(fileName, config, category, endpoint, field, operation);
                    setSaveState(true);
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
