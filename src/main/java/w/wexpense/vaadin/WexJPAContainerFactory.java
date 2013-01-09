package w.wexpense.vaadin;

import com.vaadin.addon.jpacontainer.EntityProvider;
import com.vaadin.addon.jpacontainer.JPAContainer;
import com.vaadin.addon.jpacontainer.JPAContainerFactory;
import com.vaadin.addon.jpacontainer.util.EntityManagerPerRequestHelper;

public class WexJPAContainerFactory {

	public static final String PERSISTENCE_UNIT = "w.wexpense.eclipselink";
	//public static final String PERSISTENCE_UNIT = "w.wexpense.hibernate";
	
	private EntityManagerPerRequestHelper helper;
	
	public <T> JPAContainer<T> getJPAContainer(Class<T> entityClass, String... nestedProperties) {
		JPAContainer<T> jpac = JPAContainerFactory.make(entityClass, PERSISTENCE_UNIT);
		
		if (helper != null) helper.addContainer(jpac);

		if (nestedProperties != null) {
			for (String nestedProperty : nestedProperties) {
				jpac.addNestedContainerProperty(nestedProperty);
			}
		}
		return jpac;
	}
	
	public <T> JPAContainer<T> getJPAContainerFrom(EntityProvider<?> entityProvider, Class<T> entityClass, String... nestedProperties) {
		JPAContainer<T> jpac = JPAContainerFactory.makeBatchable(entityClass, entityProvider.getEntityManager());			

		if (helper != null) helper.addContainer(jpac);

		if (nestedProperties != null) {
			for (String nestedProperty : nestedProperties) {
				jpac.addNestedContainerProperty(nestedProperty);
			}
		}
		
		// Set the lazy loading delegate to the same as the parent.
		jpac.getEntityProvider().setLazyLoadingDelegate(entityProvider.getLazyLoadingDelegate());
		return jpac;
	}

	public EntityManagerPerRequestHelper getHelper() {
		return helper;
	}

	public void setHelper(EntityManagerPerRequestHelper helper) {
		this.helper = helper;
	}

}
