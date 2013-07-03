package w.wexpense.vaadin7.container;

import java.util.Collection;
import java.util.Collections;

import com.vaadin.data.util.BeanItem;
import com.vaadin.data.util.BeanItemContainer;

public class OneToManyContainer<BEANTYPE> extends BeanItemContainer<BEANTYPE> {

	private Collection<BEANTYPE> beans;

	public OneToManyContainer(Class<? super BEANTYPE> type) throws IllegalArgumentException {
		super(type);
	}

	public Collection<BEANTYPE> getBeans() {
		return Collections.unmodifiableCollection(beans);
	}
	
	public void setBeans(Collection<BEANTYPE> ts) {
		beans = ts;
		
		addAll(ts);
	}
	
	public void resetBeans(Collection<BEANTYPE> ts) {
		beans.clear();
		beans.addAll(ts);

		removeAllItems();
		addAll(ts);
	}

	public BeanItem<BEANTYPE> addBean(BEANTYPE t) {		
		beans.add(t);
		
		return super.addBean(t);
	}

	public void removeBean(BEANTYPE t) {	
		removeItem(t);
		
		beans.remove(t);
	}
	
	public boolean isEmpty() {
		return beans.isEmpty();
	}
	
	public void fireItemSetChange() {
      super.fireItemSetChange();
  }
}
