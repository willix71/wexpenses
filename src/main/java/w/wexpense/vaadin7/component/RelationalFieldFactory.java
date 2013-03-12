package w.wexpense.vaadin7.component;

import w.wexpense.model.Codable;
import w.wexpense.model.DBable;
import w.wexpense.service.PersistenceService;

import com.vaadin.addon.jpacontainer.fieldfactory.SingleSelectConverter;
import com.vaadin.data.Container;
import com.vaadin.shared.ui.combobox.FilteringMode;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Field;
import com.vaadin.ui.NativeSelect;

public class RelationalFieldFactory extends SimpleFieldFactory {

	private static final long serialVersionUID = -2122739273213720235L;
	
	protected PersistenceService persistenceService;
	
	public RelationalFieldFactory(PersistenceService persistenceService) {
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
		if (Codable.class.isAssignableFrom(type)) {
			return createManyToOneField(type);
		}
		if (DBable.class.isAssignableFrom(type)) {
			return createManyToOneField(type);
		}
		return null;
	}

	protected Field<?> createManyToOneField(Class<?> type) {
		ComboBox comboBox = new ComboBox();
		comboBox.setImmediate(true);
		comboBox.setContainerDataSource(getContainer(type));
		comboBox.setConverter(new SingleSelectConverter(comboBox));
		comboBox.setItemCaptionMode(NativeSelect.ItemCaptionMode.ITEM);
		comboBox.setFilteringMode(FilteringMode.CONTAINS);
		comboBox.setSizeFull();
		return comboBox;
		
		//return new WexComboBox(type, getContainer(type));		
	}

	protected Container getContainer(Class<?> type) {
		return persistenceService.getJPAContainer(type);		
	}
}