package hu.gdf.thesis.pages;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.component.select.data.SelectListDataView;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import hu.gdf.thesis.AppHeader;
import hu.gdf.thesis.backend.FileHandler;
import hu.gdf.thesis.model.config.*;
import hu.gdf.thesis.utils.dialogs.*;
import hu.gdf.thesis.utils.layouts.CustomHorizontalLayout;
import hu.gdf.thesis.utils.selects.CustomSelect;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

@PageTitle("Config Page")
@Route("config")
@Slf4j
public class ConfigCreatorPage extends VerticalLayout {
    static String fileName = "";
    static Config config = new Config();
    static Category category = new Category();
    static Entry entry = new Entry();
    static RestField restField = new RestField();
    static Operation operation = new Operation();

    static SelectListDataView<Category> categoryDataView;
    static SelectListDataView<Entry> entryDataView;
    static SelectListDataView<RestField> restFieldDataView;
    static SelectListDataView<Operation> operationDataView;


    public ConfigCreatorPage(@Autowired FileHandler fileHandler) {

        VerticalLayout pageContentLayout = new VerticalLayout();
        this.add(new AppHeader());

        //Component containers
        CustomHorizontalLayout configFileLayout = new CustomHorizontalLayout();
        CustomHorizontalLayout categoryLayout = new CustomHorizontalLayout();
        CustomHorizontalLayout entryLayout = new CustomHorizontalLayout();
        CustomHorizontalLayout restFieldLayout = new CustomHorizontalLayout();
        CustomHorizontalLayout operationLayout = new CustomHorizontalLayout();
        CustomHorizontalLayout addressLayout = new CustomHorizontalLayout();

        //Select component for file sselection
        Select fileSelect = new Select();
        fileSelect.setItems(fileHandler.listFilesInDirectory());
        fileSelect.setLabel("Configuration File Selector");
        fileSelect.setHelperText("Select the file you wish to fill with edit.");

        //Select components, that will appear on page at user input
        CustomSelect categorySelect = new CustomSelect("Select Category");
        CustomSelect entrySelect = new CustomSelect("Select Entry");
        CustomSelect restFieldSelect = new CustomSelect("Select REST Field Path");
        CustomSelect operationSelect = new CustomSelect("Select Operation");

        //Buttons and their click listener functions

        Button createFileButton = new Button("Create Config");
        createFileButton.addClickListener(buttonClickEvent -> {

            ConfigCreatorDialog configCreatorDialog = new ConfigCreatorDialog(fileHandler);
            configCreatorDialog.open();
            configCreatorDialog.addDetachListener(detachEvent -> {
                if (configCreatorDialog.isSaveState()) {
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

        Button addEntryButton = new Button("Add Entry");
        addEntryButton.addClickListener(buttonClickEvent -> {

            EntryDialog entryDialog = new EntryDialog(fileName, config, category, fileHandler);
            entryDialog.open();
            entryDialog.addDetachListener(detachEvent -> {
                if (entryDialog.isSaveState()) {
                    entryDataView.addItem(entryDialog.getEntry());
                }
            });
        });

        Button editEntryButton = new Button("Edit Entry");
        editEntryButton.addClickListener(buttonClickEvent -> {
            try {
            EditEntryDialog editEntryDialog = new EditEntryDialog(fileName, config, category, entry, fileHandler);
            editEntryDialog.open();
            editEntryDialog.addDetachListener(detachEvent -> {
                if (!editEntryDialog.isDeleteState()) {
                    entryDataView.refreshAll();
                } else {
                    entryDataView.removeItem(entry);
                }
            });

            } catch (NullPointerException ex) {
                log.warn("Empty selection");
            }
        });


        Button addRestFieldButton = new Button("Add REST Field Path");
        addRestFieldButton.addClickListener(buttonClickEvent -> {

            RestFieldDialog restFieldDialog = new RestFieldDialog(fileName, config, category, entry, fileHandler);
            restFieldDialog.open();
            restFieldDialog.addDetachListener(detachEvent -> {
                if (restFieldDialog.isSaveState()) {
                    restFieldDataView.addItem(restFieldDialog.getRestField());
                }
            });
        });

        Button editRestFieldButton = new Button("Edit REST Field Path");
        editRestFieldButton.addClickListener(buttonClickEvent -> {
            try {
            EditRestFieldDialog editRestFieldDialog = new EditRestFieldDialog(fileName, config, category, entry, restField, fileHandler);
            editRestFieldDialog.open();
            editRestFieldDialog.addDetachListener(detachEvent -> {
                if (!editRestFieldDialog.isDeleteState()) {
                    restFieldDataView.refreshAll();
                } else {
                    restFieldDataView.removeItem(restField);
                }
            });
            } catch (NullPointerException ex) {
                log.warn("Empty selection");
            }
        });

        Button addOperationButton = new Button("Add Operation");
        addOperationButton.addClickListener(buttonClickEvent -> {
            OperationDialog operationDialog = new OperationDialog(fileName, config, category, entry, restField, fileHandler);
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
                EditOperationDialog editOperationDialog = new EditOperationDialog(fileName, config, category, entry, restField, operation, fileHandler);
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
                entrySelect.clear();
                restFieldSelect.clear();
                operationSelect.clear();

                categoryLayout.removeAll();

                fileName = String.valueOf(fileSelect.getValue());
                config = fileHandler.deserializeJsonConfig(fileHandler.readFromFile(fileName), Config.class);

                categoryDataView = (SelectListDataView<Category>) categorySelect.setItems(fileHandler.getAllCategories(config));
                categorySelect.addValueChangeListener(c -> {

                    category = (Category) categorySelect.getValue();

                    try {
                        entrySelect.clear();
                        restFieldSelect.clear();
                        operationSelect.clear();
                        entryLayout.removeAll();

                        entrySelect.clear();

                        entryDataView = (SelectListDataView<Entry>) entrySelect.setItems(fileHandler.getAllEntries(category));
                        entrySelect.addValueChangeListener(e -> {
                            try {
                                restFieldSelect.clear();
                                operationSelect.clear();

                                restFieldLayout.removeAll();

                                entry = (Entry) entrySelect.getValue();

                                restFieldDataView = (SelectListDataView<RestField>) restFieldSelect.setItems(fileHandler.getAllRestFields(entry));
                                restFieldSelect.addValueChangeListener(r -> {
                                    try {
                                        operationSelect.clear();

                                        operationLayout.removeAll();

                                        restField = (RestField) restFieldSelect.getValue();

                                        operationDataView = (SelectListDataView<Operation>) operationSelect.setItems(fileHandler.getAllOperations(restField));
                                        operationSelect.addValueChangeListener(o -> {

                                            operation = (Operation) operationSelect.getValue();

                                        });

                                        operationLayout.add(operationSelect, addOperationButton, editOperationButton);

                                    } catch (Exception ex) {
                                        log.error("Error at setting RestField", ex);
                                    }
                                });

                                restFieldLayout.add(restFieldSelect, addRestFieldButton, editRestFieldButton);

                            } catch (Exception ex) {
                                log.error("Error at setting Entry", ex);
                            }
                        });

                        entryLayout.add(entrySelect, addEntryButton, editEntryButton);

                    } catch (Exception ex) {
                        log.error("Error at setting Category", ex);
                    }
                });

                categoryLayout.add(categorySelect, addCategoryButton, editCategoryButton);

            } catch (Exception ex) {
                log.error("Error at setting Config", ex);
            }
        });
        configFileLayout.add(fileSelect, createFileButton, editFileButton);
        pageContentLayout.add(categoryLayout, entryLayout, restFieldLayout, operationLayout, addressLayout);
        this.add(configFileLayout);
        this.add(pageContentLayout);
    }
}