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
public class RestFieldDialog extends Dialog {

    @Getter
    private RestField restField = new RestField();
    @Getter
    private boolean saveState = false;

    public RestFieldDialog(String fileName, Config config, Category category, Entry entry, @Autowired FileHandler fileHandler) {

        TextField restFieldPathTF = new TextField("REST Field Path");
        restFieldPathTF.setHelperText("Please add the full path using JsonPath syntax (e.g. $.fieldArray[0].field)");
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
                    entry.getRestFields().add(restField);

                    int entryIndex = category.getEntries().indexOf(entry);
                    category.getEntries().set(entryIndex,entry);

                    int categoryIndex = config.getServer().getCategories().indexOf(category);
                    config.getServer().getCategories().set(categoryIndex, category);

                    fileHandler.writeConfigToFile(fileName, fileHandler.serializeJsonConfig(config));
                    saveState=true;
                    this.close();
                }
            }catch (NullPointerException ex) {
                log.error("Rest Field Dialog produced error, when trying to save", ex);
            }

        });
        this.setCloseOnOutsideClick(false);
        HorizontalLayout buttonLayout = new HorizontalLayout(cancelButton, saveButton);
        VerticalLayout dialogContentLayout = new VerticalLayout(restFieldPathTF, buttonLayout);
        this.add(dialogContentLayout);
    }

}
