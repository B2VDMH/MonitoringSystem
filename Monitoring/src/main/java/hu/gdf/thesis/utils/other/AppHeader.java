package hu.gdf.thesis.utils.other;
import com.vaadin.flow.component.contextmenu.MenuItem;
import com.vaadin.flow.component.menubar.MenuBar;

public class AppHeader extends MenuBar {
	public AppHeader() {
		//Menu Buttons to navigate between pages
		MenuItem homeMenuItem = super.addItem("Home Page");
		MenuItem monitoringMenuItem = super.addItem("Monitoring Page");
		MenuItem configMenuItem = super.addItem("Configuration Page");
		MenuItem testPageMenuItem = super.addItem("Test Request Page");

		homeMenuItem.addClickListener((t) -> {
			homeMenuItem.getUI().
					ifPresent(ui -> ui.navigate(""));
		});
		monitoringMenuItem.addClickListener((t) -> {
			monitoringMenuItem.getUI().
					ifPresent(ui -> ui.navigate("monitoring"));
		});
		configMenuItem.addClickListener((t) -> {
			configMenuItem.getUI().
					ifPresent(ui -> ui.navigate("config"));
		});
		testPageMenuItem.addClickListener((t) -> {
			testPageMenuItem.getUI().
					ifPresent(ui -> ui.navigate("test"));
		});
	}
}