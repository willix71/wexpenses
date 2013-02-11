package w.wexpense.vaadin.view;

import java.util.Arrays;

import javax.annotation.PostConstruct;
import javax.persistence.EntityManager;

import org.springframework.beans.factory.annotation.Autowired;

import w.wexpense.model.DBable;
import w.wexpense.persistence.PersistenceUtils;
import w.wexpense.vaadin.PropertyConfiguror;
import w.wexpense.vaadin.SelectionChangeEvent;
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

public class GenericEditor<T> extends ConfigurableView<T> implements Button.ClickListener {
	private static final long serialVersionUID = 5282517667310057582L;

	@Autowired
	private WexJPAContainerFactory jpaContainerFactory;

	private String idProperty;
	private JPAContainer<T> jpaContainer;
	private BeanItem<T> item;
	private boolean isNew;
	
	private BeanValidationForm<T> form;
	private Button saveButton;
	private Button saveAndCloseButton;
	
	public GenericEditor(Class<T> entityClass) {
		super(entityClass);
		this.idProperty = PersistenceUtils.getIdName(entityClass);
	}

	public GenericEditor(Class<T> entityClass, WexJPAContainerFactory jpaContainerFactory) {
		this(entityClass);
		this.jpaContainerFactory = jpaContainerFactory;
	}
	
	@PostConstruct
	protected void buildLayout() {
		buildForm();

		// build the buttons bar
		addComponent(buildButtons());
	}
		
	protected void buildForm() {
		form = new BeanValidationForm<T>(entityClass);		
		form.setWriteThrough(true);
		form.setImmediate(true);				
		addComponent(form);		
	}
	
	protected AbstractOrderedLayout buildButtons() {
		HorizontalLayout buttonLayout = new HorizontalLayout();
		saveButton = new Button("Save", (Button.ClickListener) this);
		saveAndCloseButton = new Button("Save&Close", (Button.ClickListener) this);
		buttonLayout.addComponent(saveButton);
		buttonLayout.addComponent(saveAndCloseButton);
		buttonLayout.addComponent(getCloseButton());
		return buttonLayout;
	}
	
	protected void setInstance(JPAContainer<T> jpaContainer) {		
		this.jpaContainer = jpaContainer;
		
		this.form.setFormFieldFactory(new RelationalFieldFactory<T>(propertyConfiguror, jpaContainer, jpaContainerFactory, this));				
	}

	protected void setInstance(T instance) {
		if (instance == null) {
			this.isNew = true;
			this.item = new BeanItem<T>(PersistenceUtils.newInstance(entityClass));
		} else if (DBable.class.isAssignableFrom(entityClass) && ((DBable) instance).isNew()){ 
			// special case where the new instance was created by the caller
			this.isNew = true;
			this.item = new BeanItem<T>(instance);
		} else {
			this.isNew = false;

			// we need to refresh the instance else we can't follow lazy links because 
			// the instance is not attached to the current running entity manager
			T t = reloadInstance(instance);

			this.item = new BeanItem<T>(t);
		}
		
		String[] propertyIds=propertyConfiguror.getPropertyValues(PropertyConfiguror.visibleProperties);
		form.setItemDataSource(item, Arrays.asList(propertyIds));
	}
		
	public T reloadInstance(T instance) {
		EntityManager entityManager = jpaContainerFactory.getEntityManager();
		Object id = new BeanItem<T>(instance).getItemProperty(idProperty).getValue();
		T t = entityManager.find(entityClass, id);
		return t;
	}	
	
	public void setInstance(T instance, JPAContainer<T> jpaContainer) {
		setInstance(jpaContainer);
		setInstance(instance);
	}

	@Override
	public String getTitle() {
		String title = entityClass.getSimpleName();
		if (!isNew) {
			Object o = item.getBean();
			if (o != null) {
				title = o.toString();
			}
		}
		return title;
	}
	
	@Override
	public void buttonClick(ClickEvent event) {
		if (event.getButton() == saveButton) {
			saveOnly();			
		} else if (event.getButton() == saveAndCloseButton) {
			saveAndClose();
		} 
	}
	
	protected void saveOnly() {
		T t = save();
		if (t == null) {
			LOGGER.error("\n\nHouston we have a problem!!!");
		}
		// set new value
		setInstance(t);
		
		getWindow().setCaption(getTitle());
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
				Object id = item.getItemProperty(idProperty).getValue();
				jpaContainer.refreshItem(id);
			}
			
			isNew = false;
			item = new BeanItem<T>(t);
			
			fireEvent(new SelectionChangeEvent(this, item.getItemProperty(idProperty).getValue()));

			return t;
		} catch (Exception e) {
			LOGGER.error("Failed to save entity", e);
			em.getTransaction().rollback();
			return null;
		}
	}
	
//	protected void cancelAndClose() {
//		cancel();
//		close();		
//	}
	
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

//	protected void cancel() {
//		form.discard();
//	}

	public Form getForm() {
		return form;
	}
	
	public BeanItem<T> getItem() {
		return item;
	}

//	public boolean isNew() {
//		return isNew;
//	}

	public JPAContainer<T> getJpaContainer() {
		return jpaContainer;
	}
}
