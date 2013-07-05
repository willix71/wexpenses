package w.wexpense.vaadin;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
		String caption = component.getTitle();
		setCaption(caption);

		String width = component.getPropertyConfiguror().getPropertyValue(PropertyConfiguror.windowWidth, "100%");
		String height = component.getPropertyConfiguror().getPropertyValue(PropertyConfiguror.windowHeight, "100%");				
		setWidth(width);
		setHeight(height);		
		center();
		
		component.setWexWindow(this);
		
		setContent(component);
		
		addCloseListener(new Window.CloseListener() {
			private static final long serialVersionUID = -1;
			@Override
			public void windowClose(CloseEvent e) {
				// on closing, remember the window's size 
				
				float width = WexWindow.this.getWidth();
				String widthUnit = WexWindow.this.getWidthUnits().getSymbol();
				
				float height = WexWindow.this.getHeight();
				String heightUnit = WexWindow.this.getHeightUnits().getSymbol();

				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug(component.getEntityClass().getSimpleName() + " Width="+ width + widthUnit + " Height=" + height + heightUnit);
				}
				
				component.getPropertyConfiguror().setPropertyValue(PropertyConfiguror.windowWidth, width + widthUnit);
				component.getPropertyConfiguror().setPropertyValue(PropertyConfiguror.windowHeight, height + heightUnit);
			}
		});
	}
	
	public void close() {
		super.close();
	}
}
