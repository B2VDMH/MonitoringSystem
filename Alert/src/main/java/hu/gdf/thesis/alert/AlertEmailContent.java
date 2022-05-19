package hu.gdf.thesis.alert;

import lombok.EqualsAndHashCode;
import lombok.Setter;

@Setter
@EqualsAndHashCode
public class AlertEmailContent {

    private String serverHost;
    private String category;
    private String restURL;
    private String fieldPath;
    private String fieldValue;
    private String operator;
    private String value;


    @Override
    public String toString() {
        return "Alert! " + "\n" +
                "Server: " + serverHost + "\n" +
                "Category: " + category + "\n" +
                "REST URL: " + restURL + "\n" +
                "Field Path: " + fieldPath + "\n" +
                "Expected: " + value + "\n" +
                "Operator: " + operator + "\n" +
                "Found: " + fieldValue;
    }
}
