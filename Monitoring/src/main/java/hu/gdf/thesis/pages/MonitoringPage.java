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
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import hu.gdf.thesis.AppHeader;
import hu.gdf.thesis.backend.*;
import hu.gdf.thesis.model.Response;
import hu.gdf.thesis.model.config.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.util.*;
import java.util.function.Consumer;

@PageTitle("Monitoring Page")
@Route("monitoring")
@Slf4j
public class MonitoringPage extends VerticalLayout {
    static Config config = new Config();
    static String fileName;
    private boolean checkTimerState;
    private List<Response> responseList = new ArrayList<>();
    private GridListDataView<Response> responseDataView;

    public MonitoringPage(@Autowired FileHandler fileHandler, RestClient restClient) {

        //Select component for file selection
        Select fileSelect = new Select();
        fileSelect.setItems(fileHandler.listFilesInDirectory());
        fileSelect.setLabel("Configuration File Selector");
        fileSelect.setHelperText("Select the server you wish to monitor.");

        //Add Page selection menu bar and file selector component to page
        this.add(new AppHeader(), fileSelect);

        VerticalLayout gridLayout = new VerticalLayout();
        HorizontalLayout timerLayout = new HorizontalLayout();

        Grid<Response> monitoringGrid = new Grid<>(Response.class, false);

        //Create Grid columns
        Grid.Column<Response> serverHostColumn = monitoringGrid.addColumn(Response::getHostName).setHeader("Server Host").setSortable(true).setFlexGrow(0).setAutoWidth(true);
        Grid.Column<Response> categoryColumn = monitoringGrid.addColumn(Response::getCategoryType).setHeader("Category").setSortable(true).setFlexGrow(0).setAutoWidth(true);
        Grid.Column<Response> restURLColumn = monitoringGrid.addColumn(Response::getRestURL).setHeader("REST URL").setSortable(true).setFlexGrow(0).setAutoWidth(true);
        Grid.Column<Response> fieldPathColumn = monitoringGrid.addColumn(Response::getFieldPath).setHeader("Field").setSortable(true).setFlexGrow(0).setAutoWidth(true);
        Grid.Column<Response> fieldValueColumn = monitoringGrid.addColumn(Response::getFieldValue).setHeader("Value").setSortable(true).setFlexGrow(0).setAutoWidth(true);
        monitoringGrid.setClassNameGenerator(Response::getColor);

        responseDataView = monitoringGrid.setItems();

        //Grid Component's Filter Headers based on createFilterHeader static component
        monitoringGrid.getHeaderRows().clear();
        HeaderRow headerRow = monitoringGrid.appendHeaderRow();

        //Search bars per grid row
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


        gridLayout.add(monitoringGrid);
        this.add(timerLayout, gridLayout);
        fileSelect.addValueChangeListener(e -> {
            try {
                timerLayout.removeAll();

                fileName = String.valueOf(fileSelect.getValue());
                config = fileHandler.deserializeJsonConfig(fileHandler.readFromFile(fileName), Config.class);
                responseDataView = monitoringGrid.setItems();

                if (config != null) {
                    ResponseHandler responseHandler = new ResponseHandler();
                    responseHandler.buildResponseList(config, restClient, responseList);
                    responseDataView = monitoringGrid.setItems(responseList);

                    SimpleTimer timer = new SimpleTimer(new BigDecimal(config.getServer().getRefreshTimer()));
                    Button timerButton = new Button("Start/Pause");

                    //Periodically refresh grid
                    timerButton.addClickListener(buttonClickEvent -> {
                        if (checkTimerState) {
                            timer.pause();
                            checkTimerState = false;
                        } else {
                            timer.start();
                            checkTimerState = true;
                        }
                    });
                    timerLayout.add(timer, timerButton);

                    timer.addTimerEndEvent(timerEndedEvent -> {

                        fileName = String.valueOf(fileSelect.getValue());
                        config = fileHandler.deserializeJsonConfig(fileHandler.readFromFile(fileName), Config.class);

                        responseHandler.buildResponseList(config, restClient, responseList);
                        responseDataView = monitoringGrid.setItems(responseList);

                        timer.reset();
                        timer.start();

                    });
                    timer.start();
                } else {
                    log.error("");
                }
            } catch (NullPointerException ex) {
                log.error("Exception occurred when trying to fill monitoring grid with data: " + ex.getMessage());
            }
        });

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

    //Inner static class containing custom setters for filtering and methods to check if data in grid data view matches the searched data
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
