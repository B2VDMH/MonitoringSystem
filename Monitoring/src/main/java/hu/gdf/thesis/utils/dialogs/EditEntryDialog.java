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
public class EditEntryDialog extends Dialog {
    @Getter
    private Entry entry = new Entry();
    @Getter
    private boolean deleteState = false;

    public EditEntryDialog(String fileName, Config config, Category category, Entry entry, @Autowired FileHandler fileHandler) {

        TextField restURLFieldTF = new TextField("REST URL");
        restURLFieldTF.setHelperText("Please add the REST URL starting with a \"/\" (forward slash).");
        restURLFieldTF.setWidth("450px");
        restURLFieldTF.setValue(entry.getRestURL());

        Select alertSelect = new Select(true, false);
        alertSelect.setWidth("300px");
        alertSelect.setLabel("Set Alert");
        alertSelect.setValue(entry.isAlert());

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
                    this.entry = entry;
                    fileHandler.deleteOrEditEntry(fileName, config, category, this.entry, "edit");
                    this.close();
                }
            } catch (Exception ex) {
                log.error("Edit Entry Dialog produced error, when trying to save", ex);
            }
        });
        Button deleteButton = new Button("Delete Entry");
        deleteButton.addClickListener(buttonClickEvent -> {
            try {
                ConfirmDialog confirmDialog = new ConfirmDialog(entry.getRestURL());
                confirmDialog.open();
                confirmDialog.addDetachListener(detachEvent -> {
                    if(confirmDialog.isDeleteState()) {
                        deleteState = true;
                        this.close();
                    }
                });

            } catch (Exception ex) {
                log.error("Edit Entry Dialog produced error, when trying to delete", ex);
            }
        });

        this.setCloseOnOutsideClick(false);

        HorizontalLayout buttonLayout = new HorizontalLayout(cancelButton, saveButton, deleteButton);
        VerticalLayout dialogContentLayout = new VerticalLayout(restURLFieldTF, alertSelect, buttonLayout);

        this.add(dialogContentLayout);
    }
}
