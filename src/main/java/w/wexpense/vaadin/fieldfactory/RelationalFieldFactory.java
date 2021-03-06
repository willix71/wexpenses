package w.wexpense.vaadin.fieldfactory;

import w.wexpense.model.Codable;
import w.wexpense.model.Selectable;
import w.wexpense.vaadin.PropertyConfiguror;
import w.wexpense.vaadin.WexJPAContainerFactory;

import com.vaadin.addon.jpacontainer.JPAContainer;
import com.vaadin.addon.jpacontainer.metadata.PropertyKind;
import com.vaadin.data.Container.Filter;
import com.vaadin.data.Item;
import com.vaadin.data.util.filter.Compare;
import com.vaadin.ui.Component;
import com.vaadin.ui.Field;

public class RelationalFieldFactory<T> extends SimpleFieldFactory {

	private static final long serialVersionUID = -2122739273213720235L;
	
	private Component caller;
	
	private WexJPAContainerFactory jpaContainerFactory;
	
	private final JPAContainer<T> jpaContainer;

	public RelationalFieldFactory(PropertyConfiguror propertyConfiguror, JPAContainer<T> jpaContainer, WexJPAContainerFactory jpaContainerFactory, Component caller) {
		super(propertyConfiguror);

		this.jpaContainer = jpaContainer;
		this.jpaContainerFactory = jpaContainerFactory;
		
		this.caller = caller;
	}

	@Override
	protected Field createField(Item item, Class<?> type, Object propertyId, Component uiContext) {
		Field f = createRelationalField(item, type, propertyId, uiContext);
		if (f == null) {
			f = createSimpleField(type, propertyId);
		}
		return f;
	}

	protected Field createRelationalField(Item item, Class<?> type, Object propertyId, Component uiContext) {
		if (Codable.class.isAssignableFrom(type)) {
			return createManyToOneField(item, type, propertyId, uiContext);
		}
		PropertyKind propertyKind = jpaContainer.getPropertyKind(propertyId);
		switch (propertyKind) {
			case MANY_TO_ONE:
				return createManyToOneField(item, type, propertyId, uiContext);
			default:
				// fall to default for now
				return null;
		}
	}

	protected Field createManyToOneField(Item item, final Class<?> type, Object propertyId, Component uiContext) {
		final JPAContainer<?> comboContainer = getJpaContainer(type, propertyId);
		return new WexComboBox(type, comboContainer, caller);		
	}

	protected JPAContainer<?> getJpaContainer(Class<?> type, Object propertyId) {
		JPAContainer<?> container = jpaContainerFactory.getJPAContainer(type);
		
		if (Selectable.class.isAssignableFrom(type) &&
				! Boolean.valueOf(getPropertyConfiguror().getPropertyValue(
						propertyId.toString() + PropertyConfiguror.propertyIncludeNonSelectable, "false"))) {
			Filter filter = new Compare.Equal("selectable", true);
			container.addContainerFilter(filter);
		}
		return container;
	}
}