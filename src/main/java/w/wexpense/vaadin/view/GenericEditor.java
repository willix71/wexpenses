package w.wexpense.vaadin.view;

import java.util.Arrays;

import javax.annotation.PostConstruct;
import javax.persistence.EntityManager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import w.wexpense.persistence.PersistenceUtils;
import w.wexpense.vaadin.CloseViewEvent;
import w.wexpense.vaadin.PropertyConfiguror;
import w.wexpense.vaadin.WexJPAContainerFactory;
import w.wexpense.vaadin.fieldfactory.RelationalFieldFactory;

import com.vaadin.addon.beanvalidation.BeanValidationForm;
import com.vaadin.addon.jpacontainer.EntityItem;
import com.vaadin.addon.jpacontainer.JPAContainer;
import com.vaadin.data.util.BeanItem;
import com.vaadin.ui.AbstractOrderedLayout;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Form;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.VerticalLayout;

public class GenericEditor<T> extends VerticalLayout implements Button.ClickListener {
	private static final long serialVersionUID = 5282517667310057582L;
	
	private static final Logger LOGGER = LoggerFactory.getLogger(GenericEditor.class);

	@Autowired
	private WexJPAContainerFactory jpaContainerFactory;

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

	@PostConstruct
	protected void buildLayout() {
		buildForm();

		// build the buttons bar
		addComponent(buildButtons());
	}
		
	protected void buildForm() {
		form = new BeanValidationForm<T>(entityClass);		
		form.setWriteThrough(false);
		form.setImmediate(true);				
		addComponent(form);		
	}
	
	protected AbstractOrderedLayout buildButtons() {
		HorizontalLayout buttonLayout = new HorizontalLayout();
		saveButton = new Button("Save", (Button.ClickListener) this);
		cancelButton = new Button("Cancel", (Button.ClickListener) this);
		buttonLayout.addComponent(saveButton);
		buttonLayout.addComponent(cancelButton);
		return buttonLayout;
	}
	
	public void setInstance(T instance, JPAContainer<T> jpaContainer) {		
		this.jpaContainer = jpaContainer;
		if (instance == null) {
			this.isNew = true;
			this.item = new BeanItem<T>(PersistenceUtils.newInstance(entityClass));
		} else {
			this.isNew = false;

			// we need to refresh the instance else we can't follow lazy links because 
			// the instance is not attached to the current running entity manager
			EntityManager entityManager = jpaContainerFactory.getEntityManager();
			Object id = new BeanItem<T>(instance).getItemProperty(idProperty).getValue();
			T t = entityManager.find(entityClass, id);

			this.item = new BeanItem<T>(t);
		}			
		
		form.setFormFieldFactory(new RelationalFieldFactory<T>(this.propertyConfiguror, jpaContainer, jpaContainerFactory));				
		String[] propertyIds=propertyConfiguror.getPropertyValues(PropertyConfiguror.visibleProperties);		
		form.setItemDataSource(item, Arrays.asList(propertyIds));
		
		setCaption();
	}

	protected void setCaption() {
		String caption = entityClass.getSimpleName();
		if (!isNew) {
			Object o = item.getBean();
			if (o != null) {
				caption = o.toString();
			}
		}
		setCaption(caption);
	}
	
	@Override
	public void buttonClick(ClickEvent event) {
		if (event.getButton() == saveButton) {
			saveAndClose();
		} else if (event.getButton() == cancelButton) {
			cancelAndClose();
		}
	}

	protected void saveAndClose() {
		save();
		close();		
	}
	
	protected T save() {
		LOGGER.debug("Saving entity {}", item.getBean());
		
		form.commit();
		
		EntityManager em = jpaContainer.getEntityProvider().getEntityManager();
		em.getTransaction().begin();

		try {			
			T t = isNew ? insert(em) : update(em);
			em.getTransaction().commit();
			
			if (!isNew) {
				Object o = item.getItemProperty(idProperty).getValue();
				jpaContainer.refreshItem(o);
			}

			return t;
		} catch (Exception e) {
			LOGGER.error("Failed to save entity", e);
			em.getTransaction().rollback();
			return null;
		}
	}
	
	protected void cancelAndClose() {
		cancel();
		close();		
	}
	
	protected T insert(EntityManager em) {
		LOGGER.debug("Inserting {}", item.getBean());		

		Object tid = jpaContainer.addEntity(item.getBean());
		EntityItem<T> et = jpaContainer.getItem(tid);
		T t = et.getEntity();

		return t;
	}

	protected T update(EntityManager em) {
		LOGGER.debug("Merging {}", item.getBean());
		T t = item.getBean();
		t = em.merge(t);
		
		return t;
	}

	protected void cancel() {
		form.discard();
	}
	
	protected void close() {
		fireEvent(new CloseViewEvent(this));
	}

	public Form getForm() {
		return form;
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
