package hu.gdf.thesis.utils.notifications;

import com.vaadin.flow.component.notification.Notification;

public class CustomNotification extends Notification {

    public CustomNotification() {
        this.setDuration(3000);
        this.setPosition(Notification.Position.TOP_CENTER);

    }

}
