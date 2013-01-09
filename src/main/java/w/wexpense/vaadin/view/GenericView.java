package w.wexpense.vaadin.view;

import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;

import w.wexpense.vaadin.ClosableWindow;
import w.wexpense.vaadin.PropertyConfiguror;
import w.wexpense.vaadin.WexJPAContainerFactory;

import com.vaadin.addon.jpacontainer.EntityItem;
import com.vaadin.addon.jpacontainer.JPAContainer;
import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.event.ItemClickEvent;
import com.vaadin.event.ItemClickEvent.ItemClickListener;
import com.vaadin.ui.AbstractLayout;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Component;
import com.vaadin.ui.ComponentContainer;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Table;
import com.vaadin.ui.VerticalLayout;

public class GenericView<T> extends VerticalLayout implements Button.ClickListener, ComponentContainer {
	private static final long serialVersionUID = 5282517667310057582L;
	
	@Autowired
	private WexJPAContainerFactory jpaContainerFactory;
	
	private JPAContainer<T> jpaContainer;
	private Class<T> entityClass;
	
	private AbstractLayout toolbar;
	private Button newButton;
	private Button deleteButton;
	private Button editButton;
	private Button refreshButton;
	private Table table;

	private Component filter;

	private PropertyConfiguror propertyConfiguror;
	
	public GenericView(Class<T> entityClass) {
		this.entityClass = entityClass;		
	}
	
	@PostConstruct
	public void init() {
		this.jpaContainer = jpaContainerFactory.getJPAContainer(entityClass, propertyConfiguror.getPropertyValues(PropertyConfiguror.nestedProperties));
		
		buildTable();
		buildToolbar();

		addComponent(toolbar);
		addComponent(table);
		setExpandRatio(table, 1);
		setSizeFull();
	}
	
	private void buildTable() {
		table = new Table(null, jpaContainer) {
		    protected String formatPropertyValue(Object rowId, Object colId, Property property) {
		        if (property == null || property.getValue() == null) {
		            return "";
		        }
		        if (Date.class.isAssignableFrom(property.getType())) {
		      	  return new SimpleDateFormat("dd/MM/yyyy").format(property.getValue());			      	  
		        }
		        if (Number.class.isAssignableFrom(property.getType())) {
		      	  return MessageFormat.format("{0,number,0.00}", property.getValue());
		        }
		        return property.toString();
		    }
		};
		table.setSelectable(true);
		table.setImmediate(true);
		table.setSizeFull();
		
		table.addListener(new Property.ValueChangeListener() {
			@Override
			public void valueChange(ValueChangeEvent event) {
				boolean modificationEnabled = event.getProperty().getValue() != null;
				
				deleteButton.setEnabled(modificationEnabled);
				editButton.setEnabled(modificationEnabled);
			}
		});
		table.addListener(new ItemClickListener() {
			@Override
			public void itemClick(ItemClickEvent event) {
				if (event.isDoubleClick()) {
					table.select(event.getItemId());

					EntityItem<T> t = (EntityItem<T>) event.getItem(); 
					editEntity(t.getEntity());
				}
			}
		});

		String[] propertyIds=propertyConfiguror.getPropertyValues(PropertyConfiguror.visibleProperties);
		table.setVisibleColumns(propertyIds);
		for(String pid: propertyIds) {
			String p = propertyConfiguror.getPropertyValue(pid + PropertyConfiguror.propertyAlignement);
			if (p!=null) table.setColumnAlignment(pid, p);
			p = propertyConfiguror.getPropertyValue(pid + PropertyConfiguror.propertyExpandRatio);
			if (p!=null) table.setColumnExpandRatio(pid, Float.valueOf(p));
		}
		
	}

	private void buildToolbar() {
		HorizontalLayout toolbar = new HorizontalLayout();

		newButton = new Button("Add");
		newButton.addListener(this);

		editButton = new Button("Edit");
		editButton.addListener(this);
		editButton.setEnabled(false);
		
		deleteButton = new Button("Delete");
		deleteButton.addListener(this);
		deleteButton.setEnabled(false);

		if (newEditor() != null) {
			toolbar.addComponent(newButton);
			toolbar.addComponent(editButton);
			toolbar.addComponent(deleteButton);
		}
		
		refreshButton = new Button("Refresh");
		refreshButton.addListener(this);
		toolbar.addComponent(refreshButton);
	
		if (filter !=null) {
			toolbar.addComponent(filter);
			toolbar.setWidth("100%");
			toolbar.setExpandRatio(filter, 1);
			toolbar.setComponentAlignment(filter, Alignment.TOP_RIGHT);
		}
		
		this.toolbar = toolbar;
	}

	@Override
	public String getCaption() {
		return entityClass.getSimpleName();
	}

	public void buttonClick(ClickEvent event) {
		if (event.getButton() == editButton) {
			EntityItem<T> item = (EntityItem<T>) table.getItem(table.getValue());
			editEntity(item.getEntity());
		} else if (event.getButton() == newButton) {
			editEntity(null);
		} else  if (event.getButton() == deleteButton) {
			deleteEntity();
		} else if (event.getButton() == refreshButton) {
			refreshContainer();
		}
	}

	protected void editEntity(T t) {		
		GenericEditor<T> editor = newEditor();
		editor.setInstance(t, jpaContainer);
		newWindow(editor);			
	}
	
	protected void deleteEntity() {
		jpaContainer.removeItem(table.getValue());
	}

	protected void newWindow(Component component) {
		getApplication().getMainWindow().addWindow(new ClosableWindow(component));
	}

	/**
	 * Method to refresh containers in this view. E.g. if a bidirectional
	 * reference is edited from the opposite direction or if we knew that an
	 * other user had edited visible values.
	 */
	public void refreshContainer() {
		jpaContainer.refresh();
	}

	public Class<T> getEntityClass() {
		return entityClass;
	}
	
	public JPAContainer<T> getJpaContainer() {
		return jpaContainer;
	}
	
	public GenericEditor<T> newEditor() {
		return null;
	}

	public void setFilter(Component filter) {
		this.filter = filter;
	}

	public void setPropertyConfiguror(PropertyConfiguror propertyConfiguror) {
		this.propertyConfiguror = propertyConfiguror;
	}
	
}