package w.wexpense.vaadin.view;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.vaadin.dialogs.ConfirmDialog;

import w.wexpense.vaadin.PropertyConfiguror;
import w.wexpense.vaadin.WexJPAContainerFactory;
import w.wexpense.vaadin.WexWindow;
import w.wexpense.vaadin.filter.WexFilter;

import com.vaadin.addon.jpacontainer.EntityItem;
import com.vaadin.addon.jpacontainer.JPAContainer;
import com.vaadin.event.Action;
import com.vaadin.event.ItemClickEvent;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.ComponentContainer;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Notification;
import com.vaadin.ui.UI;

public class GenericView<T> extends AbstractView<T> implements ComponentContainer {
	private static final long serialVersionUID = 5282517667310057582L;

	@Autowired
	protected WexJPAContainerFactory jpaContainerFactory;
	
	protected JPAContainer<T> jpaContainer;
	
	protected HorizontalLayout toolbar;

	protected WexFilter filter;

	public GenericView(Class<T> entityClass) {
		super(entityClass);	
	}

	@PostConstruct
	public void build() {
		entitySelectedActions = new Action[] { addAction, editAction, deleteAction, refreshAction };
		noEntitySelectedActions = new Action[] { addAction, refreshAction };

		jpaContainer = jpaContainerFactory.getJPAContainer(entityClass, propertyConfiguror.getPropertyValues(PropertyConfiguror.nestedProperties));
		
		String[] sortProperties = propertyConfiguror.getPropertyValues(PropertyConfiguror.defaultOrdering);
		// set a default ordering or sorting
		if (sortProperties !=null && sortProperties.length > 0) {
			String[] propertyIds = new String[sortProperties.length];
			boolean[] ascendings = new boolean[sortProperties.length];
			for(int i=0;i<sortProperties.length;i++) {
				char c = sortProperties[i].charAt(0);
				ascendings[i] = c != '-';
				if (c == '+' || c == '-') {
					propertyIds[i] = sortProperties[i].substring(1);
				} else {
					propertyIds[i] = sortProperties[i];
				}
			}
			jpaContainer.sort(propertyIds, ascendings);
		}
		
		buildToolbar();
		buildTable();
	}
	
	protected void buildToolbar() {
		toolbar = new HorizontalLayout();		
		if (filter !=null) {
			filter.setJPAContainer(jpaContainer);
			
			toolbar.addComponent(filter);
			toolbar.setWidth("100%");
			toolbar.setExpandRatio(filter, 1);
			toolbar.setComponentAlignment(filter, Alignment.TOP_RIGHT);
		}
		addComponent(toolbar);
	}
	
	protected void buildTable() {		
		table = new WexTable(jpaContainer, propertyConfiguror);
		table.setSizeFull();
		table.setSelectable(true);
		table.setImmediate(true);
		table.addListener(this);
		table.addActionHandler(this);
		
		addComponent(table);
		setExpandRatio(table, 1);
	}
	
	@Override
	public void attach() {
		setSizeFull();
	}
	
	@Override
	public void addEntity() {
		addEntity(null, UI.getCurrent());	
	}

	public void addEntity(T newInstance, UI ui) {
		GenericEditor<T> editor = newEditor();
		editor.setInstance(newInstance, jpaContainer);
		ui.addWindow(new WexWindow(editor));	
	}
	
	@Override
	public void editEntity(Object target) {
		GenericEditor<T> editor = newEditor();		
		EntityItem<T> item = (EntityItem<T>) table.getItem(target);		
		editor.setInstance(item.getEntity(), jpaContainer);
		UI.getCurrent().addWindow(new WexWindow(editor));		
	}
	
	@Override
	public void deleteEntity(final Object target) {
		EntityItem<T> item = (EntityItem<T>) table.getItem(target);
		final String text = item.getEntity().toString();
		ConfirmDialog.show(UI.getCurrent(), "Delete", text, "yes", "no", 
			new ConfirmDialog.Listener() {            
				private static final long serialVersionUID = 1L;
				public void onClose(ConfirmDialog dialog) {
					if (dialog.isConfirmed()) {
						jpaContainer.removeItem(target);
						Notification.show(
								text, "deleted...",
				                Notification.Type.TRAY_NOTIFICATION);
					}
				}
			});
	}

	@Override
	public void refreshContainer(boolean notify) {
		jpaContainer.refresh();
		super.refreshContainer(notify);
	}
		
	@Override
	public void entitySelected(ItemClickEvent event) {
		GenericEditor<T> editor = newEditor();
		EntityItem<T> t = (EntityItem<T>) event.getItem(); 
		editor.setInstance(t.getEntity(), jpaContainer);
		UI.getCurrent().addWindow(new WexWindow(editor));
	}
	
	public GenericEditor<T> newEditor() {
		return null;
	}

	public void setFilter(WexFilter filter) {
		this.filter = filter;
	}
	
	public WexFilter getFilter() {
		return this.filter;
	}
	
}