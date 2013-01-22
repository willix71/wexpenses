package w.wexpense.vaadin.view;

import w.wexpense.vaadin.PropertyConfiguror;

import com.vaadin.event.Action.Handler;
import com.vaadin.event.ItemClickEvent.ItemClickListener;
import com.vaadin.ui.VerticalLayout;

public class ConfigurableView extends VerticalLayout {
	
	private static final long serialVersionUID = -1978284182111196480L;
	
	protected PropertyConfiguror propertyConfiguror;

	public PropertyConfiguror getPropertyConfiguror() {
		return this.propertyConfiguror;
	}
	public void setPropertyConfiguror(PropertyConfiguror propertyConfiguror) {
		this.propertyConfiguror = propertyConfiguror;
	}

}