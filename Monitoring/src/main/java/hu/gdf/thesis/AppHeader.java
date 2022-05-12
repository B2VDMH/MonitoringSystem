package hu.gdf.thesis;
import com.vaadin.flow.component.contextmenu.MenuItem;
import com.vaadin.flow.component.menubar.MenuBar;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;

public class AppHeader extends HorizontalLayout {
	public AppHeader() {
		//Home page
		//Menu Buttons to navigate between pages

		MenuBar menuBar = new MenuBar();
		MenuItem monitoringMenuItem = menuBar.addItem("Monitoring Page");
		MenuItem configPageMenuItem = menuBar.addItem("Config Creator Page");

		monitoringMenuItem.addClickListener((t) -> {
			monitoringMenuItem.getUI().ifPresent(ui -> ui.navigate("monitoring"));
		});
		configPageMenuItem.addClickListener((t) -> {
			configPageMenuItem.getUI().ifPresent(ui -> ui.navigate("config"));
		});

		this.add(menuBar);
	}
}