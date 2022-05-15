package hu.gdf.thesis;

import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.html.*;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.page.AppShellConfigurator;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.theme.Theme;
import com.vaadin.flow.theme.lumo.Lumo;


@Route("")
@CssImport("./styles/shared-styles.css")
@CssImport(value="./styles/vaadin-text-field-styles.css", themeFor = "vaadin-text-field")
@CssImport(value="./styles/vaadin-grid-styling.css", themeFor = "vaadin-grid")
@Theme(themeClass = Lumo.class, variant = Lumo.DARK)
public class MainView extends VerticalLayout implements AppShellConfigurator {
	public MainView () {
		this.add(new AppHeader());
		VerticalLayout pageLayout = new VerticalLayout();
		H2 titleText = new H2("Monitoring System");
		H3 informationHeaderText = new H3("Usage Information");
		H5 informationText = new H5("This web application was developed to monitor simulated application servers with REST API. "
				+ "The information required to build REST requests, are stored in .json Configuration files. "
				+ "To create, or edit Configuration files, please click on the \"Config Creator Page\" button, to navigate to the Config Creator page. "
				+ "If you already have Configuration files set up, click on the \"Monitoring Page\" button to navigate to the Monitoring Page.");

		pageLayout.add(titleText,informationHeaderText,informationText);
		this.add(pageLayout);
	}
}