package w.wexpense.vaadin.view;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import w.wexpense.vaadin.WexWindow;
import w.wexpense.vaadin.PropertyConfiguror;

import com.vaadin.ui.Button;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Button.ClickEvent;

public class ConfigurableView<T> extends VerticalLayout {
	
	private static final long serialVersionUID = -1978284182111196480L;
	
	protected final Logger LOGGER = LoggerFactory.getLogger(getClass());
	
	protected WexWindow window;
	
	protected Class<T> entityClass;
	
	protected PropertyConfiguror propertyConfiguror;
	
	public ConfigurableView(Class<T> entityClass) {
		this.entityClass = entityClass;
	}
	
	public Class<T> getEntityClass() {
		return entityClass;
	}

	public String getTitle() {
		return entityClass.getSimpleName();
	}
	
	public void setWexWindow(WexWindow w) {
		this.window = w;
	}
	
	public void close() {
		this.window.close();
	}
	
	public Button getCloseButton() {
		return new Button("Close", new Button.ClickListener() {
			private static final long serialVersionUID = 1L;

			@Override
			public void buttonClick(ClickEvent event) {
				close();			
			}
		});
	}
	
	public PropertyConfiguror getPropertyConfiguror() {
		return this.propertyConfiguror;
	}
	
	public void setPropertyConfiguror(PropertyConfiguror propertyConfiguror) {
		this.propertyConfiguror = propertyConfiguror;
	}
}