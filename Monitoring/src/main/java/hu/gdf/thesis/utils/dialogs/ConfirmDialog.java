package hu.gdf.thesis.utils.dialogs;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;

public class ConfirmDialog extends Dialog {
    private boolean deleteState;

    public ConfirmDialog(String selected) {

        Label questionLabel = new Label("Delete: " + selected + "?");
        Button cancelButton = new Button("Cancel", buttonClickEvent -> this.close());
        Button confirmButton = new Button("Confirm");
        confirmButton.addClickListener(buttonClickEvent -> {
            deleteState=true;
            this.close();
        });
        HorizontalLayout buttonLayout = new HorizontalLayout(cancelButton, confirmButton);
        VerticalLayout dialogLayout = new VerticalLayout(questionLabel, buttonLayout);
        this.add(dialogLayout);
    }
    public boolean isDeleteState() {
        return deleteState;
    }
}
