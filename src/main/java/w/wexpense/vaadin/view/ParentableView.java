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
	
	@Override
	protected void initTable() {
		this.jpaContainer = jpaContainerFactory.getJPAContainer(entityClass, propertyConfiguror.getPropertyValues(PropertyConfiguror.nestedProperties));
		this.jpaContainer.setParentProperty("parent");
		if (filter != null) {
			filter.setJPAContainer(this.jpaContainer);
		}
		this.table = new WexTreeTable(jpaContainer, propertyConfiguror);
	}
	
	@Override
	public void addEntity() {
		Object parentId = table.getValue();
		T parent = ((EntityItem<T>) table.getItem(parentId)).getEntity();		
		T child = PersistenceUtils.newInstance(entityClass);
		child.setParent(parent);
		
		GenericEditor<T> editor = newEditor();
		editor.setInstance(child, jpaContainer);
		getApplication().getMainWindow().addWindow(new ClosableWindow(editor));	
	}
}
