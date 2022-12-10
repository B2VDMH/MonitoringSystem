package hu.gdf.thesis.pages;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.component.select.data.SelectListDataView;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import hu.gdf.thesis.model.*;
import hu.gdf.thesis.utils.notifications.CustomNotification;
import hu.gdf.thesis.utils.other.AppHeader;
import hu.gdf.thesis.backend.FileHandler;
import hu.gdf.thesis.utils.dialogs.*;
import hu.gdf.thesis.utils.other.CustomHorizontalLayout;
import hu.gdf.thesis.utils.selects.CustomSelect;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;

@PageTitle("Configuration Page")
@Route("config")
@Slf4j
public class ConfigCreatorPage extends VerticalLayout {

    private static CustomNotification notification = new CustomNotification();

    String fileName = "";
    Config config = new Config();
    Category category = new Category();
    Endpoint endpoint = new Endpoint();
    Field field = new Field();
    Operation operation = new Operation();

    SelectListDataView<Category> categoryDataView;
    SelectListDataView<Endpoint> endpointDataView;
    SelectListDataView<Field> fieldDataView;
    SelectListDataView<Operation> operationDataView;


    public ConfigCreatorPage(FileHandler fileHandler) {

        VerticalLayout pageContentLayout = new VerticalLayout();
        this.add(new AppHeader());

        //Component containers
        CustomHorizontalLayout configFileLayout = new CustomHorizontalLayout();
        CustomHorizontalLayout categoryLayout = new CustomHorizontalLayout();
        CustomHorizontalLayout endpointLayout = new CustomHorizontalLayout();
        CustomHorizontalLayout fieldLayout = new CustomHorizontalLayout();
        CustomHorizontalLayout operationLayout = new CustomHorizontalLayout();
        CustomHorizontalLayout addressLayout = new CustomHorizontalLayout();

        //Select component for file selection
        Select fileSelect = new Select();
        fileSelect.setItems(fileHandler.listFilesInDirectory());
        fileSelect.setLabel("Configuration File Selector");
        fileSelect.setHelperText("Select the configuration file you wish to edit. Current directory: " + fileHandler.directory());
        fileSelect.setWidth("400px");

        //Select components, that will appear on page at user input
        CustomSelect categorySelect = new CustomSelect("Select Category");
        CustomSelect endpointSelect = new CustomSelect("Select Endpoint");
        CustomSelect fieldSelect = new CustomSelect("Select Field Path");
        CustomSelect operationSelect = new CustomSelect("Select Operation");

        //Buttons and their click listener functions

        Button createFileButton = new Button("Create Config");
        createFileButton.addClickListener(buttonClickEvent -> {

            ConfigDialog configDialog = new ConfigDialog(fileHandler);
            configDialog.open();
            configDialog.addDetachListener(detachEvent -> {
                if (configDialog.isSaveState()) {
                    UI.getCurrent().getPage().reload();
                }
            });
        });

        Button editFileButton = new Button("Edit Config");
        editFileButton.addClickListener(buttonClickEvent -> {
            try {
            EditConfigDialog editConfigDialog = new EditConfigDialog(String.valueOf(fileSelect.getValue()), fileHandler);
            editConfigDialog.open();
            editConfigDialog.addDetachListener(detachEvent -> {
                if (editConfigDialog.isSaveState()) {
                    UI.getCurrent().getPage().reload();
                }
            });
        } catch (NullPointerException ex) {
            log.warn("Empty selection");
        }
        });
        Button addressButton = new Button("Addresses");
        addressButton.addClickListener(buttonClickEvent -> {
                if(fileSelect.isEmpty() || fileSelect.getValue() == null) {
                    notification.setText("Please select a Configuration File!");
                    notification.open();
                } else {
                    AddressDialog addressDialog = new AddressDialog(fileName, config, fileHandler);
                    addressDialog.open();
                    addressDialog.addDetachListener(detachEvent -> {
                        UI.getCurrent().getPage().reload();
                    });
                }
        });
        Button addCategoryButton = new Button("Add Category");
        addCategoryButton.addClickListener(buttonClickEvent -> {

            CategoryDialog categoryDialog = new CategoryDialog(fileName, config, fileHandler);
            categoryDialog.open();
            categoryDialog.addDetachListener(detachEvent -> {
                if (categoryDialog.isSaveState()) {
                    categoryDataView.addItem(categoryDialog.getCategory());
                }
            });
        });

        Button editCategoryButton = new Button("Edit Category");
        editCategoryButton.addClickListener(buttonClickEvent -> {
            try {
            EditCategoryDialog editCategoryDialog = new EditCategoryDialog(fileName, config, category, fileHandler);
            editCategoryDialog.open();
            editCategoryDialog.addDetachListener(detachEvent -> {
                if (!editCategoryDialog.isDeleteState()) {
                    categoryDataView.refreshAll();
                } else {
                    categoryDataView.removeItem(category);
                }
            });
        } catch (NullPointerException ex) {
            log.warn("Empty selection");
        }
        });

        Button addEndpointButton = new Button("Add Endpoint");
        addEndpointButton.addClickListener(buttonClickEvent -> {

            EndpointDialog endpointDialog = new EndpointDialog(fileName, config, category, fileHandler);
            endpointDialog.open();
            endpointDialog.addDetachListener(detachEvent -> {
                if (endpointDialog.isSaveState()) {
                    endpointDataView.addItem(endpointDialog.getEntry());
                }
            });
        });

        Button editEndpointButton = new Button("Edit Endpoint");
        editEndpointButton.addClickListener(buttonClickEvent -> {
            try {
            EditEndpointDialog editEndpointDialog = new EditEndpointDialog(fileName, config, category, endpoint, fileHandler);
            editEndpointDialog.open();
            editEndpointDialog.addDetachListener(detachEvent -> {
                if (!editEndpointDialog.isDeleteState()) {
                    endpointDataView.refreshAll();
                } else {
                    endpointDataView.removeItem(endpoint);
                }
            });

            } catch (NullPointerException ex) {
                log.warn("Empty selection");
            }
        });


        Button addFieldButton = new Button("Add Field Path");
        addFieldButton.addClickListener(buttonClickEvent -> {

            FieldDialog fieldDialog = new FieldDialog(fileName, config, category, endpoint, fileHandler);
            fieldDialog.open();
            fieldDialog.addDetachListener(detachEvent -> {
                if (fieldDialog.isSaveState()) {
                    fieldDataView.addItem(fieldDialog.getField());
                }
            });
        });

        Button editFieldButton = new Button("Edit Field Path");
        editFieldButton.addClickListener(buttonClickEvent -> {
            try {
            EditFieldDialog editFieldDialog = new EditFieldDialog(fileName, config, category, endpoint, field, fileHandler);
            editFieldDialog.open();
            editFieldDialog.addDetachListener(detachEvent -> {
                if (!editFieldDialog.isDeleteState()) {
                    fieldDataView.refreshAll();
                } else {
                    fieldDataView.removeItem(field);
                }
            });
            } catch (NullPointerException ex) {
                log.warn("Empty selection");
            }
        });

        Button addOperationButton = new Button("Add Operation");
        addOperationButton.addClickListener(buttonClickEvent -> {
            OperationDialog operationDialog = new OperationDialog(fileName, config, category, endpoint, field, fileHandler);
            operationDialog.open();
            operationDialog.addDetachListener(detachEvent -> {
                if (operationDialog.isSaveState()) {
                    operationDataView.addItem(operationDialog.getOperation());
                }
            });
        });
        Button editOperationButton = new Button("Edit Operation");
        editOperationButton.addClickListener(buttonClickEvent -> {
            try {
                EditOperationDialog editOperationDialog = new EditOperationDialog(fileName, config, category, endpoint, field, operation, fileHandler);
                editOperationDialog.open();
                editOperationDialog.addDetachListener(detachEvent -> {
                    if (!editOperationDialog.isDeleteState()) {
                        operationDataView.refreshAll();
                    } else {
                        operationDataView.removeItem(operation);
                    }
                });
            } catch (NullPointerException ex) {
                log.warn("Empty selection");
            }
        });

        fileSelect.addValueChangeListener(f -> {
            try {
                categorySelect.clear();
                endpointSelect.clear();
                fieldSelect.clear();
                operationSelect.clear();

                categoryLayout.removeAll();

                fileName = String.valueOf(fileSelect.getValue());
                config = fileHandler.deserialize(fileName);

                categoryDataView = (SelectListDataView<Category>) categorySelect.setItems(new ArrayList<>(config.getCategories()));
                categorySelect.addValueChangeListener(c -> {
                    category = (Category) categorySelect.getValue();

                    try {
                        endpointSelect.clear();
                        fieldSelect.clear();
                        operationSelect.clear();

                        endpointLayout.removeAll();

                        endpointDataView = (SelectListDataView<Endpoint>) endpointSelect.setItems(new ArrayList<>(category.getEndpoints()));
                        endpointSelect.addValueChangeListener(e -> {
                            try {
                                fieldSelect.clear();
                                operationSelect.clear();

                                fieldLayout.removeAll();

                                endpoint = (Endpoint) endpointSelect.getValue();

                                fieldDataView = (SelectListDataView<Field>) fieldSelect.setItems(new ArrayList<>(endpoint.getFields()));
                                fieldSelect.addValueChangeListener(r -> {
                                    try {
                                        operationSelect.clear();

                                        operationLayout.removeAll();

                                        field = (Field) fieldSelect.getValue();

                                        operationDataView = (SelectListDataView<Operation>) operationSelect.setItems(new ArrayList<>(field.getOperations()));
                                        operationSelect.addValueChangeListener(o -> {

                                            operation = (Operation) operationSelect.getValue();

                                        });

                                        operationLayout.add(operationSelect, addOperationButton, editOperationButton);

                                    } catch (Exception ex) {
                                        log.error("Error at setting Field", ex);
                                    }
                                });

                                fieldLayout.add(fieldSelect, addFieldButton, editFieldButton);

                            } catch (Exception ex) {
                                log.error("Error at setting Endpoint", ex);
                            }
                        });

                        endpointLayout.add(endpointSelect, addEndpointButton, editEndpointButton);

                    } catch (Exception ex) {
                        log.error("Error at setting Category", ex);
                    }
                });

                categoryLayout.add(categorySelect, addCategoryButton, editCategoryButton);

            } catch (Exception ex) {
                log.error("Error at setting Config", ex);
            }
        });
        configFileLayout.add(fileSelect, createFileButton, editFileButton, addressButton);
        pageContentLayout.add(categoryLayout, endpointLayout, fieldLayout, operationLayout, addressLayout);
        this.add(configFileLayout);
        this.add(pageContentLayout);
    }
}