package w.wexpense.service;

import java.util.Collection;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.vaadin.addon.jpacontainer.JPAContainer;
import com.vaadin.addon.jpacontainer.JPAContainerFactory;
import com.vaadin.addon.jpacontainer.util.EntityManagerPerRequestHelper;

@Service
public class PersistenceService {

	private static final Logger LOGGER = LoggerFactory.getLogger(PersistenceService.class);

	@PersistenceContext
	private EntityManager entityManager;

	private EntityManagerPerRequestHelper helper;
	
	public <T> JPAContainer<T> getJPAContainer(Class<T> entityClass, String... nestedProperties) {
		LOGGER.info("Creating JPAContainer for {}", entityClass);
		
		JPAContainer<T> jpac = JPAContainerFactory.make(entityClass, entityManager);

		if (nestedProperties != null) {
			for (String nestedProperty : nestedProperties) {
				jpac.addNestedContainerProperty(nestedProperty);
			}
		}
		
		if (helper != null) helper.addContainer(jpac);

		return jpac;
	}
	
	public <T> JPAContainer<T> getJPAContainer(Class<T> entityClass, Collection<String> nestedProperties) {
		LOGGER.info("Creating JPAContainer for {}", entityClass);
		
		JPAContainer<T> jpac = JPAContainerFactory.make(entityClass, entityManager);

		if (nestedProperties != null) {
			for (String nestedProperty : nestedProperties) {
				jpac.addNestedContainerProperty(nestedProperty);
			}
		}
		
		if (helper != null) helper.addContainer(jpac);

		return jpac;
	}
	
	public EntityManagerPerRequestHelper getHelper() {
		return helper;
	}

	public void setHelper(EntityManagerPerRequestHelper helper) {
		this.helper = helper;
	}

}
