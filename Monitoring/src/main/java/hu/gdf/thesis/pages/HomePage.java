package hu.gdf.thesis.pages;

import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.details.Details;
import com.vaadin.flow.component.html.*;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.page.AppShellConfigurator;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.theme.Theme;
import com.vaadin.flow.theme.lumo.Lumo;
import hu.gdf.thesis.utils.other.AppHeader;

@PageTitle("Home")
@Route("")
@Theme(themeClass = Lumo.class, variant = Lumo.DARK)
@CssImport("./styles/shared-styles.css")
@CssImport(value="./styles/vaadin-text-field-styles.css",
		themeFor = "vaadin-text-field")
@CssImport(value="./styles/vaadin-grid-styling.css", themeFor = "vaadin-grid")
public class HomePage extends VerticalLayout implements AppShellConfigurator {
	public HomePage() {
		this.add(new AppHeader());

		VerticalLayout pageLayout = new VerticalLayout();

		H2 titleText = new H2("Introduction");

		Details monitoringDetail = new Details();
		H3 monitoringDetailSummary = new H3("Monitoring Page");
		monitoringDetail.setSummary(monitoringDetailSummary);

		Details configDetail = new Details();
		H3 configDetailSummary = new H3("Configuration Page");
		configDetail.setSummary(configDetailSummary);

		Details testDetail = new Details();
		H3 testDetailSummary = new H3("Test Request Page");
		testDetail.setSummary(testDetailSummary);

		Label informationText = new Label("This web application was developed for developers and administrators, " +
				"to help monitor application servers with REST API. The information required to build REST requests, " +
				"are stored in JSON configuration files. These files are application specific, " +
				"therefore it is recommended to use the built in tools of the application, to handle these files properly. " +
				"To create, or edit configuration files, please click on the \"Config Creator Page\" button, to navigate to the Config Creator page. " +
				"If you already have configuration files set up, click on the \"Monitoring Page\" button to start real time monitoring. ");
		Label monitoringText = new Label("The purpose of this webpage is to display a collection of important server metrics you wish to monitor. " +
				"When selecting a file from the „Configuration File” select, " +
				"your configured requests are automatically sent and the table is automatically populated with the REST responses. " +
				"Only one configuration file can be active at a time. The table has a timer bound to it. If the allotted time passes, " +
				"the application will automatically refresh the monitored metrics. " +
				"The user may disable or enable this timer, with the „Start/Pause” button. This is implemented for the purpose of easier readability. " +
				"The user may force a refresh with the „manual refresh” button. ");
		Label configText = new Label("This webpage is for creating Configuration files, or editing existing ones." +
				" The websites design guides you through the creation process, with labeled input fields and helpful notifications." +
				" To understand how to create your configurations, it is knowledge is required about how JSON files are structured," +
				" and how the Monitoring application stores the input information. ");
		Label serverDataText = new Label("By default, the page is loaded with the navigation menu, " +
				"a configuration file select and the buttons to create or modify your files. To start, " +
				"create a configuration file, press the „Create Config” button. Doing so will present you with a dialog, " +
				"where you can specify the name of your file and the address of the server you wish to monitor, " +
				"consisting of the host name and port number. " +
				"The refresh timer field is responsible for the timer value of the monitoring table, measured in seconds. " +
				"The „Addresses” button opens up a dialog, where you can save multiple e-mail addresses to your file. " +
				"These are necessary if you wish to receive alert e-mails from the Alert web application.");
		Label categoryText = new Label("Categories are user defined monitoring types. " +
				"It useful to help categorize the metrics if the user wishes to monitor a lot of data, " +
				"received from many different endpoints. ");
		Label endpointText = new Label("The first field is the API endpoint URL of the data resource. When defining this, start with a forward slash (/)." +
				"The last one is the alert boolean value, when set to true, the Alert web application will search for operations bound to searched fields. " +
				"In order to send an alert e-mail, the field’s operation must have it’s alert value set to true. " +
				"In case of HTTP Errors, the application will send alerts anyway. ");
		Label fieldText = new Label("Add the full path of the field in the returned resource," +
				" using the JSONPath query language syntax (e.g.: fields[*].field). ");
		Label operationText = new Label ("Operations consist of five elements: The operator, which defines the evaluation condition. " +
				"The two operands which is the selected field from the resource, and a custom value, this is the one you can define. " +
				"The action is the action to be performed when a defined condition is met. The last one is the alert boolean value, when set to true, " +
				"the Alert web application will send emails when a defined condition is met. ");
		Label testText = new Label("The user can manually check a REST request’s result. " +
				"The returning result (if there is any) will be printed in the text area located on the page. " +
				"This helps the user identify the measurable metric fields. " +
				"This also helps to check connection to the server, which the user is trying to monitor. ");

		H3 authorInformationHeaderText = new H3("Author information");
		Label authorName = new Label("Author: Varga Balázs");
		Label neptunCode = new Label("Neptun code: B2VDMH");
		Label email = new Label("    E-mail address: varga.balazs.0428@gmail.com");

		monitoringDetail.addContent(monitoringText);
		configDetail.addContent(configText, serverDataText, categoryText, endpointText, fieldText, operationText);
		testDetail.addContent(testText);
		pageLayout.add(titleText,informationText, monitoringDetail, configDetail, testDetail, authorInformationHeaderText,authorName, neptunCode, email);
		this.add(pageLayout);
	}
}