package hu.gdf.thesis.pages;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import hu.gdf.thesis.utils.other.AppHeader;
import hu.gdf.thesis.backend.RestClient;
import hu.gdf.thesis.utils.other.CustomHorizontalLayout;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.reactive.function.client.WebClientRequestException;
import org.springframework.web.reactive.function.client.WebClientResponseException;

@PageTitle("Test Request")
@Route("test")
@Slf4j
public class ResponseViewer extends VerticalLayout {
    public ResponseViewer(@Autowired RestClient restClient) {

        this.add(new AppHeader());

        TextField restUrlField = new TextField("Test Endpoint");
        restUrlField.setHelperText("Write a full URL of a REST request here, to see the content of the response.");
        restUrlField.setWidth("800px");

        TextArea responseTA = new TextArea ("REST Response");

        responseTA.setWidth("80%");
        responseTA.setHeight("800px");
        responseTA.setReadOnly(true);

        Button testButton = new Button("Send Request");
        testButton.addClickListener(buttonClickEvent -> {
            try {
                String responseJson = restClient.
                        getRequest(restUrlField.getValue());
                responseTA.setValue(responseJson);
            } catch (WebClientRequestException ex) {
                responseTA.setValue("Server Unreachable! " +
                        "Please check the" +
                        " provided address for input errors.");
                log.info(String.valueOf(ex.getMostSpecificCause()));
            } catch (WebClientResponseException ex) {
                responseTA.setValue(ex.getLocalizedMessage());
                log.info(String.valueOf(ex.getMostSpecificCause()));
            }
        });
        CustomHorizontalLayout topLayout = new CustomHorizontalLayout(restUrlField, testButton);
        this.add(topLayout, responseTA);
    }

}

