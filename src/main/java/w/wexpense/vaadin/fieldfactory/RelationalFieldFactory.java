package w.wexpense.vaadin.fieldfactory;

import java.util.HashMap;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.OneToMany;

import w.wexpense.model.Codable;
import w.wexpense.model.TransactionLine;
import w.wexpense.persistence.PersistenceUtils;

import com.vaadin.addon.jpacontainer.JPAContainer;
import com.vaadin.addon.jpacontainer.JPAContainerFactory;
import com.vaadin.addon.jpacontainer.fieldfactory.SingleSelectTranslator;
import com.vaadin.addon.jpacontainer.metadata.PropertyKind;
import com.vaadin.data.Container;
import com.vaadin.data.Item;
import com.vaadin.ui.AbstractSelect;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.DateField;
import com.vaadin.ui.Field;
import com.vaadin.ui.NativeSelect;

public class RelationalFieldFactory<T> extends SimpleFieldFactory {

	private static final long serialVersionUID = -2122739273213720235L;

	private final JPAContainer<T> jpaContainer;

	public RelationalFieldFactory(JPAContainer<T> jpaContainer) {
		super();

		this.jpaContainer = jpaContainer;
	}

	@Override
	protected Field createField(Item item, Class<?> type, Object propertyId, Component uiContext) {
		Field f = createRelationalField(item, type, propertyId, uiContext);
		if (f == null) {
			f = createSimpleField(type);
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
			case ONE_TO_MANY:				
				return oneToManyField(item, propertyId, uiContext);
			default:
				// fall to default for now
				return null;
		}
	}

	protected Field createManyToOneField(Item item, Class<?> type, Object propertyId, Component uiContext) {
		ComboBox select = new ComboBox();
		select.setMultiSelect(false);
		select.setContainerDataSource(getJpaContainer(type));
		select.setPropertyDataSource(new SingleSelectTranslator(select));
		select.setItemCaptionMode(NativeSelect.ITEM_CAPTION_MODE_ITEM);
		select.setFilteringMode(AbstractSelect.Filtering.FILTERINGMODE_CONTAINS);
		return select;
	}

	protected Field oneToManyField(Item item, Object propertyId, Component uiContext) {
		return new OneToManyField<>(jpaContainer, item, propertyId, TransactionLine.class);
	}

	protected Container getJpaContainer(Class<?> type) {
		EntityManager em = jpaContainer.getEntityProvider().getEntityManager();
		return JPAContainerFactory.make(type, em);
	}
}