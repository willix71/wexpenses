package w.wexpense.vaadin;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import w.wexpense.vaadin.ui.SizeUnit;
import w.wexpense.vaadin.view.ConfigurableView;

import com.vaadin.ui.Component;
import com.vaadin.ui.Window;

public class ClosableWindow extends Window {
	private static final long serialVersionUID = 8121179082149508634L;

	private static final Logger LOGGER = LoggerFactory.getLogger(ClosableWindow.class);
	
	public ClosableWindow(final ConfigurableView<?> component) {
		String height = component.getPropertyConfiguror().getPropertyValue(PropertyConfiguror.windowHeight, "100%");
		setHeight(height);
		String width = component.getPropertyConfiguror().getPropertyValue(PropertyConfiguror.windowWidth, "100%");
		setWidth(width);
		
		setCaption(component.getCaption());
		component.setCaption(null);
		
		addComponent(component);
		
		component.addListener(new Component.Listener() {
			private static final long serialVersionUID = 8121179082149508635L;
			
			@Override
			public void componentEvent(Event event) {
				if (event instanceof CloseViewEvent && event.getComponent()==component) {
					float width = ClosableWindow.this.getWidth();
					SizeUnit widthUnit = SizeUnit.fromValue(ClosableWindow.this.getWidthUnits());
					
					float height = ClosableWindow.this.getHeight();
					SizeUnit heightUnit = SizeUnit.fromValue(ClosableWindow.this.getHeightUnits());

					if (LOGGER.isDebugEnabled()) {
						LOGGER.debug(component.getEntityClass().getSimpleName() + " Width="+ width + widthUnit + " Height=" + height + heightUnit);
					}
					
					component.getPropertyConfiguror().setPropertyValue(PropertyConfiguror.windowWidth, width + widthUnit.getAbreviation());
					component.getPropertyConfiguror().setPropertyValue(PropertyConfiguror.windowHeight, height + heightUnit.getAbreviation());
					close();
				}
			}
		});			
	}
}
