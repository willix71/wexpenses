package w.wexpense.vaadin.fieldfactory;

import w.wexpense.model.DBable;
import w.wexpense.vaadin.ClosableWindow;
import w.wexpense.vaadin.WexApplication;
import w.wexpense.vaadin.view.GenericEditor;

import com.vaadin.addon.jpacontainer.JPAContainer;
import com.vaadin.addon.jpacontainer.fieldfactory.SingleSelectTranslator;
import com.vaadin.data.Property;
import com.vaadin.ui.AbstractSelect;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.NativeSelect;
import com.vaadin.ui.Window.CloseEvent;
import com.vaadin.ui.Window.CloseListener;

public class WexComboBox<T> extends ComboBox {

	private Class<T> entityClass;
	
	private JPAContainer<T> comboContainer; 
	
	private Component caller;

	public WexComboBox(Class<T> entityClass, JPAContainer<T> comboContainer, Component caller) {
		this.entityClass = entityClass;
		this.comboContainer = comboContainer;
		this.caller = caller;
		
		setMultiSelect(false);
		setContainerDataSource(comboContainer);
		setPropertyDataSource(new SingleSelectTranslator(this));
		setItemCaptionMode(NativeSelect.ITEM_CAPTION_MODE_ITEM);
		setFilteringMode(AbstractSelect.Filtering.FILTERINGMODE_CONTAINS);
		setSizeFull();
				
		if (DBable.class.isAssignableFrom(entityClass)) {
			setNewItemsAllowed(false);
			setNewItemHandler(new NewItemHandler() {
				public void addNewItem(String newItemCaption) {
					addNew(getValue(), newItemCaption);
				}
			});
		}
	}
	
	protected void addNew(final Object currentValue, String currentText) {
		// get the editor for this entity from the main application
		WexApplication wexapplication = (WexApplication) caller.getApplication();
		final GenericEditor editor = wexapplication.getEditorFor(entityClass);
		editor.setInstance(null, comboContainer);
		
		// set the entered text in the name property if one exists
		Property p = editor.getItem().getItemProperty("name");
		if (p!=null && String.class.equals(p.getType())) {
			p.setValue(currentText);
		}
		
		// open a modal window
		ClosableWindow window = new ClosableWindow(editor);
		window.setModal(true);
		wexapplication.getMainWindow().addWindow(window);
		
		window.addListener(new CloseListener() {						
			@Override
			public void windowClose(CloseEvent e) {
				if (!editor.isNew()) {
					Long id = ((DBable) editor.getItem().getBean()).getId();
					setValue(id);
				} else {
					setValue(currentValue);
				}
			}
		});		
	}
}
