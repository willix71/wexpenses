package w.wexpense.vaadin;

import javax.persistence.EntityManager;

import org.springframework.beans.factory.annotation.Autowired;

import com.vaadin.addon.jpacontainer.JPAContainer;
import com.vaadin.addon.jpacontainer.JPAContainerFactory;

public abstract class AbstractViewFactory<T> {

	@Autowired
	private EntityManager entityManager;
	
	private Class<T> entityClass;
	
	private Object[] visibleProperties;
 	
	private String[] nestedProperties;
	
	public AbstractViewFactory(Class<T> entityClass) {
		this.entityClass = entityClass;
	}
	
	public Object[] getVisibleProperties() {
		return visibleProperties;
	}
	
	public void setVisibleProperties(Object[] visibleProperties) {
		this.visibleProperties = visibleProperties;
	}

	public void setVisiblePropertiesList(String visibleProperties) {
		this.visibleProperties = visibleProperties.split(",");
	}

	public void setNestedPropertiesList(String nestedProperties) {
		this.nestedProperties = nestedProperties.split(",");
	}

	public EntityManager getEntityManager() {
		return entityManager;
	}

	public Class<T> getEntityClass() {
		return entityClass;
	}
	
	public JPAContainer<T> buildJPAContainer() {
		JPAContainer<T> jpaContainer = JPAContainerFactory.make(entityClass, entityManager);
		if (nestedProperties != null) {
			for(String nestedProperty:nestedProperties) {
				jpaContainer.addNestedContainerProperty(nestedProperty);
			}
		}
		return jpaContainer;
	}
}
