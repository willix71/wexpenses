package w.wexpense.vaadin.view;

import w.wexpense.model.Parentable;
import w.wexpense.persistence.PersistenceUtils;
import w.wexpense.vaadin.ClosableWindow;
import w.wexpense.vaadin.PropertyConfiguror;

import com.vaadin.addon.jpacontainer.EntityItem;

public class ParentableView<T extends Parentable<T>> extends GenericView<T> {

	private static final long serialVersionUID = 6499289439725418193L;

	public ParentableView(Class<T> entityClass) {
		super(entityClass);
	}
	
	public void setInstance() {
		this.jpaContainer = jpaContainerFactory.getJPAContainer(entityClass, propertyConfiguror.getPropertyValues(PropertyConfiguror.nestedProperties));
		this.jpaContainer.setParentProperty("parent");
		
		this.table = new WexTreeTable(jpaContainer, propertyConfiguror);
		if (filter != null) {
			filter.setJPAContainer(this.jpaContainer);
		}
	}
	
	@Override
	public void addEntity() {
		T child = PersistenceUtils.newInstance(entityClass);
		
		Object parentId = table.getValue();
		if (parentId !=null) {
			T parent = ((EntityItem<T>) table.getItem(parentId)).getEntity();					
			child.setParent(parent);
		}
		
		GenericEditor<T> editor = newEditor();
		editor.setInstance(child, jpaContainer);
		getApplication().getMainWindow().addWindow(new ClosableWindow(editor));	
	}
}
