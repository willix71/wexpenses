package w.wexpense.vaadin.view;

import w.wexpense.vaadin.PropertyConfiguror;

import com.vaadin.ui.VerticalLayout;

public class ConfigurableView<T> extends VerticalLayout {
	
	private static final long serialVersionUID = -1978284182111196480L;
	
	protected Class<T> entityClass;
	
	protected PropertyConfiguror propertyConfiguror;

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

}