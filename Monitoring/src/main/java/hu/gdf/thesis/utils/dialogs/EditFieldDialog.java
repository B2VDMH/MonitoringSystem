package hu.gdf.thesis.utils.dialogs;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import hu.gdf.thesis.backend.FileHandler;
import hu.gdf.thesis.model.Category;
import hu.gdf.thesis.model.Config;
import hu.gdf.thesis.model.Endpoint;
import hu.gdf.thesis.model.Field;
import hu.gdf.thesis.utils.notifications.CustomNotification;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class EditFieldDialog extends Dialog {
    private static CustomNotification notification = new CustomNotification();
    @Getter
    @Setter
    private boolean deleteState = false;
    public EditFieldDialog(String fileName, Config config, Category category, Endpoint endpoint, Field field, FileHandler fileHandler) {

        TextField restFieldPathTF = new TextField("REST Field Path");
        restFieldPathTF.setHelperText("Please add the full path using JsonPath syntax (e.g. $.fieldArray[0].field)");
        restFieldPathTF.setValue(field.getFieldPath());
        restFieldPathTF.setWidth("350px");

        Button cancelButton = new Button("Cancel" , e -> this.close());

        Button saveButton = new Button("Save to Config");
        saveButton.addClickListener( buttonClickEvent -> {
            try {
                if(restFieldPathTF.getValue().isEmpty()){
                    notification.setText("Save failed - Invalid or empty Input.");
                    notification.open();
                } else {
                    field.setFieldPath(restFieldPathTF.getValue().trim());
                    fileHandler.modifyField(fileName, config, category, endpoint, field, true);

                    this.close();
                }

            }catch (NullPointerException ex) {
                log.error("Edit Rest Field Dialog produced error, when trying to save", ex);
            }
        });

        Button deleteButton = new Button("Delete Rest Field");
        deleteButton.addClickListener(buttonClickEvent -> {
            try {
                ConfirmDeleteDialog confirmDialog = new ConfirmDeleteDialog(field.getFieldPath());
                confirmDialog.open();
                confirmDialog.addDetachListener(detachEvent -> {
                    if(confirmDialog.isDeleteState()) {
                        deleteState = true;
                        fileHandler.modifyField(fileName, config, category, endpoint, field, false);
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
