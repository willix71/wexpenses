package w.wexpense.vaadin;

import com.vaadin.ui.Component;
import com.vaadin.ui.Window;

public class ClosableWindow extends Window {
	private static final long serialVersionUID = 8121179082149508634L;

	public ClosableWindow(final Component component) {
		setSizeFull();
		
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
	
	public ClosableWindow(final Component component, String width, String height) {
		this(component);
		setWidth(width);
		setHeight(height);
	}
}
