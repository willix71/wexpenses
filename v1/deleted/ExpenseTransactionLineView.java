package w.wexpense.vaadin.view;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;

import w.wexpense.model.DBable;
import w.wexpense.persistence.PersistenceUtils;
import w.wexpense.vaadin.fieldfactory.RelationalFieldFactory;

import com.vaadin.addon.jpacontainer.EntityContainer;
import com.vaadin.addon.jpacontainer.EntityItem;
import com.vaadin.addon.jpacontainer.EntityItemProperty;
import com.vaadin.addon.jpacontainer.JPAContainer;
import com.vaadin.addon.jpacontainer.JPAContainerFactory;
import com.vaadin.addon.jpacontainer.fieldfactory.JPAContainerCustomField;
import com.vaadin.data.Container;
import com.vaadin.data.Item;
import com.vaadin.data.Buffered.SourceException;
import com.vaadin.data.Container.Filter;
import com.vaadin.data.Validator.InvalidValueException;
import com.vaadin.data.util.BeanContainer;
import com.vaadin.data.util.BeanItem;
import com.vaadin.data.util.filter.Compare;
import com.vaadin.event.Action;
import com.vaadin.ui.Button;
import com.vaadin.ui.ComponentContainer.ComponentAttachEvent;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.Table;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.VerticalLayout;

class ExpenseTransactionLineView<P extends DBable,C extends DBable> extends VerticalLayout implements Action.Handler {

		private static final long serialVersionUID = 1698018766277514987L;
		
		private final Class<P> parentType;
		private final Class<C> childType;
		private final String parentPropertyId;
		private final String childPropertyId;
		
		private JPAContainer<P> parentJpaContainer;
		private RelationalFieldFactory<C> fieldFactory;
		
		final private Action add = new Action("Add");
		final private Action remove = new Action("Remove");
		final private Action[] actions = new Action[] { add, remove };

		private P parentEntity;
		private Container container;
		private Table table;

		public ExpenseTransactionLineView(P parentEntity, JPAContainer<P> parentJpaContainer, String parentPropertyId, Class<C> childType) {
			this.parentJpaContainer = parentJpaContainer;
			this.parentType = parentJpaContainer.getEntityClass();
			this.parentPropertyId = parentPropertyId;
			
			this.childPropertyId = PersistenceUtils.getMappedByProperty(parentType, parentPropertyId);
			this.childType = childType;

			JPAContainer<C> childJpaContainer = getJpaContainer();
			this.fieldFactory = new RelationalFieldFactory<C>(childJpaContainer);
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

		protected JPAContainer<C> getJpaContainer() {
			JPAContainer<C> jpaContainer = JPAContainerFactory.makeBatchable(this.childType, this.parentJpaContainer.getEntityProvider().getEntityManager());			

			// Set the lazy loading delegate to the same as the parent.
			jpaContainer.getEntityProvider().setLazyLoadingDelegate(this.parentJpaContainer.getEntityProvider().getLazyLoadingDelegate());
			return jpaContainer;
		}

		protected void buildLayout() {
			CssLayout vl = new CssLayout();
			buildTable();
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

		protected void buildTable() {
			table = new Table(null, container);
			Object[] visibleProperties = null;
			if (visibleProperties == null) {
				List<Object> asList = new ArrayList<Object>(Arrays.asList(new Object[]{"amount", "factor", "account"}));
				asList.remove(this.childPropertyId);
				visibleProperties = asList.toArray();
			}
			table.setPageLength(5);
			table.setVisibleColumns(visibleProperties);
			table.addActionHandler(this);

			table.setTableFieldFactory(this.fieldFactory);
			table.setEditable(true);
			table.setSelectable(true);
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
				Logger.getLogger(getClass().getName()).warning("Could not instantiate detail instance " + this.childType.getName());
				e.printStackTrace();
			}
		}

//		@Override
//		public void commit() throws SourceException, InvalidValueException {
//			if (!isWriteThrough() && container instanceof JPAContainer) {
//				JPAContainer jpaConainer = (JPAContainer) container;
//				// Update the original collection to contain up to date list of
//				// referenced entities
//				((EntityItemProperty) getPropertyDataSource()).getItem().refresh();
//				Collection c = (Collection) getPropertyDataSource().getValue();
//				boolean isNew = c == null;
//				HashSet orphaned = !isNew ? new HashSet(c) : null;
//				Collection itemIds = jpaConainer.getItemIds();
//				for (Object object : itemIds) {
//					EntityItem item = jpaConainer.getItem(object);
//					Object entity = item.getEntity();
//					if (!isNew) {
//						orphaned.remove(entity);
//					}
//					if (c == null) {
//						try {
//							c = createNewCollectionForType(this.childType);
//						} catch (InstantiationException e) {
//							throw new SourceException(jpaConainer, e);
//						} catch (IllegalAccessException e) {
//							throw new SourceException(jpaConainer, e);
//						}
//					}
//					if (isNew || !c.contains(entity)) {
//						c.add(entity);
//					}
//				}
//				if (!isNew) {
//					c.removeAll(orphaned);
//				}
//				getPropertyDataSource().setValue(c);
//			} else {
//				super.commit();
//			}
//		}		
		
//	public Collection createNewCollectionForType(Class<?> type)
//			throws InstantiationException, IllegalAccessException {
//		if (type.isInterface()) {
//			if (type == Set.class) {
//				return new HashSet();
//			} else if (type == List.class) {
//				return new ArrayList();
//			} else {
//				throw new RuntimeException(
//						"Couldn't instantiate a collection for property.");
//			}
//		} else {
//			return (Collection) type.newInstance();
//		}
//	}
}