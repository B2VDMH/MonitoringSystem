package hu.gdf.thesis.utils.selects;

import com.vaadin.flow.component.select.Select;

import java.util.ArrayList;
import java.util.List;

public class ActionSelect extends Select {
    public ActionSelect() {
        List<String> actionList = new ArrayList<>();

        actionList.add("colorGreen");
        actionList.add("colorRed");
        actionList.add("colorYellow");

        this.setItems(actionList);
        this.setLabel("Actions");
        this.setWidth("300px");
    }
}
