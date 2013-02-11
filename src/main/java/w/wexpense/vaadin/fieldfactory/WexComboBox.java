package w.wexpense.vaadin.fieldfactory;

import org.vaadin.addon.customfield.CustomField;

import w.wexpense.model.DBable;
import w.wexpense.vaadin.SelectionChangeEvent;
import w.wexpense.vaadin.WexApplication;
import w.wexpense.vaadin.WexWindow;
import w.wexpense.vaadin.view.GenericEditor;

import com.vaadin.addon.jpacontainer.JPAContainer;
import com.vaadin.addon.jpacontainer.fieldfactory.SingleSelectTranslator;
import com.vaadin.data.Property;
import com.vaadin.data.Validator;
import com.vaadin.data.Validator.InvalidValueException;
import com.vaadin.terminal.Sizeable;
import com.vaadin.ui.AbstractSelect;
import com.vaadin.ui.AbstractSelect.NewItemHandler;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.NativeSelect;

public class WexComboBox<T> extends CustomField implements Button.ClickListener {

	private static final long serialVersionUID = -5068539384518692094L;
	
	private Class<T> entityClass;
	private JPAContainer<T> comboContainer;
	private Component caller;
	private ComboBox comboBox;
	
	private Button add;
	private String text;
	
	public WexComboBox(Class<T> entityClass, JPAContainer<T> comboContainer, Component caller) {
		this.entityClass = entityClass;
		this.comboContainer = comboContainer;
		this.caller = caller;
		
		comboBox = buildComboBox();

		HorizontalLayout layout = new HorizontalLayout();
		layout.setSizeFull();
		layout.addComponent(comboBox);
		layout.setExpandRatio(comboBox, 100);
		
		if (DBable.class.isAssignableFrom(entityClass)) {
			add = new Button("", (Button.ClickListener) this);
			add.setWidth(20, Sizeable.UNITS_PIXELS);
			add.setHeight(20, Sizeable.UNITS_PIXELS);
			layout.addComponent(add);
			
			comboBox.setNewItemsAllowed(true);
			comboBox.setNewItemHandler(new NewItemHandler() {
				private static final long serialVersionUID = 1L;

				public void addNewItem(String newItemCaption) {
					text=newItemCaption;
				}
			});
		}
		
		setCompositionRoot(layout);
	}

	protected ComboBox buildComboBox() {
		ComboBox box = new ComboBox();
		box.setImmediate(true);
		box.setMultiSelect(false);
		box.setContainerDataSource(comboContainer);
		box.setPropertyDataSource(new SingleSelectTranslator(box));
		box.setItemCaptionMode(NativeSelect.ITEM_CAPTION_MODE_ITEM);
		box.setFilteringMode(AbstractSelect.Filtering.FILTERINGMODE_CONTAINS);
		box.setSizeFull();
		return box;
	}
	
	@Override
	public void buttonClick(ClickEvent event) {
		addNew(comboBox.getValue(), text);		
	}

	protected void addNew(final Object currentValue, String currentText) {
		WexApplication wexapplication = (WexApplication) caller.getApplication();

		// get the editor for this entity from the main application
		@SuppressWarnings("unchecked")
		final GenericEditor<T> editor = (GenericEditor<T>) wexapplication.getEditorFor(entityClass);
		editor.setInstance(null, comboContainer);
		editor.addListener(new Component.Listener() {
			private static final long serialVersionUID = 8121179082149508635L;
			
			@Override
			public void componentEvent(Event event) {
				if (event instanceof SelectionChangeEvent && event.getComponent()==editor) {
					setValue(((SelectionChangeEvent) event).getId());
				} 
			}
		});	
		
		// set the entered text in the name property if one exists
		Property p = editor.getItem().getItemProperty("name");
		if (p!=null && String.class.equals(p.getType())) {
			p.setValue(currentText);
		}
		
		// open a modal window
		WexWindow window = new WexWindow(editor);
		window.setModal(true);	
		wexapplication.getMainWindow().addWindow(window);
	}

	
	@Override
	public Class<?> getType() {
		return entityClass;
	}
	
	@Override
	public void commit() throws SourceException, InvalidValueException {
		comboBox.commit();
	}

	@Override
	public void discard() throws SourceException {
		comboBox.discard();
	}

	@Override
	public Object getValue() {
		return comboBox.getValue();
	}

	@Override
	public void setValue(Object newValue) throws ReadOnlyException, ConversionException {
		comboBox.setValue(newValue);
	}

	@Override
	public void requestRepaint() {
		// not sure that the following is really needed
		// super.requestRepaint();

		if (comboBox != null) {
			comboBox.requestRepaint();
		}
	}
	
	@Override
	public Property getPropertyDataSource() {
		return comboBox.getPropertyDataSource();
	}

	@Override
	public void addValidator(Validator validator) {
		comboBox.addValidator(validator);
	}

	@Override
	public void removeValidator(Validator validator) {
		comboBox.removeValidator(validator);
	}

	@Override
	public void addListener(ValueChangeListener listener) {
		comboBox.addListener(listener);
	}

	@Override
	public void removeListener(ValueChangeListener listener) {
		comboBox.removeListener(listener);
	}
}