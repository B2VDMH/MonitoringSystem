package hu.gdf.thesis.utils.notifications;

import com.vaadin.flow.component.notification.Notification;

public class CustomNotification extends Notification {

    public CustomNotification(String message) {
        this.setDuration(3000);
        this.setText(message);
        this.setPosition(Notification.Position.TOP_CENTER);
        this.open();
    }
}
