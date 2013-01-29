package w.wexpense.vaadin.view;

import javax.annotation.PostConstruct;

import w.wexpense.model.Parentable;
import w.wexpense.persistence.PersistenceUtils;
import w.wexpense.vaadin.WexWindow;
import w.wexpense.vaadin.PropertyConfiguror;

import com.vaadin.addon.jpacontainer.EntityItem;
import com.vaadin.event.Action;

public class ParentableView<T extends Parentable<T>> extends GenericView<T> {

	private static final long serialVersionUID = 6499289439725418193L;

	public ParentableView(Class<T> entityClass) {
		super(entityClass);
	}
	
	@PostConstruct
	@Override
	public void build() {
		entitySelectedActions = new Action[] { addAction, editAction, deleteAction, refreshAction };
		noEntitySelectedActions = new Action[] { addAction, refreshAction };

		this.jpaContainer = jpaContainerFactory.getJPAContainer(entityClass, propertyConfiguror.getPropertyValues(PropertyConfiguror.nestedProperties));
		this.jpaContainer.setParentProperty("parent");
		
		buildToolbar();
		buildTable();
	}
	
	@Override
	protected void buildTable() {		
		this.table = new WexTreeTable(jpaContainer, propertyConfiguror);
		table.setSizeFull();
		table.setSelectable(true);
		table.setImmediate(true);
		table.addListener(this);
		table.addActionHandler(this);
		
		addComponent(table);
		setExpandRatio(table, 1);
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
		getApplication().getMainWindow().addWindow(new WexWindow(editor));	
	}
}
