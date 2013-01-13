package w.wexpense.vaadin;

import javax.persistence.EntityManager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;

import com.vaadin.addon.jpacontainer.EntityProvider;
import com.vaadin.addon.jpacontainer.JPAContainer;
import com.vaadin.addon.jpacontainer.JPAContainerFactory;
//import com.vaadin.addon.jpacontainer.LazyLoadingDelegate;
import com.vaadin.addon.jpacontainer.util.EntityManagerPerRequestHelper;
//import com.vaadin.addon.jpacontainer.util.HibernateLazyLoadingDelegate;

public class WexJPAContainerFactory {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(WexApplication.class);

	@Value( "${jdbc.jpa.adapter}" ) 
	private String jpaAdapter;
	
	private EntityManagerPerRequestHelper helper;
	
	// private LazyLoadingDelegate lazyLoader = new HibernateLazyLoadingDelegate();
	
	public String getPersistenceUnit() {
		return "w.wexpense." + jpaAdapter;
	}
	
	public EntityManager getEntityManager() {
		String persistenceUnit = getPersistenceUnit();
		LOGGER.info("Creating EntityManager from persistence unit {}", persistenceUnit);
		return JPAContainerFactory.createEntityManagerForPersistenceUnit(getPersistenceUnit());
	}
	
	public <T> JPAContainer<T> getJPAContainer(Class<T> entityClass, String... nestedProperties) {
		String persistenceUnit = getPersistenceUnit();
		LOGGER.info("Creating JPAContainer from persistence unit {}", persistenceUnit);
		
		JPAContainer<T> jpac = JPAContainerFactory.make(entityClass, persistenceUnit);
		
		if (helper != null) helper.addContainer(jpac);

		//if (lazyLoader != null) jpac.getEntityProvider().setLazyLoadingDelegate(lazyLoader);
		
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
		//jpac.getEntityProvider().setLazyLoadingDelegate(entityProvider.getLazyLoadingDelegate());
		return jpac;
	}

	public EntityManagerPerRequestHelper getHelper() {
		return helper;
	}

	public void setHelper(EntityManagerPerRequestHelper helper) {
		this.helper = helper;
	}

}
