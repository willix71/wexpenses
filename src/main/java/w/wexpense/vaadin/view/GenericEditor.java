package w.wexpense.vaadin.view;

import java.util.Arrays;

import javax.persistence.EntityManager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import w.wexpense.persistence.PersistenceUtils;
import w.wexpense.vaadin.CloseViewEvent;
import w.wexpense.vaadin.PropertyConfiguror;
import w.wexpense.vaadin.fieldfactory.RelationalFieldFactory;

import com.vaadin.addon.beanvalidation.BeanValidationForm;
import com.vaadin.addon.jpacontainer.JPAContainer;
import com.vaadin.data.util.BeanItem;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.VerticalLayout;

public class GenericEditor<T> extends VerticalLayout implements Button.ClickListener {
	private static final long serialVersionUID = 5282517667310057582L;
	
	private static final Logger LOGGER = LoggerFactory.getLogger(GenericEditor.class);
	
	private Class<T> entityClass;
	private String idProperty;
	private JPAContainer<T> jpaContainer;
	private BeanItem<T> item;
	private boolean isNew;
	
	private BeanValidationForm<T> form;
	private Button saveButton;
	private Button cancelButton;

	private PropertyConfiguror propertyConfiguror;
	
	public GenericEditor(Class<T> entityClass) {
		this.entityClass = entityClass;
		this.idProperty = PersistenceUtils.getIdName(entityClass);			
	}

	public void setInstance(T instance, JPAContainer<T> jpaContainer) {
		isNew = instance == null;
		item = new BeanItem<T>(isNew?newInstance():instance);
		
		this.jpaContainer = jpaContainer;
		
		buildForm();
		buildExtra(); 
		buildButtons();
	}
	
	public void buildForm() {
		form = new BeanValidationForm<T>(entityClass);		
		form.setWriteThrough(false);
		form.setImmediate(true);
		
		String[] propertyIds=propertyConfiguror.getPropertyValues(PropertyConfiguror.visibleProperties);
		
		form.setFormFieldFactory(new RelationalFieldFactory<T>(jpaContainer));
		form.setItemDataSource(item, Arrays.asList(propertyIds));
		
		addComponent(form);
	}
	
	public void buildExtra() {
		
	}
	
	public void buildButtons() {
		HorizontalLayout buttonLayout = new HorizontalLayout();
		saveButton = new Button("Save", (Button.ClickListener) this);
		cancelButton = new Button("Cancel", (Button.ClickListener) this);
		buttonLayout.addComponent(saveButton);
		buttonLayout.addComponent(cancelButton);
		addComponent(buttonLayout);
	}
	
	@Override
	public String getCaption() {
		if (!isNew) {
			Object o = item.getBean();
			if (o != null) {
				return o.toString();
			}
		}
		return entityClass.getSimpleName();
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
		form.commit();
		
		EntityManager em = jpaContainer.getEntityProvider().getEntityManager();
		em.getTransaction().begin();
		T t = null;
		try {
			t = isNew ? insert(em) : update(em);
			em.getTransaction().commit();
		} catch (Exception e) {
			LOGGER.error("Failed to save entity", e);
			em.getTransaction().rollback();
		}

		if (t!=null && !isNew) {
			Object o = new BeanItem<>(t).getItemProperty(idProperty).getValue();
			jpaContainer.refreshItem(o);
		}
		return t;
	}
	
	protected T insert(EntityManager em) {
		LOGGER.debug("Inserting {}", item.getBean());		
		
		T t = item.getBean();
		em.persist(t);
		return t;
	}

	protected T update(EntityManager em) {
		LOGGER.debug("Merging {}", item.getBean());
		
		T t = em.merge(item.getBean());
		return t;
	}

	public void cancel() {
		form.discard();
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
			newInstance = entityClass.newInstance();
			return newInstance;
		} catch (InstantiationException e) {
			throw new RuntimeException(e);
		} catch (IllegalAccessException e) {
			throw new RuntimeException(e);
		}
	}

	public BeanItem<T> getItem() {
		return item;
	}

	public JPAContainer<T> getJpaContainer() {
		return jpaContainer;
	}

	public void setPropertyConfiguror(PropertyConfiguror propertyConfiguror) {
		this.propertyConfiguror = propertyConfiguror;
	}
}
