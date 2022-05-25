package hu.gdf.thesis.utils.dialogs;


import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import hu.gdf.thesis.backend.FileHandler;
import hu.gdf.thesis.model.config.Category;
import hu.gdf.thesis.model.config.Config;
import hu.gdf.thesis.utils.notifications.CustomNotification;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

@Slf4j
public class EditCategoryDialog extends Dialog {

    @Getter
    private Category category = new Category();
    @Getter
    private boolean deleteState = false;

    public EditCategoryDialog(String fileName, Config config, Category category, @Autowired FileHandler fileHandler) {
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

                    CustomNotification errorNotification = new CustomNotification("Save failed - Invalid or empty Input.");
                    errorNotification.open();

                }
                category.setType(categoryTypeField.getValue());
                this.category = category;

                fileHandler.deleteOrEditCategory(fileName, config, this.category, true);
                this.close();

            } catch (NullPointerException ex) {
                log.error("Edit Category Dialog produced error, when trying to save", ex);
            }

        });
        Button deleteButton = new Button("Delete Category");
        deleteButton.addClickListener(buttonClickEvent -> {
            try {
                ConfirmDialog confirmDialog = new ConfirmDialog(category.getType());
                confirmDialog.open();
                confirmDialog.addDetachListener(detachEvent -> {
                    if(confirmDialog.isDeleteState()) {
                        deleteState = true;
                        fileHandler.deleteOrEditCategory(fileName, config, category, false);
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
