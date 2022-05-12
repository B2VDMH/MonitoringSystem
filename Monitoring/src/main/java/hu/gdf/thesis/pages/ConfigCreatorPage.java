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
            ConfigCreatorDialog configCreatorDialog = new ConfigCreatorDialog(fileHandler);
            configCreatorDialog.open();
            configCreatorDialog.addDetachListener(detachEvent -> {
                if(configCreatorDialog.isSaveState()) {
                    UI.getCurrent().getPage().reload();
                }
            });
        });

        Button editFileButton = new Button("Edit server data");
        editFileButton.addClickListener(buttonClickEvent -> {
            EditConfigDialog editConfigDialog = new EditConfigDialog(String.valueOf(fileSelect.getValue()), fileHandler);
            editConfigDialog.open();
            editConfigDialog.addDetachListener(detachEvent -> {
                if (editConfigDialog.isSaveState()) {
                    UI.getCurrent().getPage().reload();
                }
            });
        });


        Button addCategoryButton = new Button("Add Category");
        addCategoryButton.addClickListener(buttonClickEvent -> {
            CategoryDialog categoryDialog = new CategoryDialog(fileName, config ,fileHandler);
            categoryDialog.open();
            categoryDialog.addDetachListener(detachEvent -> {
                if (categoryDialog.isSaveState()) {
                    categoryDataView.addItem(categoryDialog.getCategory());
                }
            });
        });

        Button deleteCategoryButton = new Button("Delete Category");
        deleteCategoryButton.addClickListener(buttonClickEvent -> {
            ConfirmDialog confirmDialog = new ConfirmDialog(categorySelect.getValue().toString());
            confirmDialog.open();
            confirmDialog.addDetachListener(detachEvent -> {
                if(confirmDialog.isDeleteState()) {
                    categoryDataView.removeItem(category);
                    fileHandler.deleteCategory(fileName, config, category);
                }
            });
        });
        Button addEntryButton = new Button("Add Entry");
        addEntryButton.addClickListener(buttonClickEvent -> {
            EntryDialog entryDialog = new EntryDialog(fileName, config, category, fileHandler);
            entryDialog.open();
            entryDialog.addDetachListener(detachEvent -> {
                if(entryDialog.isSaveState()){
                    entryDataView.addItem(entryDialog.getEntry());
                }
            });
        });
        Button deleteEntryButton = new Button("Delete Entry");
        deleteEntryButton.addClickListener(buttonClickEvent -> {
            ConfirmDialog confirmDialog = new ConfirmDialog(entrySelect.getValue().toString());
            confirmDialog.open();
            confirmDialog.addDetachListener(detachEvent -> {
                if(confirmDialog.isDeleteState()) {
                    entryDataView.removeItem(entry);
                    fileHandler.deleteEntry(fileName, config, category, entry);
                }
            });
        });

        Button addRestFieldButton = new Button("Add REST Field Path");
        addRestFieldButton.addClickListener(buttonClickEvent -> {
            RestFieldDialog restFieldDialog = new RestFieldDialog(fileName, config, category, entry, fileHandler);
            restFieldDialog.open();
            restFieldDialog.addDetachListener(detachEvent -> {
                if(restFieldDialog.isSaveState()) {
                    restFieldDataView.addItem(restFieldDialog.getRestField());
                }
            });
        });

        Button deleteRestFieldButton = new Button("Delete REST Field Path");
        deleteRestFieldButton.addClickListener(buttonClickEvent -> {
            ConfirmDialog confirmDialog = new ConfirmDialog(restFieldSelect.getValue().toString());
            confirmDialog.open();
            confirmDialog.addDetachListener(detachEvent -> {
                if(confirmDialog.isDeleteState()) {
                    restFieldDataView.removeItem(restField);
                    fileHandler.deleteRestField(fileName, config, category, entry, restField);
                }
            });
        });

        Button addOperationButton = new Button("Add Operation");
        addOperationButton.addClickListener(buttonClickEvent -> {
            OperationDialog operationDialog = new OperationDialog(fileName, config, category, entry, restField, fileHandler);
            operationDialog.open();
            operationDialog.addDetachListener(detachEvent -> {
                if(operationDialog.isSaveState()) {
                    operationDataView.addItem(operationDialog.getOperation());
                }
            });
        });
        Button deleteOperationButton = new Button("Delete Operation");
        deleteOperationButton.addClickListener(buttonClickEvent -> {
            ConfirmDialog confirmDialog = new ConfirmDialog(operationSelect.getValue().toString());
            confirmDialog.open();
            confirmDialog.addDetachListener(detachEvent -> {
                if(confirmDialog.isDeleteState()) {
                    operationDataView.removeItem(operation);
                    fileHandler.deleteOperation(fileName, config, category, entry, restField, operation);
                }
            });
        });

        Button addAddressButton = new Button("Add Address");
        addAddressButton.addClickListener(buttonClickEvent -> {
            AddressDialog addressDialog = new AddressDialog(fileName, config, category, entry, restField, operation, fileHandler);
            addressDialog.open();
            addressDialog.addDetachListener(detachEvent -> {
                if(addressDialog.isSaveState()) {
                    addressDataView.addItem(addressDialog.getAddress());
                }
            });
        });

        Button deleteAddressButton = new Button("Delete Address");
        deleteAddressButton.addClickListener(buttonClickEvent -> {
            ConfirmDialog confirmDialog = new ConfirmDialog(addressSelect.getValue().toString());
            confirmDialog.open();
            confirmDialog.addDetachListener(detachEvent -> {
                if(confirmDialog.isDeleteState()) {
                    addressDataView.removeItem(address);
                    fileHandler.deleteAddress(fileName, config, category, entry, restField, operation, address);
                }
            });
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
                    try {
                        entrySelect.clear();
                        restFieldSelect.clear();
                        operationSelect.clear();
                        addressSelect.clear();

                        entryLayout.removeAll();

                        category = (Category) categorySelect.getValue();

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
                                                    } catch (Exception ex){
                                                        System.err.println(ex);
                                                        System.err.println("Error at setting Address");
                                                    }
                                                });
                                                addressLayout.add(addressSelect, addAddressButton, deleteAddressButton);
                                            } catch(Exception ex) {
                                                System.err.println(ex);
                                                System.err.println("Error at setting Operation");
                                            }
                                        });
                                        operationLayout.add(operationSelect, addOperationButton, deleteOperationButton);
                                    } catch (Exception ex) {
                                        System.out.println(ex);
                                        System.err.println("Error at setting REST Field");
                                    }
                                });
                                restFieldLayout.add(restFieldSelect, addRestFieldButton, deleteRestFieldButton);
                            }catch(Exception ex) {
                                System.out.println(ex);
                                System.err.println("Error at setting Entry");
                            }
                        });
                        entryLayout.add(entrySelect, addEntryButton, deleteEntryButton);
                    } catch (Exception ex) {
                        System.out.println(ex);
                        System.err.println("Error at setting Category");
                    }
                });
                categoryLayout.add(categorySelect,addCategoryButton,deleteCategoryButton);
            } catch (Exception ex) {
                System.out.println(ex);
                System.err.println("Error at setting File");
            }
        });
        configFileLayout.add(fileSelect, createFileButton, editFileButton);
        pageContentLayout.add(categoryLayout, entryLayout, restFieldLayout, operationLayout, addressLayout);
        this.add(configFileLayout);
        this.add(pageContentLayout);
    }
}