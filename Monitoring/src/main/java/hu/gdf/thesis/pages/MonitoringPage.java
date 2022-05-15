package hu.gdf.thesis.pages;

import com.flowingcode.vaadin.addons.simpletimer.SimpleTimer;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.HeaderRow;
import com.vaadin.flow.component.grid.dataview.GridListDataView;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.component.textfield.TextFieldVariant;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.Route;
import hu.gdf.thesis.AppHeader;
import hu.gdf.thesis.backend.*;
import hu.gdf.thesis.model.Response;
import hu.gdf.thesis.model.config.*;
import net.minidev.json.JSONArray;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.util.*;
import java.util.function.Consumer;

@Route("monitoring")
public class MonitoringPage extends VerticalLayout {
    static Config config = new Config();
    static String fileName = "";
    private boolean checkTimerState;

    private static final Logger LOGGER = LoggerFactory.getLogger(MonitoringPage.class);

    public MonitoringPage(@Autowired FileHandler fileHandler, RestClient restClient) {


        Select fileSelect = new Select();
        fileSelect.setItems(fileHandler.listFilesInDirectory());
        fileSelect.setLabel("Configuration File Selector");
        fileSelect.setHelperText("Select the server you wish to monitor.");

        this.add(new AppHeader(), fileSelect);

        fileSelect.addValueChangeListener(e -> {
            try {
                fileName = String.valueOf(fileSelect.getValue());
                config = fileHandler.deserializeJsonConfig(fileHandler.readFromFile(fileName), Config.class);

                VerticalLayout gridLayout = new VerticalLayout();
                HorizontalLayout timerLayout = new HorizontalLayout();

                SimpleTimer timer = new SimpleTimer(new BigDecimal(config.getServer().getRefreshTimer()));

                timer.addTimerEndEvent(timerEndedEvent -> {
                    gridLayout.removeAll();
                    gridLayout.add(buildMonitoringGrid(fileHandler, restClient));
                    timer.reset();
                    timer.start();
                });

                Button timerButton = new Button("Start/Pause");
                timerButton.addClickListener(buttonClickEvent -> {
                    if (checkTimerState) {
                        timer.pause();
                        checkTimerState = false;
                    } else {
                        timer.start();
                        checkTimerState = true;
                    }
                });

                timer.start();
                timerLayout.add(timer, timerButton);
                gridLayout.removeAll();
                gridLayout.add(buildMonitoringGrid(fileHandler, restClient));
                this.add(timerLayout, gridLayout);
            } catch (Exception ex) {
                LOGGER.error("File Selection error", ex);
            }
        });

    }

