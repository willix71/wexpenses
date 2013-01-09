package w.wexpense.vaadin;

import java.util.Arrays;

import javax.persistence.EntityManager;

import w.wexpense.vaadin.fieldfactory.RelationalFieldFactory;

import com.vaadin.addon.beanvalidation.BeanValidationForm;
import com.vaadin.addon.jpacontainer.EntityItem;
import com.vaadin.addon.jpacontainer.JPAContainer;
import com.vaadin.data.util.BeanItem;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Component;

public class GenericEditorFactory<T> extends AbstractViewFactory<T> {

	public GenericEditorFactory(Class<T> entityClass) {
		super(entityClass);
	}

	public Component newInstance(T entity) {
		return new GenericEditor(entity);
	}

	class GenericEditor extends BeanValidationForm<T> implements Button.ClickListener {
		private final JPAContainer<T> jpaContainer;
		
		private final BeanItem<T> item;
		
		private Button saveButton;
		private Button cancelButton;

		private boolean isNew;
		
		public GenericEditor(T entity) {
			super(getEntityClass());
			
			if (entity == null) {
				item = new BeanItem<T>(newInstance());
				isNew = true;
			} else {
				item = new BeanItem<T>(entity);
				isNew = false;
			}
			
			jpaContainer = buildJPAContainer();
			
			setFormFieldFactory(new RelationalFieldFactory<T>(jpaContainer));
			setWriteThrough(false);
			setImmediate(true);
			setItemDataSource(item, Arrays.asList(getProperties("editor","visibleProperties")));

			saveButton = new Button("Save", (Button.ClickListener) this);
			cancelButton = new Button("Cancel", (Button.ClickListener) this);

			getFooter().addComponent(saveButton);
			getFooter().addComponent(cancelButton);
		}

		@Override
		public String getCaption() {
			if (!isNew) {
				Object o = item.getBean();
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

		public T save() {
			commit();
			T t;
			if (isNew) {
				Object tid = jpaContainer.addEntity(item.getBean());
				EntityItem<T> et = jpaContainer.getItem(tid);
				t = et.getEntity();
			} else {
				EntityManager em = jpaContainer.getEntityProvider().getEntityManager();
				em.getTransaction().begin();
				t = em.merge(item.getBean());				
				em.getTransaction().commit();
			}
			return t;
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

		public JPAContainer<T> getJpaContainer() {
			return jpaContainer;
		}
	}
}
