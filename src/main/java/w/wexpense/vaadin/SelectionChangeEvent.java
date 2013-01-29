package w.wexpense.vaadin;

import java.util.Collection;
import java.util.Collections;

import com.vaadin.ui.Component;

public class SelectionChangeEvent extends Component.Event {

	private static final long serialVersionUID = -3060677120276997236L;

	private Object id;
	
	public SelectionChangeEvent(Component source, Object id) {
		super(source);
		this.id = id;
	}
	
	public Object getId() {
		return id;
	}
	public Collection<?> getIds() {
		if (id instanceof Collection) {
			return (Collection<?>) id;
		} else {
			return Collections.singleton(id);
		}
	}

}
