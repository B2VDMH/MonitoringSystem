package hu.gdf.thesis.pages;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.component.select.data.SelectListDataView;
import com.vaadin.flow.router.Route;
import hu.gdf.thesis.AppHeader;
import hu.gdf.thesis.backend.FileHandler;
import hu.gdf.thesis.model.config.*;
import hu.gdf.thesis.utils.dialogs.*;
import hu.gdf.thesis.utils.layouts.CustomHorizontalLayout;
import hu.gdf.thesis.utils.selects.CustomSelect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

@Route("config")
public class ConfigCreatorPage extends VerticalLayout {
    static String fileName = "";
    static Config config = new Config();
    static Category category = new Category();
    static Entry entry = new Entry();
    static RestField restField = new RestField();
    static Operation operation = new Operation();
    static Address address = new Address();

    static SelectListDataView<Category> categoryDataView;
    static SelectListDataView<Entry> entryDataView;
    static SelectListDataView<RestField> restFieldDataView;
    static SelectListDataView<Operation> operationDataView;
    static SelectListDataView<Address> addressDataView;

    private static final Logger LOGGER = LoggerFactory.getLogger(ConfigCreatorPage.class);

    public ConfigCreatorPage(@Autowired FileHandler fileHandler) {

        VerticalLayout pageContentLayout = new VerticalLayout();
        this.add(new AppHeader());

        CustomHorizontalLayout configFileLayout = new CustomHorizontalLayout();
        CustomHorizontalLayout categoryLayout = new CustomHorizontalLayout();
        CustomHorizontalLayout entryLayout = new CustomHorizontalLayout();
        CustomHorizontalLayout restFieldLayout = new CustomHorizontalLayout();
        CustomHorizontalLayout operationLayout = new CustomHorizontalLayout();
        CustomHorizontalLayout addressLayout = new CustomHorizontalLayout();

        Select fileSelect = new Select();
        fileSelect.setItems(fileHandler.listFilesInDirectory());
        fileSelect.setLabel("Configuration File Selector");
        fileSelect.setHelperText("Select the file you wish to add a monitoring category to.");

        CustomSelect categorySelect = new CustomSelect("Select Category");
        CustomSelect entrySelect = new CustomSelect("Select Entry");
        CustomSelect restFieldSelect = new CustomSelect("Select REST Field Path");
        CustomSelect operationSelect = new CustomSelect("Select Operation");
        CustomSelect addressSelect = new CustomSelect("Select e-mail address");

        Button createFileButton = new Button("Create new config");
        createFileButton.addClickListener(buttonClickEvent -> {
            try {
                ConfigCreatorDialog configCreatorDialog = new ConfigCreatorDialog(fileHandler);
                configCreatorDialog.open();
                configCreatorDialog.addDetachListener(detachEvent -> {
                    if (configCreatorDialog.isSaveState()) {
                        UI.getCurrent().getPage().reload();
                    }
                });
            } catch (Exception ex) {
                LOGGER.error("Error when trying create config", ex);
            }
        });

        Button editFileButton = new Button("Edit server data");
        editFileButton.addClickListener(buttonClickEvent -> {
            try {
                EditConfigDialog editConfigDialog = new EditConfigDialog(String.valueOf(fileSelect.getValue()), fileHandler);
                editConfigDialog.open();
                editConfigDialog.addDetachListener(detachEvent -> {
                    if (editConfigDialog.isSaveState()) {
                        UI.getCurrent().getPage().reload();
                    }
                });
            } catch (Exception ex) {
                LOGGER.error("Error editing server data in config file", ex);
            }

        });


        Button addCategoryButton = new Button("Add Category");
        addCategoryButton.addClickListener(buttonClickEvent -> {
            try {

                CategoryDialog categoryDialog = new CategoryDialog(fileName, config, fileHandler);
                categoryDialog.open();
                categoryDialog.addDetachListener(detachEvent -> {

                    if (categoryDialog.isSaveState()) {
                        categoryDataView.addItem(categoryDialog.getCategory());
                    }
                });
            } catch (Exception ex) {
                LOGGER.error("Error when trying to add Category to config", ex);
            }

        });

        Button deleteCategoryButton = new Button("Delete Category");
        deleteCategoryButton.addClickListener(buttonClickEvent -> {
            try {
                ConfirmDialog confirmDialog = new ConfirmDialog(categorySelect.getValue().toString());
                confirmDialog.open();
                confirmDialog.addDetachListener(detachEvent -> {
                    if (confirmDialog.isDeleteState()) {
                        fileHandler.deleteCategory(fileName, config, category);
                        categoryDataView.removeItem(category);
                    }
                });
            } catch (Exception ex) {
                LOGGER.error("Error when deleting Category" ,ex);
            }

        });
        Button addEntryButton = new Button("Add Entry");
        addEntryButton.addClickListener(buttonClickEvent -> {
            try {

                EntryDialog entryDialog = new EntryDialog(fileName, config, category, fileHandler);
                entryDialog.open();
                entryDialog.addDetachListener(detachEvent -> {
                    if (entryDialog.isSaveState()) {
                        entryDataView.addItem(entryDialog.getEntry());
                    }
                });
            } catch (Exception ex) {
                LOGGER.error("Error when trying to add Entry to config", ex);
            }

        });
        Button deleteEntryButton = new Button("Delete Entry");
        deleteEntryButton.addClickListener(buttonClickEvent -> {
            try {
                ConfirmDialog confirmDialog = new ConfirmDialog(entrySelect.getValue().toString());
                confirmDialog.open();
                confirmDialog.addDetachListener(detachEvent -> {
                    if (confirmDialog.isDeleteState()) {
                        fileHandler.deleteEntry(fileName, config, category, entry);
                        entryDataView.removeItem(entry);
                    }
                });
            } catch (Exception ex) {
                LOGGER.error("Error when deleting Entry" ,ex);
            }

        });

        Button addRestFieldButton = new Button("Add REST Field Path");
        addRestFieldButton.addClickListener(buttonClickEvent -> {
            try {
                RestFieldDialog restFieldDialog = new RestFieldDialog(fileName, config, category, entry, fileHandler);
                restFieldDialog.open();
                restFieldDialog.addDetachListener(detachEvent -> {
                    if (restFieldDialog.isSaveState()) {
                        restFieldDataView.addItem(restFieldDialog.getRestField());
                    }
                });
            } catch (Exception ex) {
                LOGGER.error("Error when trying to add Rest Field Path to config", ex);
            }

        });

        Button deleteRestFieldButton = new Button("Delete REST Field Path");
        deleteRestFieldButton.addClickListener(buttonClickEvent -> {
            try {
                ConfirmDialog confirmDialog = new ConfirmDialog(restFieldSelect.getValue().toString());
                confirmDialog.open();
                confirmDialog.addDetachListener(detachEvent -> {
                    if (confirmDialog.isDeleteState()) {
                        fileHandler.deleteRestField(fileName, config, category, entry, restField);
                        restFieldDataView.removeItem(restField);
                    }
                });
            } catch (Exception ex) {
                LOGGER.error("Error when deleting RestField Path" ,ex);
            }

        });

        Button addOperationButton = new Button("Add Operation");
        addOperationButton.addClickListener(buttonClickEvent -> {
            try {
                OperationDialog operationDialog = new OperationDialog(fileName, config, category, entry, restField, fileHandler);
                operationDialog.open();
                operationDialog.addDetachListener(detachEvent -> {
                    if (operationDialog.isSaveState()) {
                        operationDataView.addItem(operationDialog.getOperation());
                    }
                });
            }catch (Exception ex) {
                LOGGER.error("Error when trying to add Operation to config", ex);
            }

        });
        Button deleteOperationButton = new Button("Delete Operation");
        deleteOperationButton.addClickListener(buttonClickEvent -> {
            try {
                ConfirmDialog confirmDialog = new ConfirmDialog(operationSelect.getValue().toString());
                confirmDialog.open();
                confirmDialog.addDetachListener(detachEvent -> {
                    if (confirmDialog.isDeleteState()) {
                        fileHandler.deleteOperation(fileName, config, category, entry, restField, operation);
                        operationDataView.removeItem(operation);
                    }
                });
            } catch (Exception ex) {
                LOGGER.error("Error when deleting Operation" ,ex);
            }

        });

        Button addAddressButton = new Button("Add Address");
        addAddressButton.addClickListener(buttonClickEvent -> {
            try {
                AddressDialog addressDialog = new AddressDialog(fileName, config, category, entry, restField, operation, fileHandler);
                addressDialog.open();
                addressDialog.addDetachListener(detachEvent -> {
                    if (addressDialog.isSaveState()) {
                        addressDataView.addItem(addressDialog.getAddress());
                    }
                });
            } catch (Exception ex) {
                LOGGER.error("Error when trying to add Address to config", ex);
            }

        });

        Button deleteAddressButton = new Button("Delete Address");
        deleteAddressButton.addClickListener(buttonClickEvent -> {
            try {
                ConfirmDialog confirmDialog = new ConfirmDialog(addressSelect.getValue().toString());
                confirmDialog.open();
                confirmDialog.addDetachListener(detachEvent -> {
                    if (confirmDialog.isDeleteState()) {
                        fileHandler.deleteAddress(fileName, config, category, entry, restField, operation, address);
                        addressDataView.removeItem(address);
                    }
                });
            } catch (Exception ex) {
                LOGGER.error("Error when deleting Address" ,ex);
            }

        });

        fileSelect.addValueChangeListener(f -> {
            try {
                categorySelect.clear();
                entrySelect.clear();
                restFieldSelect.clear();
                operationSelect.clear();
                addressSelect.clear();

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
                        addressSelect.clear();

                        entryLayout.removeAll();



                        entrySelect.clear();

                        entryDataView = (SelectListDataView<Entry>) entrySelect.setItems(fileHandler.getAllEntries(category));
                        entrySelect.addValueChangeListener(e -> {
                            try {
                                restFieldSelect.clear();
                                operationSelect.clear();
                                addressSelect.clear();

                                restFieldLayout.removeAll();

                                entry = (Entry) entrySelect.getValue();

                                restFieldDataView = (SelectListDataView<RestField>) restFieldSelect.setItems(fileHandler.getAllRestFields(entry));
                                restFieldSelect.addValueChangeListener(r -> {
                                    try {
                                        operationSelect.clear();
                                        addressSelect.clear();

                                        operationLayout.removeAll();

                                        restField = (RestField) restFieldSelect.getValue();

                                        operationDataView = (SelectListDataView<Operation>) operationSelect.setItems(fileHandler.getAllOperations(restField));
                                        operationSelect.addValueChangeListener(o -> {
                                            try {
                                                addressSelect.clear();

                                                addressLayout.removeAll();

                                                operation = (Operation) operationSelect.getValue();

                                                addressDataView = (SelectListDataView<Address>) addressSelect.setItems(fileHandler.getAllAddresses(operation));
                                                addressSelect.addValueChangeListener(a -> {
                                                    try {
                                                        address = (Address) addressSelect.getValue();
                                                    } catch (NullPointerException ignored) {
                                                        LOGGER.debug("Non critical null pointer Exception");
                                                    } catch (Exception ex) {
                                                        LOGGER.error("Error at setting Address", ex);
                                                    }
                                                });
                                                addressLayout.add(addressSelect, addAddressButton, deleteAddressButton);
                                            } catch (NullPointerException ignored) {
                                                LOGGER.debug("Non critical null pointer Exception");
                                            } catch (Exception ex) {
                                                LOGGER.error("Error at setting Operation", ex);
                                            }
                                        });
                                        operationLayout.add(operationSelect, addOperationButton, deleteOperationButton);
                                    } catch (NullPointerException ignored) {
                                        LOGGER.debug("Non critical null pointer Exception");
                                    } catch (Exception ex) {
                                        LOGGER.error("Error at setting RestField", ex);
                                    }
                                });
                                restFieldLayout.add(restFieldSelect, addRestFieldButton, deleteRestFieldButton);
                            } catch (NullPointerException ignored) {
                                LOGGER.debug("Non critical null pointer Exception");
                            } catch (Exception ex) {
                                LOGGER.error("Error at setting Entry", ex);
                            }
                        });
                        entryLayout.add(entrySelect, addEntryButton, deleteEntryButton);
                    } catch (NullPointerException ignored) {
                        LOGGER.debug("Non critical null pointer Exception");
                    } catch (Exception ex) {
                        LOGGER.error("Error at setting Category", ex);
                    }
                });
                categoryLayout.add(categorySelect, addCategoryButton, deleteCategoryButton);
            } catch (NullPointerException ignored) {
                LOGGER.debug("Non critical null pointer Exception");
            } catch (Exception ex) {
                LOGGER.error("Error at setting Config", ex);
            }
        });
        configFileLayout.add(fileSelect, createFileButton, editFileButton);
        pageContentLayout.add(categoryLayout, entryLayout, restFieldLayout, operationLayout, addressLayout);
        this.add(configFileLayout);
        this.add(pageContentLayout);
    }
}