package hu.gdf.thesis.backend;

import hu.gdf.thesis.model.Operation;
import lombok.Getter;
import lombok.Setter;

import static hu.gdf.thesis.backend.TypeConverter.tryParseBool;
import static hu.gdf.thesis.backend.TypeConverter.tryParseInt;

public class OperationHandler {
    @Getter
    @Setter
    private String action;
    @Getter
    @Setter
    private boolean checkState = true;
    @Setter
    @Getter
    private boolean emailState = false;

    //If condition requirement is met on field's value, set action String
    //If operation alert is "true" in config, set emailState boolean to true
    public void checkOperation(Operation operation, String fieldValue) {

        switch (operation.getOperator()) {
            //Check if field's value is equal to operation value
            case "equals":
                if (fieldValue.equalsIgnoreCase(operation.getValue())) {
                    setAction(operation.getAction());
                    if (operation.isAlert()) {
                        setEmailState(true);
                    }
                }
                break;

            //Check if field's value is not equal to operation value
            case "doesNotEqual":
                if (!fieldValue.equalsIgnoreCase(operation.getValue())) {
                    setAction(operation.getAction());
                    if (operation.isAlert()) {
                        setEmailState(true);
                    }
                }
                break;
            //Check if field's value contains operation value
            case "contains":
                if (fieldValue.contains(operation.getValue())) {
                    setAction(operation.getAction());
                    if (operation.isAlert()) {
                        setEmailState(true);
                    }
                }
                break;
            //Check if field's value does not contain operation value
            case "doesNotContain":
                if (!fieldValue.contains(operation.getValue())) {
                    setAction(operation.getAction());
                    if (operation.isAlert()) {
                        setEmailState(true);
                    }
                }
                break;
            //Check if field's numeric value is greater than operation value.
            case "greater":
                if (tryParseInt(operation.getValue())) {
                    if (Integer.parseInt(fieldValue) > Integer.parseInt(operation.getValue())) {
                        setAction(operation.getAction());
                        if (operation.isAlert()) {
                            setEmailState(true);
                        }
                    }
                }
                break;
            //Check if field's numeric value is lesser than operation value.
            case "lesser":
                if (tryParseInt(operation.getValue())) {
                    if (Integer.parseInt(fieldValue) < Integer.parseInt(operation.getValue())) {
                        setAction(operation.getAction());
                        if (operation.isAlert()) {
                            setEmailState(true);
                        }
                    }
                }
                break;
            //Check if field's value starts with the operation value.
            case "startsWith":
                if (fieldValue.startsWith(operation.getValue())) {
                    setAction(operation.getAction());
                    if (operation.isAlert()) {
                        setEmailState(true);
                    }
                }
                break;
            //Check if field's value ends with the operation value.
            case "endsWith":
                if (fieldValue.endsWith((operation.getValue()))) {
                    setAction(operation.getAction());
                    if (operation.isAlert()) {
                        setEmailState(true);
                    }
                }
                break;
            //Check if field's value is true
            case "true":
                if (tryParseBool(fieldValue)) {
                    boolean fieldBoolValue = Boolean.parseBoolean(fieldValue);
                    if (fieldBoolValue) {
                        setAction(operation.getAction());
                        if (operation.isAlert()) {
                            setEmailState(true);
                        }
                    }
                }
                break;
            //Check if field's value is false
            case "false":
                if (tryParseBool(fieldValue)) {
                    boolean fieldBoolValue = Boolean.parseBoolean(fieldValue);
                    if (!fieldBoolValue) {
                        setAction(operation.getAction());
                        if (operation.isAlert()) {
                            setEmailState(true);
                        }
                    }
                }
                break;
            //If the checkState boolean is false, no action will be executed on the field (meaning no grid rows will be colored)
            default:
                setCheckState(false);
        }
    }
}

