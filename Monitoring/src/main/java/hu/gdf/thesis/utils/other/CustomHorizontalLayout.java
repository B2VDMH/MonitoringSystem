package hu.gdf.thesis.utils.other;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;

public class CustomHorizontalLayout extends HorizontalLayout {


    public CustomHorizontalLayout(Component... children) {
        super(children);
        this.setAlignItems(Alignment.CENTER);
        this.setAlignItems(Alignment.BASELINE);
    }
}
