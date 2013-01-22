package w.wexpense.vaadin;

import w.wexpense.vaadin.view.ConfigurableView;

import com.vaadin.ui.Component;
import com.vaadin.ui.Window;

public class ClosableWindow extends Window {
	private static final long serialVersionUID = 8121179082149508634L;

	public ClosableWindow(final ConfigurableView component) {

		String height = component.getPropertyConfiguror().getPropertyValue(PropertyConfiguror.propertyWindowHeight, "100%");
		setHeight(height);
		String width = component.getPropertyConfiguror().getPropertyValue(PropertyConfiguror.propertyWindowWidth, "100%");
		setHeight(width);
		
		
		setCaption(component.getCaption());
		component.setCaption(null);
		
		addComponent(component);
		
		component.addListener(new Component.Listener() {
			private static final long serialVersionUID = 8121179082149508635L;
			
			@Override
			public void componentEvent(Event event) {
				if (event instanceof CloseViewEvent && event.getComponent()==component) {
					close();
				}
			}
		});			
	}
}
