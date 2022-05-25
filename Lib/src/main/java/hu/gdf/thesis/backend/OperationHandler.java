package hu.gdf.thesis.backend;

import hu.gdf.thesis.model.config.Operation;
import lombok.Getter;
import org.springframework.stereotype.Service;

import static hu.gdf.thesis.backend.TypeConverter.tryParseBool;
import static hu.gdf.thesis.backend.TypeConverter.tryParseInt;

@Service

public class OperationHandler {

    @Getter
    private String color;
    @Getter
    private boolean checkState = true;
    @Getter
    private boolean emailState = false;

    //Called from checkOperation if operation conditions are met, used for coloring monitoring grid rows
    public void executeAction(String actionValue) {
        switch (actionValue) {
            case "colorGreen" -> color = "colorGreen";
            case "colorYellow" -> color = "colorYellow";
            case "colorRed" -> color = "colorRed";
            case "colorPurple" -> color = "colorPurple";
        }
    }

    //If condition requirement is met on field's value, call executeAction method
    //If operation alert is "true" in config, set emailState boolean to true
    public void checkOperation(Operation operation, String fieldValue) {

        switch (operation.getOperator()) {
            //Check if field's value is equal to operation value
            case "equals":
                if (fieldValue.equalsIgnoreCase(operation.getValue())) {
                    executeAction(operation.getAction());
                    if (operation.isAlert()) {
                        emailState = true;
                    }
                }
                break;

            //Check if field's value is not equal to operation value
            case "doesNotEqual":
                if (!fieldValue.equalsIgnoreCase(operation.getValue())) {
                    executeAction(operation.getAction());
                    if (operation.isAlert()) {
                        emailState = true;
                    }
                }
                break;
            //Check if field's value contains operation value
            case "contains":
                if (fieldValue.contains(operation.getValue())) {
                    executeAction(operation.getAction());
                    if (operation.isAlert()) {
                        emailState = true;
                    }
                }
                break;
            //Check if field's value does not contain operation value
            case "doesNotContain":
                if (!fieldValue.contains(operation.getValue())) {
                    executeAction(operation.getAction());
                    if (operation.isAlert()) {
                        emailState = true;
                    }
                }
                break;
            //Check if field's numeric value is greater than operation value.
            case "greater":
                if (tryParseInt(operation.getValue())) {
                    if (Integer.parseInt(fieldValue) > Integer.parseInt(operation.getValue())) {
                        executeAction(operation.getAction());
                        if (operation.isAlert()) {
                            emailState = true;
                        }
                    }
                }
                break;
            //Check if field's numeric value is lesser than operation value.
            case "lesser":
                if (tryParseInt(operation.getValue())) {
                    if (Integer.parseInt(fieldValue) < Integer.parseInt(operation.getValue())) {
                        executeAction(operation.getAction());
                        if (operation.isAlert()) {
                            emailState = true;
                        }
                    }
                }
                break;
            //Check if field's value starts with the operation value.
            case "startsWith":
                if (fieldValue.startsWith(operation.getValue())) {
                    executeAction(operation.getAction());
                    if (operation.isAlert()) {
                        emailState = true;
                    }
                }
                break;
            //Check if field's value ends with the operation value.
            case "endsWith":
                if (fieldValue.endsWith((operation.getValue()))) {
                    executeAction(operation.getAction());
                    if (operation.isAlert()) {
                        emailState = true;
                    }
                }
                break;
            //Check if field's value is true
            case "true":
                if (tryParseBool(fieldValue)) {
                    boolean entityFieldBoolValue = Boolean.parseBoolean(fieldValue);
                    if (entityFieldBoolValue) {
                        executeAction(operation.getAction());
                        if (operation.isAlert()) {
                            emailState = true;
                        }
                    }
                }
                break;
            //Check if field's value is false
            case "false":
                if (tryParseBool(fieldValue)) {
                    boolean entityFieldBoolValue = Boolean.parseBoolean(fieldValue);
                    if (!entityFieldBoolValue) {
                        executeAction(operation.getAction());
                        if (operation.isAlert()) {
                            emailState = true;
                        }
                    }
                }
                break;
            //If the checkState boolean is false, no action will be executed on the field (meaning no grid rows will be colored)
            default:
                checkState = false;
        }
    }
}

