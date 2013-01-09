package w.wexpense.vaadin;

import com.vaadin.ui.Component;

public class CloseViewEvent extends Component.Event {

	private static final long serialVersionUID = -3060677120276997236L;

	private Object entityId;
	private Object entity;
	
	public CloseViewEvent(Component source) {
		super(source);
	}
}
