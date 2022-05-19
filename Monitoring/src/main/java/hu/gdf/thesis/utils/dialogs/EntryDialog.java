package hu.gdf.thesis.utils.dialogs;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.component.textfield.TextField;
import hu.gdf.thesis.backend.FileHandler;
import hu.gdf.thesis.model.config.Category;
import hu.gdf.thesis.model.config.Config;
import hu.gdf.thesis.model.config.Entry;
import hu.gdf.thesis.utils.notifications.CustomNotification;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

@Slf4j
public class EntryDialog extends Dialog {
    @Getter
    private Entry entry = new Entry();
    @Getter
    private boolean saveState = false;

    public EntryDialog(String fileName, Config config, Category category, @Autowired FileHandler fileHandler) {

        TextField restURLFieldTF = new TextField("REST URL");
        restURLFieldTF.setHelperText("Please add the REST URL starting with a \"/\" (forward slash).");
        restURLFieldTF.setWidth("450px");

        Select alertSelect = new Select(true, false);
        alertSelect.setWidth("300px");
        alertSelect.setLabel("Set Alert");

        Button cancelButton = new Button("Cancel", e -> this.close());

        Button saveButton = new Button("Save to Config");
        saveButton.addClickListener(buttonClickEvent -> {
            try {
                if (restURLFieldTF.getValue().isEmpty() || alertSelect.isEmpty()) {
                    CustomNotification errorNotification = new CustomNotification("Save failed - Invalid or empty Input.");
                    errorNotification.open();
                } else {
                    entry.setRestURL(restURLFieldTF.getValue().trim());
                    entry.setAlert((Boolean) alertSelect.getValue());
                    category.getEntries().add(entry);
                    int categoryIndex = config.getServer().getCategories().indexOf(category);
                    config.getServer().getCategories().set(categoryIndex, category);
                    fileHandler.writeConfigToFile(fileName, fileHandler.serializeJsonConfig(config));
                    saveState = true;
                    this.close();
                }
            } catch (NullPointerException ex) {
                log.error("Entry Dialog produced error, when trying to save", ex);
            }
        });
        this.setCloseOnOutsideClick(false);

        HorizontalLayout buttonLayout = new HorizontalLayout(cancelButton, saveButton);
        VerticalLayout dialogContentLayout = new VerticalLayout(restURLFieldTF, alertSelect, buttonLayout);

        this.add(dialogContentLayout);
    }

}
