package w.wexpense.vaadin.fieldfactory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;

import javax.persistence.EntityManager;

import w.wexpense.model.DBable;
import w.wexpense.persistence.PersistenceUtils;
import w.wexpense.vaadin.TwoStepCommit;

import com.vaadin.addon.jpacontainer.EntityContainer;
import com.vaadin.addon.jpacontainer.EntityItem;
import com.vaadin.addon.jpacontainer.EntityItemProperty;
import com.vaadin.addon.jpacontainer.JPAContainer;
import com.vaadin.addon.jpacontainer.JPAContainerFactory;
import com.vaadin.addon.jpacontainer.fieldfactory.JPAContainerCustomField;
import com.vaadin.data.Container;
import com.vaadin.data.Container.Filter;
import com.vaadin.data.Item;
import com.vaadin.data.Validator.InvalidValueException;
import com.vaadin.data.util.BeanContainer;
import com.vaadin.data.util.BeanItem;
import com.vaadin.data.util.filter.Compare;
import com.vaadin.event.Action;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.Field;
import com.vaadin.ui.Table;
import com.vaadin.ui.TableFieldFactory;

/**
 * @author 
 *
 */
public class OneToManyFieldFactory implements CustomFieldFactory {

	private Class<?> childType;
	private String childPropertyId;

	private EntityManager entityManager;
	private TableFieldFactory fieldFactory;

	private Object[] visibleColumns;
	
	public OneToManyFieldFactory(EntityManager entityManager, Class<?> childType, Class<?> masterType, Object masterPropertyId) {
		this(entityManager, childType, PersistenceUtils.getMappedByProperty(masterType, masterPropertyId));
	}

	public OneToManyFieldFactory(EntityManager entityManager,Class<?> childType, String childPropertyId) {
		this.entityManager = entityManager;
		this.childType = childType;
		this.childPropertyId = childPropertyId;

		this.fieldFactory = new SimpleFieldFactory(JPAContainerFactory.make(childType, entityManager));
	}

	public void setVisibleColumns(Object[] visibleColumns) {
		this.visibleColumns = visibleColumns;
	}

	@Override
	public Field newInstance(Item item) {
		return new OneToManyField(item);
	}
	
	class OneToManyField extends JPAContainerCustomField implements Action.Handler, TwoStepCommit {
		private static final long serialVersionUID = 1698018766277514987L;

		final private Action add = new Action(getMasterDetailAddItemCaption());
		final private Action remove = new Action(getMasterDetailRemoveItemCaption());
		final private Action[] actions = new Action[] { add, remove };

		private Object masterEntity;
		private Container container;
		private Table table;

		public OneToManyField(Item item) {
			if (item instanceof BeanItem) {
				masterEntity = ((BeanItem) item).getBean();
				container = new BeanContainer(childType);
			} else {
				masterEntity = ((EntityItem) item).getEntity();
				container = getJpaContainer((EntityItem) item, false);
			}

			buildLayout();
		}

		protected JPAContainer getJpaContainer(EntityItem item, boolean buffered) {
			JPAContainer jpaContainer;
			if (buffered) {
				jpaContainer = JPAContainerFactory.make(childType, entityManager);
			} else {
				jpaContainer = JPAContainerFactory.makeBatchable(childType, entityManager);
			}

			Filter filter = new Compare.Equal(childPropertyId, masterEntity);
			jpaContainer.addContainerFilter(filter);

			// Set the lazy loading delegate to the same as the parent.
			EntityContainer referenceContainer = item.getContainer();
			jpaContainer.getEntityProvider().setLazyLoadingDelegate(referenceContainer.getEntityProvider().getLazyLoadingDelegate());
			return jpaContainer;
		}

		protected void buildLayout() {
			CssLayout vl = new CssLayout();
			buildTable();
			vl.addComponent(getTable());

			CssLayout buttons = new CssLayout();
			buttons.addComponent(new Button(getMasterDetailAddItemCaption(), new ClickListener() {
				private static final long serialVersionUID = 1L;
				public void buttonClick(ClickEvent event) {
					addNew();
				}
			}));
			buttons.addComponent(new Button(getMasterDetailRemoveItemCaption(), new ClickListener() {
				private static final long serialVersionUID = 1L;
				public void buttonClick(ClickEvent event) {
					remove(getTable().getValue());
				}
			}));
			vl.addComponent(buttons);

			setCompositionRoot(vl);
		}

