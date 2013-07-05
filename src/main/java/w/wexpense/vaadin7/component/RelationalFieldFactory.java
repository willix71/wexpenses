package w.wexpense.vaadin7.component;

import w.wexpense.model.Codable;
import w.wexpense.model.DBable;
import w.wexpense.model.ExchangeRate;
import w.wexpense.service.ContainerService;

import com.vaadin.addon.jpacontainer.fieldfactory.SingleSelectConverter;
import com.vaadin.data.Container;
import com.vaadin.shared.ui.combobox.FilteringMode;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Field;
import com.vaadin.ui.NativeSelect;

public class RelationalFieldFactory extends SimpleFieldFactory {

	private static final long serialVersionUID = -2122739273213720235L;
	
	protected ContainerService persistenceService;
	
	public RelationalFieldFactory(ContainerService persistenceService) {
		super();
		this.persistenceService = persistenceService;
	}

	@Override
	protected Field<?> createField(Class<?> type) {
		// Null typed properties can not be edited
		if (type == null) {
			return null;
		}
		
		Field<?> f = createRelationalField(type);
		if (f == null) {
			f = createSimpleField(type);
		}
		return f;
	}


	protected Field<?> createRelationalField(Class<?> type) {
		if (ExchangeRate.class.isAssignableFrom(type)) {
			return new ExchangeRateField();
		}
		if (Codable.class.isAssignableFrom(type)) {
			return createManyToOneField(type);
		}
		if (DBable.class.isAssignableFrom(type)) {
			return createEditableManyToOneField(type);
		}
		return null;
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	protected Field<?> createManyToOneField(Class<?> type) {
		ComboBox comboBox = new ComboBox();
		comboBox.setImmediate(true);
		comboBox.setContainerDataSource(getContainer(type));
		comboBox.setConverter(new SingleSelectConverter(comboBox));
		comboBox.setItemCaptionMode(NativeSelect.ItemCaptionMode.ITEM);
		comboBox.setFilteringMode(FilteringMode.CONTAINS);
		comboBox.setSizeFull();
		return comboBox;	
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
   protected Field<?> createEditableManyToOneField(Class<?> type) {
		return new WexComboBox(type, getContainer(type));		
	}
	
	protected Container getContainer(Class<?> type) {
		return persistenceService.getContainer(type);		
	}
}