package w.wexpense.vaadin7.action;

import com.vaadin.event.Action;

public abstract class ListViewAction extends Action implements Action.Listener {
	public ListViewAction(String caption) {
		super(caption);
	}
	
	public abstract boolean canHandle(Object target, Object sender);
}
