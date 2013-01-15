package w.wexpense.vaadin.view;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import w.wexpense.model.DBable;
import w.wexpense.persistence.PersistenceUtils;
import w.wexpense.vaadin.PropertyConfiguror;
import w.wexpense.vaadin.WexJPAContainerFactory;
import w.wexpense.vaadin.fieldfactory.RelationalFieldFactory;

import com.vaadin.addon.jpacontainer.JPAContainer;
import com.vaadin.data.Container;
import com.vaadin.data.Item;
import com.vaadin.data.Property;
import com.vaadin.data.util.BeanItem;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.event.Action;
import com.vaadin.ui.TableFieldFactory;

public class OneToManySubEditor<C extends DBable, P extends DBable> extends AbstractView<C> {

	private static final long serialVersionUID = 1698018766277514987L;

	private static final Logger LOGGER = LoggerFactory.getLogger(OneToManySubEditor.class);

	@PersistenceContext
	private EntityManager entityManager;
	
	@Autowired
	protected WexJPAContainerFactory jpaContainerFactory;
	
	private Class<P> parentType;
	private String parentPropertyId;
	protected P parentEntity;
	
	private String childPropertyId;
	private BeanItemContainer<C> childContainer;
	private TableFieldFactory fieldFactory;
	
	private boolean editable = true;
	
	private Set<C> toDelete = new HashSet<C>();
	private Set<C> toRemove = new HashSet<C>();
	
	public OneToManySubEditor(Class<C> childType, String parentPropertyId) {
		super(childType);
		this.parentPropertyId = parentPropertyId;
		
		super.entitySelectedActions = new Action[] { addAction, removeAction };
		super.noEntitySelectedActions = new Action[] { addAction };
	}

	public void setInstance(P parentEntity, JPAContainer<P> parentJpaContainer) {
		this.parentEntity = parentEntity;
		this.parentType = parentJpaContainer.getEntityClass(); 						
		this.childPropertyId = PersistenceUtils.getMappedByProperty(parentType, parentPropertyId);

		JPAContainer<C> childJpaContainer = jpaContainerFactory.getJPAContainerFrom(parentJpaContainer.getEntityProvider(), this.entityClass);			
		this.fieldFactory = getTableFieldFactory(childJpaContainer, jpaContainerFactory);
		
		buildTable();

		// populate the container
		Collection<C> children = (Collection<C>) new BeanItem<P>(parentEntity).getItemProperty(parentPropertyId).getValue();
		if (children != null && !children.isEmpty()) {
			childContainer.addAll(children);
		}
		
		addComponent(table);
	}

//	protected void buildLayout() {
//		CssLayout vl = new CssLayout();
//		
//		// build the table
//		buildTable();
//		vl.addComponent(table);
//	
//		addComponent(vl);
//	}
	
	protected TableFieldFactory getTableFieldFactory(JPAContainer<C> childJpaContainer, WexJPAContainerFactory jpaContainerFactory) {
		return new RelationalFieldFactory<C>(this.propertyConfiguror, childJpaContainer, jpaContainerFactory);
	}
	
	@Override
	protected void initTable() {
		childContainer = new BeanItemContainer<C>(entityClass) {
			@Override
			public Property getContainerProperty(Object itemId, Object propertyId) {
				// TODO figure a way to get Nested properties out of a BeanItemContainer
				if (propertyId != null && propertyId.toString().contains(".")) {
					// int index = propertyId.toString().indexOf(".");
					// Property p = super.getContainerProperty(itemId, propertyId.toString().substring(0, index));
					// BeanItem.getPropertyDescriptors(p.getType() );
					LOGGER.info("This will fail " + propertyId);
				}
				
				return super.getContainerProperty(itemId, propertyId);
			}
		};
		String[] nestedProperties = propertyConfiguror.getPropertyValues(PropertyConfiguror.visibleProperties);
		if (nestedProperties != null) {
			for (String nestedProperty : nestedProperties) {
				childContainer.addNestedContainerProperty(nestedProperty);
			}
		}
		table = new WexTable(childContainer, propertyConfiguror);
	}
	
	@Override
	protected void buildTable() {
		super.buildTable();
		table.setPageLength(5);			
		table.setEditable(editable);
		table.setTableFieldFactory(this.fieldFactory);		
	}

	@Override
	public void addEntity() {
		C newInstance = PersistenceUtils.newInstance(entityClass);
		BeanItem<C> beanItem = new BeanItem<C>(newInstance);
		beanItem.getItemProperty(this.childPropertyId).setValue(parentEntity);
		childContainer.addBean(newInstance); 
	}
	
	@Override
	public void removeEntity(Object target) {
		C c = (C) table.getValue();
		Item item = childContainer.getItem(c);
		item.getItemProperty(this.childPropertyId).setValue(null);
		if (!c.isNew()) {
			toRemove.add(c);
		}
		childContainer.removeItem(c);
	}
	
	@Override
	public void deleteEntity(Object target) {
		C c = (C) table.getValue();
		if (!c.isNew()) {
			toDelete.add(c);
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
		for(C child: toRemove) {
			em.merge(child);
		}
		for(C child: toDelete) {
			em.remove(em.merge(child));
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

	public Container getChildContainer() {
		return childContainer;
	}
}