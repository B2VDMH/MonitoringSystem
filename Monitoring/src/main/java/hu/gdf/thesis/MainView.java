package hu.gdf.thesis;

import com.vaadin.flow.component.dependency.CssImport;
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
	}
}