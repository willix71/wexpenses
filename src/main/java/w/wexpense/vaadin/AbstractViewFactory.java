package w.wexpense.vaadin;

import java.lang.reflect.ParameterizedType;

import javax.persistence.EntityManager;

import org.springframework.beans.factory.annotation.Autowired;

import w.wexpense.vaadin.fieldfactory.FieldFactoryHelper;

import com.vaadin.addon.jpacontainer.JPAContainer;
import com.vaadin.addon.jpacontainer.JPAContainerFactory;

public abstract class AbstractViewFactory<T> {
	
	@Autowired
	private EntityManager entityManager;
	
	private Class<T> entityClass;
	
	public AbstractViewFactory(Class<T> entityClass) {
		this.entityClass = entityClass;
	}
	
	@SuppressWarnings("unchecked")
   public AbstractViewFactory() {
		this.entityClass = (Class<T>) ((ParameterizedType) getClass()
            .getGenericSuperclass()).getActualTypeArguments()[0];
	}
	
	public EntityManager getEntityManager() {
		return entityManager;
	}

	public Class<T> getEntityClass() {
		return entityClass;
	}

	public String getProperty(String ...propertyKeys) {
		return FieldFactoryHelper.getProperty(entityClass, propertyKeys);
	}	
	
	public String[] getProperties(String ...propertyKeys) {
		return FieldFactoryHelper.getPropertyArray(entityClass, propertyKeys);
	}	
	
	public JPAContainer<T> buildJPAContainer() {
		JPAContainer<T> jpaContainer = JPAContainerFactory.make(entityClass, entityManager);
		String[] nestedProperties =  getProperties("nestedProperties");
		if (nestedProperties != null) {
			for(String nestedProperty:nestedProperties) {
				jpaContainer.addNestedContainerProperty(nestedProperty);
			}
		}
		return jpaContainer;
	}
}
