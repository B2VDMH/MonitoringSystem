package hu.gdf.thesis.utils.dialogs;


import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import hu.gdf.thesis.backend.FileHandler;
import hu.gdf.thesis.model.Category;
import hu.gdf.thesis.model.Config;
import hu.gdf.thesis.utils.notifications.CustomNotification;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class EditCategoryDialog extends Dialog {

    private static CustomNotification notification = new CustomNotification();

    @Getter
    @Setter
    private boolean deleteState = false;

    public EditCategoryDialog(String fileName, Config config, Category category, FileHandler fileHandler) {
        this.getElement().setAttribute("aria-label", "Add new Category");

        TextField categoryTypeField = new TextField("Category Type");
        categoryTypeField.setWidth("300px");
        categoryTypeField.setValue(category.getType());
        categoryTypeField.setHelperText("Add a monitoring category type (e.g Server Health).");

        Button cancelButton = new Button("Cancel" , e -> this.close());

        Button saveButton = new Button("Save to Config");
        saveButton.addClickListener( buttonClickEvent -> {
            try {
                if (categoryTypeField.getValue().isEmpty()) {
                    notification.setText("Save failed - Invalid or empty Input.");
                    notification.open();
                }
                category.setType(categoryTypeField.getValue());

                fileHandler.modifyCategory(fileName, config, category, true);
                this.close();

            } catch (NullPointerException ex) {
                log.error("Edit Category Dialog produced error, when trying to save", ex);
            }

        });
        Button deleteButton = new Button("Delete Category");
        deleteButton.addClickListener(buttonClickEvent -> {
            try {
                ConfirmDeleteDialog confirmDialog = new ConfirmDeleteDialog(category.getType());
                confirmDialog.open();
                confirmDialog.addDetachListener(detachEvent -> {
                    if(confirmDialog.isDeleteState()) {
                        setDeleteState(true);
                        fileHandler.modifyCategory(fileName, config, category, false);
                        this.close();
                    }
                });

            } catch (NullPointerException ex) {
                log.error("Edit Category Dialog produced error, when trying to delete", ex);
            }
        });

        this.setCloseOnOutsideClick(false);

        HorizontalLayout buttonLayout = new HorizontalLayout(cancelButton, saveButton, deleteButton);
        VerticalLayout dialogContentLayout = new VerticalLayout(categoryTypeField, buttonLayout);

        this.add(dialogContentLayout);

    }

}
