package hu.gdf.thesis;
import com.vaadin.flow.component.contextmenu.MenuItem;
import com.vaadin.flow.component.menubar.MenuBar;

public class AppHeader extends MenuBar {
	public AppHeader() {

		//Navigation between pages

		MenuItem homeMenuItem = super.addItem("Home Page");
		MenuItem monitoringMenuItem = super.addItem("Monitoring Page");
		MenuItem configPageMenuItem = super.addItem("Configuration Page");
		MenuItem testPageMenuItem = super.addItem("Test Request Page");

		homeMenuItem.addClickListener((t) -> homeMenuItem.getUI().
				ifPresent(ui -> ui.navigate("")));

		monitoringMenuItem.addClickListener((item) -> monitoringMenuItem.getUI().
				ifPresent(ui -> ui.navigate("monitoring")));

		configPageMenuItem.addClickListener((t) -> configPageMenuItem.getUI().
				ifPresent(ui -> ui.navigate("config")));

		testPageMenuItem.addClickListener((t) -> testPageMenuItem.getUI().
				ifPresent(ui -> ui.navigate("test")));




	}
}