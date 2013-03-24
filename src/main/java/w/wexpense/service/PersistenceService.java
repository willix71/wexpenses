package w.wexpense.service;

import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import w.wexpense.vaadin7.view.TableColumnConfig;

import com.vaadin.addon.jpacontainer.JPAContainer;
import com.vaadin.addon.jpacontainer.JPAContainerFactory;
import com.vaadin.addon.jpacontainer.util.EntityManagerPerRequestHelper;
import com.vaadin.data.Container;

//@Service
public class PersistenceService {

	private static final Logger LOGGER = LoggerFactory.getLogger(PersistenceService.class);

	@PersistenceContext
	private EntityManager entityManager;

	private EntityManagerPerRequestHelper helper;
	
	public Container refresh(Container container) {
		((JPAContainer) container).refresh();
		return container;
	}
	
	private <T> JPAContainer<T> getJPAContainer(Class<T> entityClass) {
		LOGGER.info("Creating JPAContainer for {}", entityClass);
		
		JPAContainer<T> jpac = JPAContainerFactory.make(entityClass, entityManager);
		
		if (helper != null) helper.addContainer(jpac);

		return jpac;
	}
	
	public <T> Container getContainer(Class<T> entityClass) {
		return getJPAContainer(entityClass);
	}
	
	public <T> Container getContainer(Class<T> entityClass, String... nestedProperties) {
		JPAContainer<T> jpac = getJPAContainer(entityClass);

		if (nestedProperties != null) {
			for (String nestedProperty : nestedProperties) {
				jpac.addNestedContainerProperty(nestedProperty);
			}
		}
	
		return jpac;
	}
	
	public <T> Container getContainer(Class<T> entityClass, TableColumnConfig... configs) {	
		JPAContainer<T> jpac = getJPAContainer(entityClass);

		if (configs != null) {
			for (String nestedProperty : TableColumnConfig.getNestedProperties(configs)) {
				jpac.addNestedContainerProperty(nestedProperty);
			}
	
			for(Map.Entry<String, String> p: TableColumnConfig.getSortProperties(configs).entrySet()) {
				jpac.setSortProperty(p.getKey(), p.getValue());
			}
			
			TableColumnConfig.sort(jpac, configs);
		}

		return jpac;
	}


	public EntityManagerPerRequestHelper getHelper() {
		return helper;
	}

	public void setHelper(EntityManagerPerRequestHelper helper) {
		this.helper = helper;
	}

}