    public Grid<Response> buildMonitoringGrid(FileHandler fileHandler, RestClient restClient) {

        Grid<Response> monitoringGrid = new Grid<>(Response.class, false);

        //Create Grid columns
        Grid.Column<Response> serverHostColumn = monitoringGrid.addColumn(Response::getHostName).setHeader("Server Host").setSortable(true).setFlexGrow(0).setAutoWidth(true);
        Grid.Column<Response> categoryColumn = monitoringGrid.addColumn(Response::getCategoryType).setHeader("Category").setSortable(true).setFlexGrow(0).setAutoWidth(true);
        Grid.Column<Response> restURLColumn = monitoringGrid.addColumn(Response::getRestURL).setHeader("REST URL").setSortable(true).setFlexGrow(0).setAutoWidth(true);
        Grid.Column<Response> fieldPathColumn = monitoringGrid.addColumn(Response::getFieldPath).setHeader("Field").setSortable(true).setFlexGrow(0).setAutoWidth(true);
        Grid.Column<Response> fieldValueColumn = monitoringGrid.addColumn(Response::getFieldValue).setHeader("Value").setSortable(true).setFlexGrow(0).setAutoWidth(true);
        monitoringGrid.setClassNameGenerator(Response::getColor);

        //Building Grid rows based on config file data
        try {
            List<Response> responseList = new ArrayList<>();

            config = fileHandler.deserializeJsonConfig(fileHandler.readFromFile(fileName), Config.class);

            ResponseHandler responseHandler = new ResponseHandler();

            responseHandler.buildResponseList(config, restClient, responseList);

            //Filling grid rows with updated responseList
            GridListDataView<Response> responseDataView = monitoringGrid.setItems(responseList);

            //Grid Component's Filter Headers based on createFilterHeader static component
            monitoringGrid.getHeaderRows().clear();
            HeaderRow headerRow = monitoringGrid.appendHeaderRow();
            Filter filter = new Filter(responseDataView);
            headerRow.getCell(serverHostColumn).setComponent(
                    createFilterHeader(filter::setServerHost));
            headerRow.getCell(categoryColumn).setComponent(
                    createFilterHeader(filter::setCategory));
            headerRow.getCell(restURLColumn).setComponent(
                    createFilterHeader(filter::setRestURL));
            headerRow.getCell(fieldPathColumn).setComponent(
                    createFilterHeader(filter::setFieldPath));
            headerRow.getCell(fieldValueColumn).setComponent(
                    createFilterHeader(filter::setFieldValue));
        } catch (Exception ex) {
            LOGGER.error("Error when building Monitoring Grid", ex);
        }
        return monitoringGrid;
    }

    //Filter Header Component
    private static Component createFilterHeader(Consumer<String> filterChangeConsumer) {
        TextField filterTF = new TextField();
        filterTF.setValueChangeMode(ValueChangeMode.EAGER);
        filterTF.setClearButtonVisible(true);
        filterTF.addThemeVariants(TextFieldVariant.LUMO_SMALL);
        filterTF.setWidthFull();
        filterTF.getStyle().set("max-width", "100%");
        filterTF.addValueChangeListener(
                e -> filterChangeConsumer.accept(e.getValue()));
        VerticalLayout filterLayout = new VerticalLayout(filterTF);
        filterLayout.getThemeList().clear();
        filterLayout.getThemeList().add("spacing-xs");

        return filterLayout;
    }

    //Inner static class containing setters for filtering and methods to check if data matches the searched data
    public static class Filter {
        private final GridListDataView<Response> responseDataView;

        private String serverHost;
        private String category;
        private String restURL;
        private String fieldPath;
        private String fieldValue;

        public Filter(GridListDataView<Response> responseDataView) {
            this.responseDataView = responseDataView;
            this.responseDataView.addFilter(this::test);
        }

        public void setServerHost(String serverHost) {
            this.serverHost = serverHost;
            this.responseDataView.refreshAll();
        }

        public void setCategory(String category) {
            this.category = category;
            this.responseDataView.refreshAll();
        }

        public void setRestURL(String restURL) {
            this.restURL = restURL;
            this.responseDataView.refreshAll();
        }

        public void setFieldPath(String fieldPath) {
            this.fieldPath = fieldPath;
            this.responseDataView.refreshAll();
        }

        public void setFieldValue(String fieldValue) {
            this.fieldValue = fieldValue;
            this.responseDataView.refreshAll();
        }

        public boolean test(Response response) {
            boolean matchesServerHost = matches(response.getHostName(), serverHost);
            boolean matchesCategory = matches(response.getCategoryType(), category);
            boolean matchesRestURL = matches(response.getRestURL(), restURL);
            boolean matchesFieldPath = matches(response.getFieldPath(), fieldPath);
            boolean matchesFieldValue = matches(response.getFieldValue(), fieldValue);

            return matchesServerHost && matchesCategory && matchesRestURL && matchesFieldPath && matchesFieldValue;
        }

        private boolean matches(String value, String searchTerm) {
            return searchTerm == null || searchTerm.isEmpty() || value
                    .toLowerCase().contains(searchTerm.toLowerCase());
        }
    }
}
