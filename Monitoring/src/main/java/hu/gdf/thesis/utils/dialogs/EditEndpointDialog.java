package hu.gdf.thesis.utils.dialogs;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.component.textfield.TextField;
import hu.gdf.thesis.backend.FileHandler;
import hu.gdf.thesis.model.Category;
import hu.gdf.thesis.model.Config;
import hu.gdf.thesis.model.Endpoint;
import hu.gdf.thesis.utils.notifications.CustomNotification;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class EditEndpointDialog extends Dialog {

    private static CustomNotification notification = new CustomNotification();

    @Getter
    @Setter
    private boolean deleteState = false;

    public EditEndpointDialog(String fileName, Config config, Category category, Endpoint endpoint, FileHandler fileHandler) {

        TextField restURLFieldTF = new TextField("REST URL");
        restURLFieldTF.setHelperText("Please add the REST URL starting with a \"/\" (forward slash).");
        restURLFieldTF.setWidth("450px");
        restURLFieldTF.setValue(endpoint.getRestURL());

        Select alertSelect = new Select(true, false);
        alertSelect.setWidth("300px");
        alertSelect.setLabel("Set Alert");
        alertSelect.setValue(endpoint.isAlert());

        Button cancelButton = new Button("Cancel", e -> this.close());

        Button saveButton = new Button("Save to Config");
        saveButton.addClickListener(buttonClickEvent -> {
            try {
                if (restURLFieldTF.getValue().isEmpty() || !restURLFieldTF.getValue().startsWith("/") || alertSelect.isEmpty()) {
                    notification.setText("Save failed - Invalid or empty Input.");
                    notification.open();
                } else {
                    endpoint.setRestURL(restURLFieldTF.getValue().trim());
                    endpoint.setAlert((Boolean) alertSelect.getValue());
                    fileHandler.modifyEndpoint(fileName, config, category, endpoint, true);
                    this.close();
                }
            } catch (NullPointerException ex) {
                log.error("Edit Entry Dialog produced error, when trying to save", ex);
            }
        });
        Button deleteButton = new Button("Delete Entry");
        deleteButton.addClickListener(buttonClickEvent -> {
            try {
                ConfirmDeleteDialog confirmDialog = new ConfirmDeleteDialog(endpoint.getRestURL());
                confirmDialog.open();
                confirmDialog.addDetachListener(detachEvent -> {
                    if(confirmDialog.isDeleteState()) {
                        deleteState = true;
                        fileHandler.modifyEndpoint(fileName, config, category, endpoint, false);
                        this.close();
                    }
                });

            } catch (NullPointerException ex) {
                log.error("Edit Entry Dialog produced error, when trying to delete", ex);
            }
        });

        this.setCloseOnOutsideClick(false);

        HorizontalLayout buttonLayout = new HorizontalLayout(cancelButton, saveButton, deleteButton);
        VerticalLayout dialogContentLayout = new VerticalLayout(restURLFieldTF, alertSelect, buttonLayout);

        this.add(dialogContentLayout);
    }
}
