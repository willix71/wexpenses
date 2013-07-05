package w.wexpense.vaadin7.component;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import w.wexpense.persistence.PersistenceUtils;

import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.data.util.filter.Compare;
import com.vaadin.data.util.filter.Or;
import com.vaadin.shared.ui.MultiSelectMode;
import com.vaadin.ui.Table;

public class OneToManyTable<T> extends Table {

    private static final long serialVersionUID = -3984686118567195208L;

    private Collection<T> beans;
   
    public OneToManyTable(Class<T> entityClass) {
        super(null, new BeanItemContainer<T>(entityClass));
       
        setSelectable(true);
        setMultiSelect(true);
        setMultiSelectMode(MultiSelectMode.SIMPLE);
       
    }

    public Filter getFilter() {       
        BeanItemContainer<T> container = (BeanItemContainer<T>) getContainerDataSource();
        String propertyId = PersistenceUtils.getIdName(container.getBeanType());
        List<Filter> filters= new ArrayList<Filter>();
        for(T t:  container.getItemIds()) {
            filters.add( new Compare.Equal(propertyId, container.getItem(t).getItemProperty(propertyId).getValue()) );
        }
       
        return new  Or(filters.toArray(new Filter[filters.size()]));
    }

//    public Collection<Object> getBeansId() {
//        List<Object> ids = new ArrayList<Object>();
//        BeanItemContainer<T> container = (BeanItemContainer<T>) getContainerDataSource();
//        for(T t:  container.getItemIds()) {
//            ids.add( container.getItem(t).getItemProperty(propertyId).getValue() );
//        }
//        return ids;       
//    }
   
    public boolean isEmpty() {
        return beans==null || beans.isEmpty();
    }
   
    public Collection<T> getBeans() {
        return beans;
    }
   
    public void setBeans(Collection<T> ts) {
        beans = ts;

        BeanItemContainer<T> container = (BeanItemContainer<T>) getContainerDataSource();
        container.removeAllItems();
        for(T t: ts) {
            container.addBean(t);           
        }
    }   
   
    public void resetBeans(Collection<T> ts) {
        beans.clear();
        beans.addAll(ts);
       
        BeanItemContainer<T> container = (BeanItemContainer<T>) getContainerDataSource();
        container.removeAllItems();
        for(T t: ts) {
            container.addBean(t);           
        }       
    }
   
    public void removeBean(T...ts) {
        BeanItemContainer<T> container = (BeanItemContainer<T>) getContainerDataSource();
        for(T t:ts) {
            container.removeItem(t);
            beans.remove(t);
        }
    }
   
    public void addBean(T...ts){
        BeanItemContainer<T> container = (BeanItemContainer<T>) getContainerDataSource();
        for(T t:ts) {
            container.addBean(t);
            beans.add(t);
        }
    }
   
   
}