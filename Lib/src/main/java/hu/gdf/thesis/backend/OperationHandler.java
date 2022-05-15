package hu.gdf.thesis.backend;

import hu.gdf.thesis.model.config.Operation;
import org.springframework.stereotype.Service;

import static hu.gdf.thesis.backend.TypeConverter.tryParseBool;
import static hu.gdf.thesis.backend.TypeConverter.tryParseInt;

@Service
public class OperationHandler {

    private String color;

    private boolean checkState = true;

    private boolean emailState = false;

    public void executeAction(String actionValue) {
        switch (actionValue) {
            case "colorGreen":
                color = "colorGreen";
                break;
            case "colorYellow":
                color = "colorYellow";
                break;
            case "colorRed":
                color = "colorRed";
                break;
        }
    }

    public void checkOperation(Operation operation, String fieldValue) {

            switch (operation.getOperator()) {
                case "equals":
                    if (fieldValue.equalsIgnoreCase(operation.getValue())) {
                        executeAction(operation.getAction());
                        if(operation.isAlert()) {
                            emailState = true;
                        }
                    }
                    break;
                case "doesNotEquals":
                    if (!fieldValue.equalsIgnoreCase(operation.getValue())) {
                        executeAction(operation.getAction());
                        if(operation.isAlert()) {
                            emailState = true;
                        }
                    }
                    break;
                case "contains":
                    if (fieldValue.contains(operation.getValue())) {
                        executeAction(operation.getAction());
                        if(operation.isAlert()) {
                            emailState = true;
                        }
                    }
                    break;
                case "doesNotContain":
                    if (!fieldValue.contains(operation.getValue())) {
                        executeAction(operation.getAction());
                        if(operation.isAlert()) {
                            emailState = true;
                        }
                    }
                    break;
                case "greater":
                    if (operation.getValue() != null|| !tryParseInt(operation.getValue()))  {
                        if (Integer.parseInt(fieldValue) > Integer.parseInt(operation.getValue())) {
                            executeAction(operation.getAction());
                            if(operation.isAlert()) {
                                emailState = true;
                            }
                        }
                    }
                    break;
                case "lesser":
                    if (operation.getValue() != null|| !tryParseInt(operation.getValue())) {
                        if (Integer.parseInt(fieldValue) < Integer.parseInt(operation.getValue())) {
                            executeAction(operation.getAction());
                            if(operation.isAlert()) {
                                emailState = true;
                            }
                        }
                    }
                    break;
                case "startsWith":
                    if (fieldValue.startsWith(operation.getValue())) {
                        executeAction(operation.getAction());
                        if(operation.isAlert()) {
                            emailState = true;
                        }
                    }
                    break;
                case "endsWith":
                    if (fieldValue.endsWith((operation.getValue()))) {
                        executeAction(operation.getAction());
                        if(operation.isAlert()) {
                            emailState = true;
                        }
                    }
                    break;
                case "true":
                    if (tryParseBool(fieldValue)) {
                        boolean entityFieldBoolValue = Boolean.parseBoolean(fieldValue);
                        if (entityFieldBoolValue) {
                            executeAction(operation.getAction());
                            if(operation.isAlert()) {
                                emailState = true;
                            }
                        }
                    }
                    break;
                case "false":
                    if (tryParseBool(fieldValue)) {
                        boolean entityFieldBoolValue = Boolean.parseBoolean(fieldValue);
                        if (!entityFieldBoolValue) {
                            executeAction(operation.getAction());
                            if(operation.isAlert()) {
                                emailState = true;
                            }
                        }
                    }
                    break;
                default: checkState = false;
            }
        }

    public boolean isCheckState() {
        return checkState;
    }

    public boolean isEmailState() {
        return emailState;
    }

    public String getColor() {

        return color;
    }
}

