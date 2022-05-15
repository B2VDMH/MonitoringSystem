package hu.gdf.thesis.model;

public class AlertEmailContent {
private String serverHost;
private String category;
private String restURL;
private String fieldPath;
private String fieldValue;
private String operator;
private String value;


    public String getServerHost() {
        return serverHost;
    }

    public void setServerHost(String serverHost) {
        this.serverHost = serverHost;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getRestURL() {
        return restURL;
    }

    public void setRestURL(String restURL) {
        this.restURL = restURL;
    }

    public String getFieldPath() {
        return fieldPath;
    }

    public void setFieldPath(String fieldPath) {
        this.fieldPath = fieldPath;
    }

    public String getFieldValue() {
        return fieldValue;
    }

    public void setFieldValue(String foundValue) {
        this.fieldValue = foundValue;
    }

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


    @Override
    public String toString() {
        return "Alert! " + "\n"+
                "Server: " + serverHost + "\n" +
                "Category: " + category + "\n" +
                "REST URL: " + restURL + "\n" +
                "Field Path: " + fieldPath + "\n" +
                "Expected: " + fieldValue + "\n" +
                "operator: " + operator + "\n" +
                "Found instead: " + value;
    }
}
