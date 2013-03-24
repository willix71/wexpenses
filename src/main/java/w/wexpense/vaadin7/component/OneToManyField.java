package w.wexpense.vaadin7.component;

import java.util.Collection;

import com.vaadin.ui.Component;
import com.vaadin.ui.CustomField;
import com.vaadin.ui.Label;

public class OneToManyField<T> extends CustomField<Collection<T>> {
	
	private Class<T> entityClass;
	
	private Label l;
	
	public OneToManyField(Class<T> entityClass) {
		this.entityClass = entityClass;
		l = new Label("xxx");
	}
	
	@Override
   protected void setInternalValue(Collection<T> newValue) {
	   super.setInternalValue(newValue);
	   l.setValue(newValue==null?"null":"size="+newValue.size());
   }

	@Override
   protected Component initContent() {
	   return l;
   }

   @Override
   @SuppressWarnings("unchecked")
   public Class<? extends Collection<T>> getType() {
	   return (Class<? extends Collection<T>>) Collection.class;
   }

}
