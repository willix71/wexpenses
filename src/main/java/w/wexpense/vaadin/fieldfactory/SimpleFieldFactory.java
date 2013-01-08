package w.wexpense.vaadin.fieldfactory;

import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;

import w.wexpense.model.Codable;
import w.wexpense.model.DBable;
import w.wexpense.vaadin.WexApplication;

import com.vaadin.addon.jpacontainer.JPAContainer;
import com.vaadin.addon.jpacontainer.JPAContainerFactory;
import com.vaadin.addon.jpacontainer.fieldfactory.SingleSelectTranslator;
import com.vaadin.addon.jpacontainer.metadata.PropertyKind;
import com.vaadin.data.Container;
import com.vaadin.data.Item;
import com.vaadin.ui.AbstractSelect;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.DateField;
import com.vaadin.ui.DefaultFieldFactory;
import com.vaadin.ui.Field;
import com.vaadin.ui.Form;
import com.vaadin.ui.NativeSelect;
import com.vaadin.ui.TextField;

public class SimpleFieldFactory<T> extends DefaultFieldFactory {

	private static final long serialVersionUID = -2122739273213720235L;

	private static final Object[] systemProperties = {"id", "version", "fullId", "uid", "modifiedTs", "createdTs"} ;

	public static boolean isSystemProperty(Object propertyId) {
		for(Object o: systemProperties) { 
			if (o.equals(propertyId)) return true;
		}
		return false;		
	}
	
	private final JPAContainer<T> jpaContainer;

	private Map<Object, CustomFieldFactory> customFieldFactories = new HashMap<Object, CustomFieldFactory>();

	private int dateResolution = DateField.RESOLUTION_DAY;
	private String dateFormat = "dd/MM/yyyy";

	public SimpleFieldFactory(JPAContainer<T> jpaContainer) {
		super();
		this.jpaContainer = jpaContainer;
	}

	@Override
	public Field createField(Item item, Object propertyId, Component uiContext) {
		Field field;

		CustomFieldFactory factory = customFieldFactories.get(propertyId);
		if (factory != null) {
			field = factory.newInstance(item);
		} else {
			Class<?> type = item.getItemProperty(propertyId).getType();
			field = createField(type, propertyId);
		}

		field.setCaption(createCaptionByPropertyId(propertyId));
		if (isSystemProperty(propertyId)) {
			field.setReadOnly(true);
		}
		return field;
	}

	@Override
	public Field createField(Container container, Object itemId, Object propertyId, Component uiContext) {
		Field field;

		Item item = container.getItem(itemId);
		
		CustomFieldFactory factory = customFieldFactories.get(propertyId);
		if (factory != null) {
			field = factory.newInstance(item);
		} else {
			Class<?> type = item.getItemProperty(propertyId).getType();
			field = createField(type, propertyId);
		}

		field.setCaption(createCaptionByPropertyId(propertyId));
		return field;
	}

	protected Field createField(Class<?> type, Object propertyId) {
		// Null typed properties can not be edited
		if (type == null) {
			return null;
		}

		// Item field
		if (Item.class.isAssignableFrom(type)) {
			return new Form();
		}

		// Date field
		if (Date.class.isAssignableFrom(type)) {
			final DateField dateField = new DateField();
			dateField.setResolution(dateResolution);
			dateField.setDateFormat(dateFormat);
			return dateField;
		}

		// Boolean field
		if (Boolean.class.isAssignableFrom(type)) {
			return new CheckBox();
		}

		// Enum field
		if (type.isEnum()) {
			ComboBox select = new ComboBox();
			select.setFilteringMode(AbstractSelect.Filtering.FILTERINGMODE_STARTSWITH);
			List<?> asList = Arrays.asList(type.getEnumConstants());
			for (Object object : asList) {
				select.addItem(object);
			}
			return select;
		}

		if (Codable.class.isAssignableFrom(type)) {
			return createManyToOneField(type);
		}

		if (DBable.class.isAssignableFrom(type)) {
			PropertyKind propertyKind = jpaContainer.getPropertyKind(propertyId);
			switch (propertyKind) {
				case MANY_TO_ONE:
					return createManyToOneField(type);
				default:
					// fall to default for now
			}
		}

		TextField field = new TextField();
		field.setNullRepresentation("");
		return field;
	}

	protected Field createManyToOneField(Class<?> type) {
		ComboBox select = new ComboBox();
		select.setMultiSelect(false);
		select.setContainerDataSource(getJpaContainer(type));
		select.setPropertyDataSource(new SingleSelectTranslator(select));
		select.setItemCaptionMode(NativeSelect.ITEM_CAPTION_MODE_ITEM);
		select.setFilteringMode(AbstractSelect.Filtering.FILTERINGMODE_CONTAINS);
		return select;
	}

	protected Container getJpaContainer(Class<?> type) {
		EntityManager em = jpaContainer.getEntityProvider().getEntityManager();
		return JPAContainerFactory.make(type, em);
	}

	public void addCustomFieldFactory(Object propertyId, CustomFieldFactory customFieldFactory) {
		this.customFieldFactories.put(propertyId, customFieldFactory);
	}

	public void setCustomFieldFactories(Map<Object, CustomFieldFactory> customFieldFactories) {
		this.customFieldFactories = customFieldFactories;
	}

	public void setDateResolution(int dateResolution) {
		this.dateResolution = dateResolution;
	}

	public void setDateFormat(String dateFormat) {
		this.dateFormat = dateFormat;
	}

}
