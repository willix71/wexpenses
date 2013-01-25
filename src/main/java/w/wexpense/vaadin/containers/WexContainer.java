package w.wexpense.vaadin.containers;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.vaadin.data.Container;
import com.vaadin.data.Item;
import com.vaadin.data.Property;
import com.vaadin.data.util.BeanItem;

public class WexContainer<T, ID extends Serializable> implements Container, Container.Sortable { //, Container.Indexed, Container.Filter {

	private static final long serialVersionUID = 6915092535414002412L;

	final Class<T> clazz;
	final BeanItem<T> REF;
	
	Map<T, BeanItem<T>> cache = new HashMap<T, BeanItem<T>>();
	List<T> current = new ArrayList<T>();
	
	public WexContainer(Class<T> clazz) {
		this.clazz = clazz;
		this.REF = new BeanItem<T>(newInstance());
	}

	public void setContent(List<T> current) {
		this.current = current;
	}
	
	// ===== Container =====
	
	@Override
	public BeanItem<T> getItem(Object itemId) {
		T id = (T) itemId;
		BeanItem<T> t = cache.get(id);
		if (t==null) {
			t = new BeanItem<T>(id);
			cache.put(id, t);					
		}
		return t;
	}

	@Override
	public Collection<?> getContainerPropertyIds() {
		return current;
	}

	@Override
	public Collection<?> getItemIds() {
		return REF.getItemPropertyIds();
	}

	@Override
	public Property getContainerProperty(Object itemId, Object propertyId) {
		return getItem(itemId).getItemProperty(propertyId);
	}

	@Override
	public Class<?> getType(Object propertyId) {
		return REF.getItemProperty(propertyId).getType();
	}

	@Override
	public int size() {
		return current.size();
	}

	@Override
	public boolean containsId(Object itemId) {
		return current.contains(itemId);
	}

	@Override
	public Item addItem(Object itemId) throws UnsupportedOperationException {
		T t = (T) itemId;
		current.add(t);
		return getItem(itemId);
	}

	@Override
	public Object addItem() throws UnsupportedOperationException {
		T t = newInstance();
		current.add(t);
		return t;
	}

	@Override
	public boolean removeItem(Object itemId)
			throws UnsupportedOperationException {
		T t = (T) itemId;
		cache.remove(t);
		current.remove(t);
		return false;
	}

	@Override
	public boolean addContainerProperty(Object propertyId, Class<?> type,
			Object defaultValue) throws UnsupportedOperationException {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean removeContainerProperty(Object propertyId)
			throws UnsupportedOperationException {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean removeAllItems() throws UnsupportedOperationException {
		cache.clear();
		current.clear();
		return true;
	}
	
	// ===== Container.Filter =====	
	

	// ===== Container.Order =====
	
	@Override
	public Object nextItemId(Object itemId) {
		T id = (T) itemId;
		int index = current.indexOf(id) + 1;
		if (index == 0) return null;
		return index == current.size()?null:current.get(index);
	}

	@Override
	public Object prevItemId(Object itemId) {
		T id = (T) itemId;
		int index = current.indexOf(id);
		if (index == -1) return null;
		return index == 0?null:current.get(index-1);
	}
	@Override
	public Object firstItemId() {
		return current.isEmpty()?null:current.get(0);
	}

	@Override
	public Object lastItemId() {
		return current.isEmpty()?null:current.get(current.size()-1);
	}

	@Override
	public boolean isFirstId(Object itemId) {
		if (current.isEmpty()) return false;
		return current.get(0).equals(itemId);		
	}

	@Override
	public boolean isLastId(Object itemId) {
		if (current.isEmpty()) return false;
		return current.get(current.size()-1).equals(itemId);	
	}
	
	@Override
	public Object addItemAfter(Object previousItemId)
			throws UnsupportedOperationException {
		T prevId = (T) previousItemId;
		int index = current.indexOf(prevId);
		if (index > -1) {
			T newT = newInstance();
			current.add(index+1, newT);
			return newT;
		}
		return null;
	}

	@Override
	public Item addItemAfter(Object previousItemId, Object newItemId)
			throws UnsupportedOperationException {
		T prevId = (T) previousItemId;
		int index = current.indexOf(prevId);
		if (index > -1) {
			T newId = (T) newItemId;
			current.add(index+1, newId);
			return getItem(newItemId);
		}
		return null;
	}
	
	// ===== Container.Sortable =====	

	@Override
	public void sort(final Object[] propertyId, final boolean[] ascending) {
		Collections.sort(current, new Comparator<T>() {
			@Override
			public int compare(T o1, T o2) {
				BeanItem<T> b1 = new BeanItem<T>(o1);
				BeanItem<T> b2 = new BeanItem<T>(o2);
				for(int i=0;i<propertyId.length;i++) {
					Object v1 = b1.getItemProperty(propertyId[i]).getValue();
					Object v2 = b2.getItemProperty(propertyId[i]).getValue();
					int c = ((Comparable) v1).compareTo(v2);
					if (c!=0) return ascending[i]?c:-c;
				}
				return 0;
			}
		});		
	}	
	
	@Override
	public Collection<?> getSortableContainerPropertyIds() {
		List<Object> ids = new ArrayList<Object>();		
        for (Object propertyId : getContainerPropertyIds()) {
            Class<?> propertyType = getType(propertyId);
            if (Comparable.class.isAssignableFrom(propertyType)
                    || propertyType.isPrimitive()) {
            	ids.add(propertyId);
            }
        }
        return ids;
	}
		
	// ===== Others =====
	
	protected T newInstance() {
		try {
			return clazz.newInstance();
		} catch(Exception e) {
			throw new RuntimeException(e);
		}
	}

}
