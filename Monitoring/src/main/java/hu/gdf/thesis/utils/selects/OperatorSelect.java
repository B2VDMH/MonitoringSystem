package hu.gdf.thesis.utils.selects;

import com.vaadin.flow.component.select.Select;

import java.util.ArrayList;
import java.util.List;

public class OperatorSelect extends Select {

    public OperatorSelect() {
        List<String> operatorList = new ArrayList<>();

        operatorList.add("equals");
        operatorList.add("doesNotEqual");
        operatorList.add("contains");
        operatorList.add("doesNotContain");
        operatorList.add("greater");
        operatorList.add("lesser");
        operatorList.add("startsWith");
        operatorList.add("endsWith");
        operatorList.add("true");
        operatorList.add("false");

        this.setItems(operatorList);
        this.setLabel("Operators");
        this.setWidth("300px");
        this.setHelperText("Select a condition operator.");
    }
}
