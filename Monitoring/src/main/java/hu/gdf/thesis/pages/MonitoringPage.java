package hu.gdf.thesis.pages;

import com.flowingcode.vaadin.addons.simpletimer.SimpleTimer;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import hu.gdf.thesis.model.Config;
import hu.gdf.thesis.utils.other.AppHeader;
import hu.gdf.thesis.backend.*;
import hu.gdf.thesis.model.Response;
import lombok.extern.slf4j.Slf4j;

import java.io.Serial;
import java.math.BigDecimal;
import java.util.*;

@PageTitle("Monitoring Page")
@Route("monitoring")
@Slf4j
public class MonitoringPage extends VerticalLayout{

    @Serial
    private static final long serialVersionUID = 6529685098267757690L;

    static Config config = new Config();
    static String fileName;
    private boolean checkTimerState;
    private final List<Response> responseList = new ArrayList<>();

    public MonitoringPage(FileHandler fileHandler, RestClient restClient) {

        //Select component for file selection
        Select fileSelect = new Select();
        fileSelect.setItems(fileHandler.listFilesInDirectory());
        fileSelect.setLabel("Configuration Files");
        fileSelect.setHelperText("Select the server you wish to monitor. " + "Current directory: " + fileHandler.directory());
        fileSelect.setWidth("400px");

        //Add Page selection menu bar and file selector component to page
        this.add(new AppHeader(), fileSelect);
        VerticalLayout gridLayout = new VerticalLayout();
        HorizontalLayout timerLayout = new HorizontalLayout();

        //Create Grid without auto creation of it's columns
        Grid<Response> monitoringGrid =
                new Grid<>(Response.class, false);

        //Create Grid columns
        monitoringGrid.addColumn(Response::getHostName).setHeader("Server Host");
        monitoringGrid.addColumn(Response::getCategoryType).setHeader("Category");
        monitoringGrid.addColumn(Response::getRestURL).setHeader("Endpoint");
        monitoringGrid.addColumn(Response::getFieldPath).setHeader("Field");
        monitoringGrid.addColumn(Response::getFieldValue).setHeader("Value");
        monitoringGrid.setClassNameGenerator(Response::getAction);

        for(Grid.Column<Response> column:monitoringGrid.getColumns()) {
            column.setAutoWidth(true);
            column.setSortable(true);
            column.setFlexGrow(2);
            column.setResizable(true);
        }

        monitoringGrid.setItems();

        gridLayout.add(monitoringGrid);
        this.add(timerLayout, gridLayout);
        fileSelect.addValueChangeListener(e -> {
            try {
                timerLayout.removeAll();

                fileName = String.valueOf(fileSelect.getValue());
                config = fileHandler.deserialize(fileName);

                if (config != null) {
                    ResponseHandler responseHandler = new ResponseHandler();
                    responseHandler.buildResponseList(config, restClient, responseList);
                    monitoringGrid.setItems(responseList);

                    Button refreshButton = new Button("Manual Refresh");
                    refreshButton.addClickListener(buttonClickEvent -> {
                        responseHandler.buildResponseList(config, restClient, responseList);
                        monitoringGrid.setItems(responseList);
                    });
                    SimpleTimer timer = new SimpleTimer(new BigDecimal(config.getRefreshTimer()));
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
                    timerLayout.add(timer, timerButton, refreshButton);

                    timer.addTimerEndEvent(timerEndedEvent -> {

                        fileName = String.valueOf(fileSelect.getValue());
                        config = fileHandler.deserialize(fileName);

                        responseHandler.buildResponseList(config, restClient, responseList);
                        monitoringGrid.setItems(responseList);

                        timer.reset();
                        timer.start();

                    });
                    timer.start();
                }
            } catch (NullPointerException ex) {
                log.error("Exception occurred when trying to fill monitoring grid with data: " + ex.getMessage());
            }
        });
    }
}
