package hu.gdf.thesis.utils.selects;

import com.vaadin.flow.component.select.Select;

public class CustomSelect<T> extends Select {
    public CustomSelect(String labelName) {
        this.setLabel(labelName);
        this.setWidth("600px");
    }


}
