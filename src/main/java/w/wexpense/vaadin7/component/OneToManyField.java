package w.wexpense.vaadin7.component;

import java.util.ArrayList;
import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;

import w.wexpense.service.ContainerService;
import w.wexpense.vaadin7.action.ActionHandler;
import w.wexpense.vaadin7.container.OneToManyContainer;
import w.wexpense.vaadin7.support.TableColumnConfig;

import com.vaadin.shared.ui.MultiSelectMode;
import com.vaadin.ui.Component;
import com.vaadin.ui.CustomField;
import com.vaadin.ui.Table;

public class OneToManyField<T> extends CustomField<Collection<T>> {

	private static final long serialVersionUID = 1L;

	@Autowired
	protected ContainerService persistenceService;

	private OneToManyContainer<T> container;
	private Table table;

	private ActionHandler actionHandler;

	public OneToManyField(Class<T> entityClass, TableColumnConfig... columnConfigs) {
		container = new OneToManyContainer<T>(entityClass);

		table = new Table(null, container);
		table.setBuffered(false);
		table.setImmediate(true);

		table.setSelectable(true);
		table.setMultiSelect(true);
		table.setMultiSelectMode(MultiSelectMode.SIMPLE);

		table.setColumnCollapsingAllowed(true);
		TableColumnConfig.configure(table, columnConfigs);

		table.setWidth(100, Unit.PERCENTAGE);
	}

	@Override
	protected Component initContent() {
		if (persistenceService != null) {
			table.setTableFieldFactory(new RelationalFieldFactory(persistenceService));
		}
		
		// listen to events
		if (actionHandler != null) {
			actionHandler.setTable(table);
		}

		return table;
	}

	@Override
	protected void setInternalValue(Collection<T> newValue) {
		if (newValue == null) {
			newValue = new ArrayList<T>();
			getPropertyDataSource().setValue(newValue);
		}

		super.setInternalValue(newValue);

		container.setBeans(newValue);

		if (table != null) {
			table.markAsDirtyRecursive();
		}
	}

	@Override
	@SuppressWarnings("unchecked")
	public Class<? extends Collection<T>> getType() {
		return (Class<? extends Collection<T>>) Collection.class;
	}

	public void setActionHandler(ActionHandler actionHandler) {
		this.actionHandler = actionHandler;
	}

	public void setPageLength(int length) {
		table.setPageLength(length);
	}

	public void setEditable(boolean editable) {
		table.setEditable(editable);
	}

}