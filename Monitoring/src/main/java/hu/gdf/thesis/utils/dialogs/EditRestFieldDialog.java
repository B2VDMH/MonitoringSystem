package hu.gdf.thesis.utils.dialogs;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import hu.gdf.thesis.backend.FileHandler;
import hu.gdf.thesis.model.config.Category;
import hu.gdf.thesis.model.config.Config;
import hu.gdf.thesis.model.config.Entry;
import hu.gdf.thesis.model.config.RestField;
import hu.gdf.thesis.utils.notifications.CustomNotification;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

@Slf4j
public class EditRestFieldDialog extends Dialog {
    @Getter
    private RestField restField = new RestField();
    @Getter
    private boolean deleteState = false;

    public EditRestFieldDialog(String fileName, Config config, Category category, Entry entry, RestField restField, @Autowired FileHandler fileHandler) {

        TextField restFieldPathTF = new TextField("REST Field Path");
        restFieldPathTF.setHelperText("Please add the full path using JsonPath syntax (e.g. $.fieldArray[0].field)");
        restFieldPathTF.setValue(restField.getFieldPath());
        restFieldPathTF.setWidth("350px");

        Button cancelButton = new Button("Cancel" , e -> this.close());

        Button saveButton = new Button("Save to Config");
        saveButton.addClickListener( buttonClickEvent -> {
            try {
                if(restFieldPathTF.getValue().isEmpty()){

                    CustomNotification errorNotification = new CustomNotification("Save failed - Invalid or empty Input.");
                    errorNotification.open();

                } else {
                    restField.setFieldPath(restFieldPathTF.getValue().trim());
                    this.restField = restField;
                    fileHandler.deleteOrEditRestField(fileName, config, category, entry, this.restField, true);

                    this.close();
                }

            }catch (NullPointerException ex) {
                log.error("Edit Rest Field Dialog produced error, when trying to save", ex);
            }
        });

        Button deleteButton = new Button("Delete Rest Field");
        deleteButton.addClickListener(buttonClickEvent -> {
            try {
                ConfirmDialog confirmDialog = new ConfirmDialog(restField.getFieldPath());
                confirmDialog.open();
                confirmDialog.addDetachListener(detachEvent -> {
                    if(confirmDialog.isDeleteState()) {
                        deleteState = true;
                        fileHandler.deleteOrEditRestField(fileName, config, category, entry, restField, false);
                        this.close();
                    }
                });

            } catch (NullPointerException ex) {
                log.error("Edit Operation Dialog produced error, when trying to delete", ex);
            }
        });
        this.setCloseOnOutsideClick(false);
        HorizontalLayout buttonLayout = new HorizontalLayout(cancelButton, saveButton, deleteButton);
        VerticalLayout dialogContentLayout = new VerticalLayout(restFieldPathTF, buttonLayout);
        this.add(dialogContentLayout);
    }
}
