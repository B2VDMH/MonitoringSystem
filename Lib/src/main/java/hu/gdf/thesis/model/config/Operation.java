
package hu.gdf.thesis.model.config;

import javax.annotation.Generated;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

@Generated("jsonschema2pojo")
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

    @SerializedName("addresses")
    @Expose
    private List<Address> addresses = new ArrayList<>();

    public String getOperator() {
        return operator;
    }

    public void setOperator(String operator) {
        this.operator = operator;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public boolean isAlert() {
        return alert;
    }

    public void setAlert(boolean alert) {
        this.alert = alert;
    }

    public List<Address> getAddresses() {
        return addresses;
    }

    public void setAddresses(List<Address> addresses) {
        this.addresses = addresses;
    }

    @Override
    public String toString() {
        return "Operator: " + operator + " - " + "Value: " + value + " - " + "Action: " + action + " - " + "Alert: " + alert;
    }
}
