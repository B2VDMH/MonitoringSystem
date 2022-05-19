package hu.gdf.thesis.utils.dialogs;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Slf4j
public class ConfirmDialog extends Dialog {

    @Getter
    private boolean deleteState;

    public ConfirmDialog(String selected) {

        Label questionLabel = new Label("Delete: " + selected + "?");
        Button cancelButton = new Button("Cancel", buttonClickEvent -> this.close());
        Button confirmButton = new Button("Confirm");
        confirmButton.addClickListener(buttonClickEvent -> {
            try {
                deleteState=true;
                this.close();
            } catch (Exception ex) {
                log.error("Confirmation Dialog produced error, when trying to delete", ex);
            }
        });
        this.setCloseOnOutsideClick(false);

        HorizontalLayout buttonLayout = new HorizontalLayout(cancelButton, confirmButton);

        VerticalLayout dialogLayout = new VerticalLayout(questionLabel, buttonLayout);

        this.add(dialogLayout);
    }
}
