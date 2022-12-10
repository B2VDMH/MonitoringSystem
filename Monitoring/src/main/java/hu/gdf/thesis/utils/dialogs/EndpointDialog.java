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
public class EndpointDialog extends Dialog {

    private static CustomNotification notification = new CustomNotification();

    @Getter
    private Endpoint entry = new Endpoint();
    @Getter
    @Setter
    private boolean saveState = false;

    public EndpointDialog(String fileName, Config config, Category category, FileHandler fileHandler) {

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
                if (restURLFieldTF.getValue().isEmpty() || !restURLFieldTF.getValue().startsWith("/") || alertSelect.isEmpty()) {
                    notification.setText("Save failed - Invalid or empty Input.");
                    notification.open();
                } else {
                    entry.setRestURL(restURLFieldTF.getValue().trim());
                    entry.setAlert((Boolean) alertSelect.getValue());

                    fileHandler.addEndpoint(fileName, config, category, entry);
                    setSaveState(true);
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
