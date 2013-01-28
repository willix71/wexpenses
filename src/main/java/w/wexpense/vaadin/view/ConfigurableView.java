package w.wexpense.vaadin.view;

import w.wexpense.vaadin.PropertyConfiguror;

import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;

public class ConfigurableView<T> extends VerticalLayout {
	
	private static final long serialVersionUID = -1978284182111196480L;
	
	protected Class<T> entityClass;
	
	protected PropertyConfiguror propertyConfiguror;

	protected Window window;
	
	public ConfigurableView(Class<T> entityClass) {
		this.entityClass = entityClass;
	}
	
	public Class<T> getEntityClass() {
		return entityClass;
	}
	
	public PropertyConfiguror getPropertyConfiguror() {
		return this.propertyConfiguror;
	}
	
	public void setPropertyConfiguror(PropertyConfiguror propertyConfiguror) {
		this.propertyConfiguror = propertyConfiguror;
	}

	public String getTitle() {
		return null;
	}

	public Window getWexWindow() {
		return window;
	}

	public void setWexWindow(Window window) {
		this.window = window;
		this.window.setCaption(getTitle());
	}
	
	
}