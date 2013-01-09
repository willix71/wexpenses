package w.wexpense.vaadin;

import com.vaadin.addon.jpacontainer.JPAContainer;
import com.vaadin.addon.jpacontainer.JPAContainerFactory;
import com.vaadin.addon.jpacontainer.util.EntityManagerPerRequestHelper;

public class WexJPAContainerFactory {

	private EntityManagerPerRequestHelper helper;

	public <T> JPAContainer<T> getJPAContainer(Class<T> entityClass, String... nestedProperties) {
		JPAContainer<T> jpac = JPAContainerFactory.make(entityClass, "w.vaadin.jpacontainer");
		
		if (helper != null) helper.addContainer(jpac);

		if (nestedProperties != null) {
			for (String nestedProperty : nestedProperties) {
				jpac.addNestedContainerProperty(nestedProperty);
			}
		}
		return jpac;
	}

	public void setHelper(EntityManagerPerRequestHelper helper) {
		this.helper = helper;
	}

}
