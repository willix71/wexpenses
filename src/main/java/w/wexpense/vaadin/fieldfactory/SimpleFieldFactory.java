package w.wexpense.vaadin.fieldfactory;

import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import w.wexpense.vaadin.PropertyConfiguror;

import com.vaadin.data.Container;
import com.vaadin.data.Item;
import com.vaadin.server.Sizeable;
import com.vaadin.shared.ui.datefield.Resolution;
import com.vaadin.ui.AbstractSelect;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.DateField;
import com.vaadin.ui.DefaultFieldFactory;
import com.vaadin.ui.Field;
import com.vaadin.ui.Form;
import com.vaadin.ui.TextField;

public class SimpleFieldFactory extends DefaultFieldFactory {

	private static final long serialVersionUID = -2122739273213720235L;
	
	private static final String  DEFAULT_DATE_RESOLUTION = String.valueOf(DateField.RESOLUTION_DAY);
	private static final String DEFAULT_DATE_FORMAT = "dd/MM/yyyy";
	
	private Map<Object, CustomFieldFactory> customFieldFactories = new HashMap<Object, CustomFieldFactory>();
	private PropertyConfiguror propertyConfiguror;

	public SimpleFieldFactory(PropertyConfiguror propertyConfiguror) {
	   super();
	   this.propertyConfiguror = propertyConfiguror;
   }

	@Override
	public Field createField(Item item, Object propertyId, Component uiContext) {
		Field field;

		CustomFieldFactory factory = customFieldFactories.get(propertyId);
		if (factory != null) {
			field = factory.newInstance(item);
		} else {
			Class<?> type = item.getItemProperty(propertyId).getType();
			field = createField(item, type, propertyId, uiContext);
		}

		// system properties are always read-only
		for(String o: PropertyConfiguror.systemProperties) { 
			if (o.equals(propertyId)) {
				field.setReadOnly(true);
				break;
			}
		}			
		
		field.setWidth(100, Sizeable.Unit.PERCENTAGE);
		field.setCaption(createCaptionByPropertyId(propertyId));

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
			field = createField(item, type, propertyId, uiContext);
		}

		field.setWidth(100, Sizeable.UNITS_PERCENTAGE);
		field.setCaption(createCaptionByPropertyId(propertyId));
		return field;
	}

	protected Field createField(Item item, Class<?> type, Object propertyId, Component uiContext) {
		return createSimpleField(type, propertyId);
	}
		
	protected Field createSimpleField(Class<?> type, Object propertyId) {
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
			final DateField dateField = new WexDateField();
			String dateFormat = propertyConfiguror.getPropertyValue(
					propertyId.toString() + PropertyConfiguror.propertyDateFormat, DEFAULT_DATE_FORMAT);			
			dateField.setDateFormat(dateFormat);
			
			String dateResolution = propertyConfiguror.getPropertyValue(
					propertyId.toString() + PropertyConfiguror.propertyDateResolution, DEFAULT_DATE_RESOLUTION);			
			dateField.setResolution(Resolution.valueOf(dateResolution));
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


		TextField field = new TextField();
		field.setNullRepresentation("");
		return field;
	}

	public void addCustomFieldFactory(Object propertyId, CustomFieldFactory customFieldFactory) {
		this.customFieldFactories.put(propertyId, customFieldFactory);
	}

	public void setCustomFieldFactories(Map<Object, CustomFieldFactory> customFieldFactories) {
		this.customFieldFactories = customFieldFactories;
	}
	
	public PropertyConfiguror getPropertyConfiguror() {
		return propertyConfiguror;
	}

	public void setPropertyConfiguror(PropertyConfiguror propertyConfiguror) {
		this.propertyConfiguror = propertyConfiguror;
	}
}
