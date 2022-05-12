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
import org.springframework.beans.factory.annotation.Autowired;

public class RestFieldDialog extends Dialog {
    private RestField restField = new RestField();
    private boolean saveState = false;

    public RestFieldDialog(String fileName, Config config, Category category, Entry entry, @Autowired FileHandler fileHandler) {

        TextField restFieldPathTF =  new TextField("REST Field Path");
        Button cancelButton = new Button("Cancel" , e -> this.close());

        Button saveButton = new Button("Save to Config");
        saveButton.addClickListener( buttonClickEvent -> {
            if(restFieldPathTF.getValue().isEmpty()){
                CustomNotification errorNotification = new CustomNotification("Save failed - Invalid or empty Input.");
                errorNotification.open();
            } else {
                restField.setFieldPath(restFieldPathTF.getValue());
                entry.getRestFields().add(restField);

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
        VerticalLayout dialogContentLayout = new VerticalLayout(restFieldPathTF, buttonLayout);
        this.add(dialogContentLayout);
    }

    public RestField getRestField() {
        return restField;
    }

    public boolean isSaveState() {
        return saveState;
    }
}
