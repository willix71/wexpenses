package w.wexpense.vaadin.view;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.annotation.PostConstruct;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import w.wexpense.model.DBable;
import w.wexpense.persistence.PersistenceUtils;
import w.wexpense.vaadin.SelectionChangeEvent;
import w.wexpense.vaadin.WexWindow;
import w.wexpense.vaadin.PropertyConfiguror;
import w.wexpense.vaadin.WexJPAContainerFactory;
import w.wexpense.vaadin.fieldfactory.RelationalFieldFactory;

import com.vaadin.addon.jpacontainer.EntityItem;
import com.vaadin.addon.jpacontainer.JPAContainer;
import com.vaadin.data.Item;
import com.vaadin.data.Property;
import com.vaadin.data.util.BeanItem;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.event.Action;
import com.vaadin.ui.Component;
import com.vaadin.ui.TableFieldFactory;
import com.vaadin.ui.Component.Event;
import com.vaadin.ui.Window.CloseEvent;
import com.vaadin.ui.Window.CloseListener;

public class OneToManySubEditor<C extends DBable, P extends DBable> extends AbstractView<C> {

	private static final long serialVersionUID = 1698018766277514987L;

	private static final Logger LOGGER = LoggerFactory.getLogger(OneToManySubEditor.class);

	@PersistenceContext
	private EntityManager entityManager;
	
	@Autowired
	protected WexJPAContainerFactory jpaContainerFactory;
	
	private String parentPropertyId;
	protected P parentEntity;
	
	private String childPropertyId;
	private BeanItemContainer<C> childContainer;
	private TableFieldFactory fieldFactory;
	
	private boolean editable = true;
	
	private enum PersistAction { REMOVE, DELETE }
	private Map<C, PersistAction> persistEntities = new HashMap<C, PersistAction>();
	
	public OneToManySubEditor(Class<C> childType, Class<P> parentType, String parentPropertyId) {
		super(childType);
		this.parentPropertyId = parentPropertyId;
		this.childPropertyId = PersistenceUtils.getMappedByProperty(parentType, parentPropertyId);
		
		super.entitySelectedActions = new Action[] { addAction, removeAction };
		super.noEntitySelectedActions = new Action[] { addAction };
	}

	@PostConstruct
	public void build() {									
		this.childContainer = new BeanItemContainer<C>(entityClass);
	
		JPAContainer<C> childJpaContainer = jpaContainerFactory.getJPAContainerBatch(this.entityClass);			
		this.fieldFactory = getTableFieldFactory(childJpaContainer, jpaContainerFactory);	
		
		buildTable();
	}

	protected TableFieldFactory getTableFieldFactory(JPAContainer<C> childJpaContainer, WexJPAContainerFactory jpaContainerFactory) {
		return new RelationalFieldFactory<C>(this.propertyConfiguror, childJpaContainer, jpaContainerFactory, this);
	}
	
	protected void buildTable() {
		table = new WexTable(childContainer, propertyConfiguror);
		table.setSizeFull();
		table.setSelectable(true);
		table.setImmediate(true);
		table.addListener(this);
		table.addActionHandler(this);

		table.setPageLength(5);			
		table.setEditable(editable);		
		table.setTableFieldFactory(fieldFactory);		
		
		addComponent(table);
		setExpandRatio(table, 1);
	}
		
	public void setInstance(P parentEntity) {
		this.parentEntity = parentEntity;
		
		// populate the container
		Collection<C> children = (Collection<C>) new BeanItem<P>(parentEntity).getItemProperty(parentPropertyId).getValue();
		childContainer.removeAllItems();
		if (children != null && !children.isEmpty()) {
			childContainer.addAll(children);
		}
	}



	@Override
	public void attach() {
		setSizeFull();
	}
	
	public C newEntity() {
		C newInstance = PersistenceUtils.newInstance(entityClass);
		BeanItem<C> beanItem = new BeanItem<C>(newInstance);
		beanItem.getItemProperty(this.childPropertyId).setValue(parentEntity);
		return newInstance;
	}
	
	@Override
	public void addEntity() {
		final GenericView<C> view = newView();

		if (view != null) {
			view.table.setMultiSelect(true);
			view.addListener(new Component.Listener() {
				private static final long serialVersionUID = 8121179082149508635L;
				
				@Override
				public void componentEvent(Event event) {
					if (event instanceof SelectionChangeEvent && event.getComponent()==view) {
						for(Object id : ((SelectionChangeEvent) event).getIds()) {
							EntityItem<C> item = (EntityItem<C>) view.table.getItem(id);
							C c = item.getEntity();
							childContainer.addBean(c);
						}
					} 
				}
			});
			
			WexWindow window = new WexWindow(view);
			window.setModal(true);
			getApplication().getMainWindow().addWindow(window);	
		}
	}
	
	@Override
	public void removeEntity(Object target) {
		C c = (C) table.getValue();
		Item item = childContainer.getItem(c);
		item.getItemProperty(this.childPropertyId).setValue(null);
		if (!c.isNew()) {
			persistEntities.put(c, PersistAction.REMOVE);
		}
		childContainer.removeItem(c);
	}
	
	@Override
	public void deleteEntity(Object target) {
		C c = (C) table.getValue();
		if (!c.isNew()) {
			persistEntities.put(c, PersistAction.DELETE);
		}
		childContainer.removeItem(table.getValue());
	}

	public void save(P parent, EntityManager em) {
		LOGGER.debug("Saving {} children of ", parent);
		
		for (Object object : childContainer.getItemIds()) {
			BeanItem<C> childBean =  childContainer.getItem(object);
			childBean.getItemProperty(this.childPropertyId).setValue(parent);
			C child = childBean.getBean();
			if (child.isNew()) {
				em.persist(child);				 
			} else {
				em.merge(child);
			}
		}
		
		for(Map.Entry<C, PersistAction> entry: persistEntities.entrySet()) {
			switch(entry.getValue()) {
			case REMOVE:
				em.merge(em.merge(entry.getKey()));
				break;
			case DELETE:
				em.remove(em.merge(entry.getKey()));
				break;
			}
		}
	}
	
	public boolean isEditable() {
		return editable;
	}

	public void setEditable(boolean editable) {
		this.editable = editable;
	}

	public String getChildPropertyId() {
		return childPropertyId;
	}

	public BeanItemContainer<C> getChildContainer() {
		return childContainer;
	}
	
	public GenericView<C> newView() {
		return null;
	}
}