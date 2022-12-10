package hu.gdf.thesis.utils.selects;

import com.vaadin.flow.component.select.Select;

import java.util.ArrayList;
import java.util.List;

public class ActionSelect extends Select {
    public ActionSelect() {
        List<String> actionList = new ArrayList<>();

        actionList.add("defaultColor");
        actionList.add("colorGreen");
        actionList.add("colorYellow");
        actionList.add("colorRed");
        actionList.add("colorPurple");

        this.setItems(actionList);
        this.setLabel("Actions");
        this.setWidth("300px");
        this.setHelperText("Select an action to be executed if condition is met. In this case, monitoring grid rows will be colored.");
    }
}
