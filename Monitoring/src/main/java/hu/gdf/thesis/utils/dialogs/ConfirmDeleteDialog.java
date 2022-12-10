package hu.gdf.thesis.utils.dialogs;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import hu.gdf.thesis.utils.notifications.CustomNotification;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ConfirmDeleteDialog extends Dialog {

    @Getter
    @Setter
    private boolean deleteState;

    public ConfirmDeleteDialog(String selected) {

        Label questionLabel = new Label("Delete: " + selected + "?");
        Button cancelButton = new Button("Cancel", buttonClickEvent -> this.close());
        Button confirmButton = new Button("Confirm");
        confirmButton.addClickListener(buttonClickEvent -> {
            setDeleteState(true);
            this.close();
        });
        this.setCloseOnOutsideClick(false);

        HorizontalLayout buttonLayout = new HorizontalLayout(cancelButton, confirmButton);

        VerticalLayout dialogLayout = new VerticalLayout(questionLabel, buttonLayout);

        this.add(dialogLayout);
    }
}
