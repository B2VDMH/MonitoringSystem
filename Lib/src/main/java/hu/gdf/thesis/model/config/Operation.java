
package hu.gdf.thesis.model.config;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@EqualsAndHashCode
public class Operation {

    @SerializedName("operator")
    @Expose
    private String operator;

    @SerializedName("value")
    @Expose
    private String value;

    @SerializedName("action")
    @Expose
    private String action;

    @SerializedName("alert")
    @Expose
    private boolean alert;


    @Override
    public String toString() {
        return "Operator: " + operator + " - " + "Value: " + value + " - " + "Action: " + action + " - " + "Alert: " + alert;
    }
}
