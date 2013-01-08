package w.wexpense.vaadin;

import java.util.Arrays;

import w.wexpense.vaadin.fieldfactory.SimpleFieldFactory;

import com.vaadin.addon.beanvalidation.BeanValidationForm;
import com.vaadin.addon.jpacontainer.EntityItem;
import com.vaadin.addon.jpacontainer.JPAContainer;
import com.vaadin.data.Item;
import com.vaadin.data.util.BeanItem;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Component;

public class GenericEditorFactory<T> extends AbstractViewFactory<T> {

	public GenericEditorFactory(Class<T> entityClass) {
		super(entityClass);
	}

	public Component newInstance(Item item) {
		return new GenericEditor(item);
	}

	class GenericEditor extends BeanValidationForm<T> implements Button.ClickListener {
		
		private final Item genericItem;

		private final JPAContainer<T> jpaContainer;

		private Button saveButton;
		private Button cancelButton;

		public GenericEditor(Item entityItem) {
			super(getEntityClass());
			
			if (entityItem == null) {
				genericItem = new BeanItem<T>(newInstance());
			} else {
				genericItem = entityItem;
			}
			
			jpaContainer = buildJPAContainer();
			
			setFormFieldFactory();
			setWriteThrough(false);
			setImmediate(true);
			setItemDataSource(genericItem,
					Arrays.asList(GenericEditorFactory.this
							.getVisibleProperties()));

			saveButton = new Button("Save", (Button.ClickListener) this);
			cancelButton = new Button("Cancel", (Button.ClickListener) this);

			getFooter().addComponent(saveButton);
			getFooter().addComponent(cancelButton);
		}

		protected void setFormFieldFactory() {
			setFormFieldFactory(new SimpleFieldFactory<T>(jpaContainer));
		}
		
		@Override
		public String getCaption() {
			if (genericItem instanceof EntityItem) {
				Object o = ((EntityItem<?>) genericItem).getEntity();
				if (o != null) {
					return o.toString();
				}
			}
			return getEntityClass().getSimpleName();
		}
		
		@Override
		public void buttonClick(ClickEvent event) {
			if (event.getButton() == saveButton) {
				save();
			} else if (event.getButton() == cancelButton) {
				cancel();
			}
			close();
		}

		@SuppressWarnings("unchecked")
      public EntityItem<T> save() {
			commit();
			if (genericItem instanceof BeanItem) {
				Object itemId = jpaContainer.addEntity(((BeanItem<T>) genericItem).getBean());
				return jpaContainer.getItem(itemId);
			}
			return (EntityItem<T>) genericItem;
		}
		
		public void cancel() {
			discard();
		}
		
		public void close() {
			fireEvent(new CloseViewEvent(this));
		}
		
		/**
		 * This method creates a new instance of the main entity type.
		 * 
		 * @return a new instance of the main entity type
		 * @throws InstantiationException
		 * @throws IllegalAccessException
		 */
		protected T newInstance() {
			T newInstance;
			try {
				newInstance = getEntityClass().newInstance();
				return newInstance;
			} catch (InstantiationException e) {
				throw new RuntimeException(e);
			} catch (IllegalAccessException e) {
				throw new RuntimeException(e);
			}
		}

		public Item getGenericItem() {
			return genericItem;
		}

		public JPAContainer<T> getJpaContainer() {
			return jpaContainer;
		}
		
	}
}
