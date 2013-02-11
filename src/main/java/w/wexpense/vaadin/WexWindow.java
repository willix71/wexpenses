package w.wexpense.vaadin;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import w.wexpense.vaadin.ui.SizeUnit;
import w.wexpense.vaadin.view.ConfigurableView;

import com.vaadin.ui.Window;

/**
 * A window with a public close() method so that the contained component can close it when it deems necessary.
 * 
 * The window also fetches it's size from the component's properties and remembers them (i.e. saves them) when it closes.
 */
public class WexWindow extends Window {
	private static final long serialVersionUID = 8121179082149508634L;

	private static final Logger LOGGER = LoggerFactory.getLogger(WexWindow.class);
	
	public WexWindow(final ConfigurableView<?> component) {
		String height = component.getPropertyConfiguror().getPropertyValue(PropertyConfiguror.windowHeight, "100%");
		String width = component.getPropertyConfiguror().getPropertyValue(PropertyConfiguror.windowWidth, "100%");
		String caption = component.getTitle();
		
		setHeight(height);		
		setWidth(width);
		center();
		setCaption(caption);
		
		component.setWexWindow(this);
		
		addComponent(component);
		
		addListener(new Window.CloseListener() {
			private static final long serialVersionUID = -1;
			@Override
			public void windowClose(CloseEvent e) {
				// on closing, remember the window's size 
				
				float width = WexWindow.this.getWidth();
				SizeUnit widthUnit = SizeUnit.fromValue(WexWindow.this.getWidthUnits());
				
				float height = WexWindow.this.getHeight();
				SizeUnit heightUnit = SizeUnit.fromValue(WexWindow.this.getHeightUnits());

				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug(component.getEntityClass().getSimpleName() + " Width="+ width + widthUnit + " Height=" + height + heightUnit);
				}
				
				component.getPropertyConfiguror().setPropertyValue(PropertyConfiguror.windowWidth, width + widthUnit.getAbreviation());
				component.getPropertyConfiguror().setPropertyValue(PropertyConfiguror.windowHeight, height + heightUnit.getAbreviation());
			}
		});
	}
	
	public void close() {
		super.close();
	}
}
