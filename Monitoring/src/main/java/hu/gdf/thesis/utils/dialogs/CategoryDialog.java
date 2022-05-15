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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;


public class CategoryDialog extends Dialog {

    private Category category = new Category();
    private boolean saveState = false;
    private static final Logger LOGGER = LoggerFactory.getLogger(CategoryDialog.class);

    public CategoryDialog(String fileName, Config config, @Autowired FileHandler fileHandler) {

        this.getElement().setAttribute("aria-label", "Add new Category");

        TextField categoryTypeField = new TextField("Category Type");
        categoryTypeField.setWidth("300px");
        categoryTypeField.setHelperText("Add a monitoring category type (e.g Server Health).");

        Button cancelButton = new Button("Cancel" , e -> this.close());

        Button saveButton = new Button("Save to Config");
        saveButton.addClickListener( buttonClickEvent -> {
            try {
                if (categoryTypeField.getValue().isEmpty()) {
                    CustomNotification errorNotification = new CustomNotification("Save failed - Invalid or empty Input.");
                    errorNotification.open();

                } else {
                    category.setType(categoryTypeField.getValue());
                    config.getServer().getCategories().add(category);
                    fileHandler.writeConfigToFile(fileName, fileHandler.serializeJsonConfig(config));
                    saveState = true;
                    this.close();
                }
            } catch (Exception ex) {
                LOGGER.error("Category Dialog produced error, when trying to save", ex);
            }

        });
        this.setCloseOnOutsideClick(false);
        HorizontalLayout buttonLayout = new HorizontalLayout(cancelButton, saveButton);
        VerticalLayout dialogContentLayout = new VerticalLayout(categoryTypeField, buttonLayout);
        this.add(dialogContentLayout);

    }
    public Category getCategory() {
        return category;
    }
    public boolean isSaveState() {
        return saveState;
    }
}
