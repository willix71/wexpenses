package w.wexpense.vaadin.fieldfactory;

import java.util.Collection;

import javax.persistence.EntityManager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import w.wexpense.model.DBable;
import w.wexpense.persistence.PersistenceUtils;
import w.wexpense.vaadin.PropertyConfiguror;
import w.wexpense.vaadin.WexJPAContainerFactory;

import com.vaadin.addon.jpacontainer.JPAContainer;
import com.vaadin.data.Container;
import com.vaadin.data.Container.Filter;
import com.vaadin.data.Item;
import com.vaadin.data.util.BeanContainer;
import com.vaadin.data.util.BeanItem;
import com.vaadin.data.util.filter.Compare;
import com.vaadin.event.Action;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.Table;
import com.vaadin.ui.VerticalLayout;

public class OneToManyView<P extends DBable,C extends DBable> extends VerticalLayout implements Action.Handler {

	private static final long serialVersionUID = 1698018766277514987L;

	private static final Logger LOGGER = LoggerFactory.getLogger(OneToManyView.class);
	
	@Autowired
	private WexJPAContainerFactory jpaContainerFactory;
	
	private PropertyConfiguror propertyConfiguror;
	
	private Class<P> parentType;
	private Class<C> childType;
	private String parentPropertyId;
	private String childPropertyId;
	
	private RelationalFieldFactory<C> fieldFactory;
	
	private final Action add = new Action("Add");
	private final Action remove = new Action("Remove");
	private final Action[] actions = new Action[] { add, remove };

	private P parentEntity;
	private Container container;
	private Table table;
	
	public OneToManyView(String parentPropertyId, Class<C> childType) {
		this.childType = childType;
		this.parentPropertyId = parentPropertyId;
	}

	public void setInstance(P parentEntity, JPAContainer<P> parentJpaContainer) {
		this.parentType = parentJpaContainer.getEntityClass(); 						
		this.childPropertyId = PersistenceUtils.getMappedByProperty(parentType, parentPropertyId);

		JPAContainer<C> childJpaContainer = jpaContainerFactory.getJPAContainerFrom(parentJpaContainer.getEntityProvider(), this.childType);			
		this.fieldFactory = new RelationalFieldFactory<C>(childJpaContainer, jpaContainerFactory);
		
		if (parentEntity.isNew()) {
			BeanContainer<String, C> bContainer = new BeanContainer<String, C>(this.childType);
			bContainer.setBeanIdProperty("uid");
			
			container = bContainer;
		} else {
			Filter filter = new Compare.Equal(this.childPropertyId, parentEntity);
			childJpaContainer.addContainerFilter(filter);

			container = childJpaContainer;
		}

		buildLayout();
	}

	protected void buildLayout() {
		CssLayout vl = new CssLayout();
		table = buildTable();
		vl.addComponent(table);

		CssLayout buttons = new CssLayout();
		buttons.addComponent(new Button(add.getCaption(), new ClickListener() {
			private static final long serialVersionUID = 1L;
			public void buttonClick(ClickEvent event) {
				addNew();
			}
		}));
		buttons.addComponent(new Button(remove.getCaption(), new ClickListener() {
			private static final long serialVersionUID = 1L;
			public void buttonClick(ClickEvent event) {
				remove(table.getValue());
			}
		}));
		vl.addComponent(buttons);
		
		addComponent(vl);
	}

	protected Table buildTable() {
		Table tbl = new Table(null, container);
		
		Object[] visibleProperties = propertyConfiguror.getPropertyValues(PropertyConfiguror.visibleProperties);
		if (visibleProperties != null) {
			tbl.setVisibleColumns(visibleProperties);
		}
		
		tbl.setPageLength(5);			
		tbl.addActionHandler(this);

		tbl.setTableFieldFactory(this.fieldFactory);
		tbl.setEditable(true);
		tbl.setSelectable(true);
		
		return tbl;
	}
	
	public void handleAction(Action action, Object sender, Object target) {
		if (action == add) {
			addNew();
		} else {
			remove(target);
		}
	}

	public Action[] getActions(Object target, Object sender) {
		return actions;
	}

	private void remove(Object itemId) {
		if (itemId != null) {
			Item item = container.getItem(itemId);
			item.getItemProperty(this.childPropertyId).setValue(null);				
			container.removeItem(itemId);
		}
	}

	private void addNew() {
		try {
			C newInstance = this.childType.newInstance();
			BeanItem<C> beanItem = new BeanItem<C>(newInstance);
			beanItem.getItemProperty(this.childPropertyId).setValue(parentEntity);
			
			if (container instanceof JPAContainer) {
				((JPAContainer) container).addEntity(newInstance);
			} else {
				((BeanContainer) container).addItem(((DBable) newInstance).getUid(), newInstance);
			}

		} catch (Exception e) {
			LOGGER.warn("Could not instantiate detail instance " + this.childType.getName(), e);
			e.printStackTrace();
		}
	}

	public void insert(P parent, EntityManager em) {
		BeanContainer<String, C> bContainer = (BeanContainer<String, C>) container;			
		Collection<String> itemIds = bContainer.getItemIds();
		
		LOGGER.debug("Inserting {} children", itemIds.size());
		
		for (Object object : itemIds) {
			BeanItem<C> childBean =  bContainer.getItem(object);
			childBean.getItemProperty(this.childPropertyId).setValue(parent);
			em.persist(childBean.getBean());				 
		}
	}
	
	public void update(P parent, EntityManager em) {
		JPAContainer<C> jpaContainer = (JPAContainer<C>) container;
		jpaContainer.commit();
	}
	
	public void setPropertyConfiguror(PropertyConfiguror propertyConfiguror) {
		this.propertyConfiguror = propertyConfiguror;
	}

	public Container getContainer() { return container; }
}