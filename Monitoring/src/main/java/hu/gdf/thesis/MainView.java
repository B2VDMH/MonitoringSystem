package hu.gdf.thesis;

import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.html.*;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.page.AppShellConfigurator;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.PWA;
import com.vaadin.flow.theme.Theme;
import com.vaadin.flow.theme.lumo.Lumo;

@PageTitle("Monitoring")
@Route("")
@PWA(name = "Monitoring System", shortName = "Monitoring")
@CssImport("./styles/shared-styles.css")
@CssImport(value="./styles/vaadin-text-field-styles.css", themeFor = "vaadin-text-field")
@CssImport(value="./styles/vaadin-grid-styling.css", themeFor = "vaadin-grid")
@Theme(themeClass = Lumo.class, variant = Lumo.DARK)
public class MainView extends VerticalLayout implements AppShellConfigurator {
	public MainView () {
		this.add(new AppHeader());

		VerticalLayout pageLayout = new VerticalLayout();
		H2 titleText = new H2("Monitoring System - Home Page");
		H3 informationHeaderText = new H3("Usage Information");
		Label informationText = new Label("This web application was developed for developers and application server administrators,"
				+ "to help monitor application servers with REST API. " +
				"The information required to build requests, are stored in JSON Configuration files. " +
				"To create, or edit Configuration files, please click on the \"Config Creator Page\" button, to navigate to the Config Creator page. " +
				"If you already have Configuration files set up, click on the \"Monitoring Page\" button to navigate to the Monitoring Page.");

		H3 authorInformationHeaderText = new H3("Author information");
		Label authorName = new Label("Author: Varga Balázs");
		Label neptunCode = new Label("Neptun code: B2VDMH");
		Label email = new Label("    E-mail address: varga.balazs0428@gmail.com");

		this.add(titleText);
		pageLayout.add(informationHeaderText, informationText, authorInformationHeaderText, authorName, neptunCode, email);
		this.add(pageLayout);
	}
}