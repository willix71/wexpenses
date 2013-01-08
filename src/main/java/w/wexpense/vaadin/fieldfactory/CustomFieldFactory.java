package w.wexpense.vaadin.fieldfactory;

import com.vaadin.data.Item;
import com.vaadin.ui.Field;

public interface CustomFieldFactory {

	Field newInstance(Item item);
		
}
