package w.wexpense.vaadin.view;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;

import w.wexpense.vaadin.ClosableWindow;
import w.wexpense.vaadin.PropertyConfiguror;
import w.wexpense.vaadin.WexJPAContainerFactory;

import com.vaadin.addon.jpacontainer.EntityItem;
import com.vaadin.addon.jpacontainer.JPAContainer;
import com.vaadin.event.Action;
import com.vaadin.event.ItemClickEvent;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Component;
import com.vaadin.ui.ComponentContainer;
import com.vaadin.ui.HorizontalLayout;

public class GenericView<T> extends AbstractView<T> implements ComponentContainer {
	private static final long serialVersionUID = 5282517667310057582L;

	@Autowired
	protected WexJPAContainerFactory jpaContainerFactory;
	
	protected JPAContainer<T> jpaContainer;
	
	protected HorizontalLayout toolbar;

	protected Component filter;

	public GenericView(Class<T> entityClass) {
		super(entityClass);	
		entitySelectedActions = new Action[] { addAction, editAction, deleteAction, refreshAction };
		noEntitySelectedActions = new Action[] { addAction, refreshAction };
	}
	
	@PostConstruct
	protected void init() {
		buildToolbar();
		buildTable();		
		addComponent(toolbar);
		addComponent(table);
		setExpandRatio(table, 1);
		setCaption( entityClass.getSimpleName() );
		setSizeFull();		
	}
	
	protected void buildTable() {	
		initTable();
		table.setSelectable(true);
		table.setImmediate(true);
		table.setSizeFull();		
		table.addListener(this);
		table.addActionHandler(this);
	}

	protected void initTable() {
		this.jpaContainer = jpaContainerFactory.getJPAContainer(entityClass, propertyConfiguror.getPropertyValues(PropertyConfiguror.nestedProperties));
		this.table = new WexTable(jpaContainer, propertyConfiguror);
	}
	
	protected void buildToolbar() {
		toolbar = new HorizontalLayout();
		
		if (filter !=null) {
			toolbar.addComponent(filter);
			toolbar.setWidth("100%");
			toolbar.setExpandRatio(filter, 1);
			toolbar.setComponentAlignment(filter, Alignment.TOP_RIGHT);
		}
	}

	@Override
	public void addEntity() {
		GenericEditor<T> editor = newEditor();
		editor.setInstance(null, jpaContainer);
		getApplication().getMainWindow().addWindow(new ClosableWindow(editor));	
	}

	@Override
	public void editEntity(Object target) {
		GenericEditor<T> editor = newEditor();		
		EntityItem<T> item = (EntityItem<T>) table.getItem(target);		
		editor.setInstance(item.getEntity(), jpaContainer);
		getApplication().getMainWindow().addWindow(new ClosableWindow(editor));		
	}
	
	@Override
	public void deleteEntity(Object target) {
		jpaContainer.removeItem(target);		
	}

	@Override
	public void refreshContainer() {
		jpaContainer.refresh();
	}
		
	@Override
	public void entitySelected(ItemClickEvent event) {
		GenericEditor<T> editor = newEditor();
		EntityItem<T> t = (EntityItem<T>) event.getItem(); 
		editor.setInstance(t.getEntity(), jpaContainer);
		getApplication().getMainWindow().addWindow(new ClosableWindow(editor));
	}
	
	public GenericEditor<T> newEditor() {
		return null;
	}

	public void setFilter(Component filter) {
		this.filter = filter;
	}
}