		protected void buildTable() {
			table = new Table(null, container);
			Object[] visibleProperties = visibleColumns;
			if (visibleProperties == null) {
				List<Object> asList = new ArrayList<Object>(Arrays.asList(getTable().getVisibleColumns()));
				asList.remove(childPropertyId);
				visibleProperties = asList.toArray();
			}
			getTable().setPageLength(5);
			getTable().setVisibleColumns(visibleProperties);
			getTable().addActionHandler(this);

			getTable().setTableFieldFactory(fieldFactory);
			getTable().setEditable(true);
			getTable().setSelectable(true);
		}

		protected Table getTable() {
			return table;
		}

		protected String getMasterDetailRemoveItemCaption() {
			return "Remove";
		}

		protected String getMasterDetailAddItemCaption() {
			return "Add";
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * com.vaadin.addon.jpacontainer.fieldfactory.JPAContainerCustomField#
		 * getType ()
		 */
		@Override
		public Class<?> getType() {
			return childType;
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
				item.getItemProperty(childPropertyId).setValue(null);
				container.removeItem(itemId);
				// if (isWriteThrough()) {
				// Collection<?> collection = (Collection<?>)
				// getPropertyDataSource().getValue();
				// collection.remove(item.getEntity());
				// }
			}
		}

		private void addNew() {
			try {
				Object newInstance = childType.newInstance();
				BeanItem<?> beanItem = new BeanItem(newInstance);
				beanItem.getItemProperty(childPropertyId).setValue(masterEntity);
				if (container instanceof JPAContainer) {
					((JPAContainer) container).addEntity(newInstance);
				} else {
					((BeanContainer) container).addItem(((DBable) newInstance).getUid(), newInstance);
				}

			} catch (Exception e) {
				Logger.getLogger(getClass().getName()).warning("Could not instantiate detail instance " + childType.getName());
				e.printStackTrace();
			}
		}

		@Override
		public void commit() throws SourceException, InvalidValueException {
			if (!isWriteThrough() && container instanceof JPAContainer) {
				JPAContainer jpaConainer = (JPAContainer) container;
				// Update the original collection to contain up to date list of
				// referenced entities
				((EntityItemProperty) getPropertyDataSource()).getItem().refresh();
				Collection c = (Collection) getPropertyDataSource().getValue();
				boolean isNew = c == null;
				HashSet orphaned = !isNew ? new HashSet(c) : null;
				Collection itemIds = jpaConainer.getItemIds();
				for (Object object : itemIds) {
					EntityItem item = jpaConainer.getItem(object);
					Object entity = item.getEntity();
					if (!isNew) {
						orphaned.remove(entity);
					}
					if (c == null) {
						try {
							c = createNewCollectionForType(childType);
						} catch (InstantiationException e) {
							throw new SourceException(jpaConainer, e);
						} catch (IllegalAccessException e) {
							throw new SourceException(jpaConainer, e);
						}
					}
					if (isNew || !c.contains(entity)) {
						c.add(entity);
					}
				}
				if (!isNew) {
					c.removeAll(orphaned);
				}
				getPropertyDataSource().setValue(c);
			} else {
				super.commit();
			}
		}		
		
		@Override
		public void postCommit(EntityItem masterItem) {
			if (container instanceof BeanContainer) {
				JPAContainer jpaContainer = getJpaContainer(masterItem, false);
				for (Object itemId : container.getItemIds() ) {
					BeanItem item = (BeanItem) container.getItem(itemId);
					item.getItemProperty(childPropertyId).setValue(masterEntity);
					jpaContainer.addEntity(item.getBean());
				}
				jpaContainer.commit();
			}
		}
		
	   public Collection createNewCollectionForType(Class<?> type)
	         throws InstantiationException, IllegalAccessException {
	     if (type.isInterface()) {
	         if (type == Set.class) {
	             return new HashSet();
	         } else if (type == List.class) {
	             return new ArrayList();
	         } else {
	             throw new RuntimeException(
	                     "Couldn't instantiate a collection for property.");
	         }
	     } else {
	         return (Collection) type.newInstance();
	     }
	 }
	}
}