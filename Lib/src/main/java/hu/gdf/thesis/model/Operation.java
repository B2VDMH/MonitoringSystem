
package hu.gdf.thesis.model;

import com.google.gson.annotations.SerializedName;
import lombok.Data;


@Data
public class Operation {

    @SerializedName("operator")
    private String operator;
    @SerializedName("value")
    private String value;
    @SerializedName("action")
    private String action;
    @SerializedName("alert")
    private boolean alert;


    @Override
    public String toString() {
        return "Operator: " + operator + " - " + "Value: " + value + " - " + "Action: " + action + " - " + "Alert: " + alert;
    }
}
