package w.wexpense.vaadin7.component;

import w.wexpense.model.DBable;
import w.wexpense.vaadin.SelectionChangeEvent;
import w.wexpense.vaadin.WexApplication;
import w.wexpense.vaadin.WexWindow;
import w.wexpense.vaadin.view.GenericEditor;
import w.wexpense.vaadin7.WexUI;

import com.vaadin.addon.jpacontainer.JPAContainer;
import com.vaadin.addon.jpacontainer.fieldfactory.SingleSelectConverter;
import com.vaadin.data.Container;
import com.vaadin.data.Property;
import com.vaadin.data.Validator;
import com.vaadin.data.Validator.InvalidValueException;
import com.vaadin.data.util.converter.Converter.ConversionException;
import com.vaadin.server.Sizeable;
import com.vaadin.shared.ui.combobox.FilteringMode;
import com.vaadin.ui.AbstractSelect;
import com.vaadin.ui.AbstractSelect.NewItemHandler;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.CustomField;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.NativeSelect;
import com.vaadin.ui.UI;
//import com.vaadin.addon.jpacontainer.fieldfactory.SingleSelectTranslator;

public class WexComboBox<T> extends CustomField<T> implements Button.ClickListener {

	private static final long serialVersionUID = -5068539384518692094L;
	
	private Class<T> entityClass;
	private Container comboContainer;

	private HorizontalLayout layout;
	private ComboBox comboBox;	
	private Button add;
	private String text;
	
	public WexComboBox(Class<T> entityClass, Container comboContainer) {
		this.entityClass = entityClass;
		this.comboContainer = comboContainer;
		
		comboBox = new ComboBox();
		comboBox.setImmediate(true);
		comboBox.setContainerDataSource(comboContainer);
		comboBox.setConverter(new SingleSelectConverter<T>(comboBox));
		comboBox.setItemCaptionMode(NativeSelect.ItemCaptionMode.ITEM);
		comboBox.setFilteringMode(FilteringMode.CONTAINS);
		comboBox.setSizeFull();

		HorizontalLayout layout = new HorizontalLayout();
		layout.setSizeFull();
		layout.addComponent(comboBox);
		layout.setExpandRatio(comboBox, 100);
		
		if (DBable.class.isAssignableFrom(entityClass)) {
			add = new Button("", (Button.ClickListener) this);
			add.setWidth(20, Sizeable.Unit.PIXELS);
			add.setHeight(20, Sizeable.Unit.PIXELS);
			layout.addComponent(add);
			
			comboBox.setNewItemsAllowed(true);
			comboBox.setNewItemHandler(new NewItemHandler() {
				private static final long serialVersionUID = 1L;

				public void addNewItem(String newItemCaption) {
					text=newItemCaption;
				}
			});
		}
		
		// OLD vaadin 6
		// setCompositionRoot(layout);
	}

	@Override
   protected Component initContent() {
	   return layout;
   }
	
	@Override
	public void buttonClick(ClickEvent event) {
		addNew(comboBox.getValue(), text);		
	}

	protected void addNew(final Object currentValue, String currentText) {
//		WexApplication wexapplication = (WexUI) UI.getCurrent();
//
//		// get the editor for this entity from the main application
//		@SuppressWarnings("unchecked")
//		final GenericEditor<T> editor = (GenericEditor<T>) wexapplication.getEditorFor(entityClass);
//		editor.setInstance(null, comboContainer);
//		editor.addListener(new Component.Listener() {
//			private static final long serialVersionUID = 8121179082149508635L;
//			
//			@Override
//			public void componentEvent(Event event) {
//				if (event instanceof SelectionChangeEvent && event.getComponent()==editor) {
//					setValue(((SelectionChangeEvent) event).getId());
//				} 
//			}
//		});	
//		
//		// set the entered text in the name property if one exists
//		Property p = editor.getItem().getItemProperty("name");
//		if (p!=null && String.class.equals(p.getType())) {
//			p.setValue(currentText);
//		}
//		
//		// open a modal window
//		WexWindow window = new WexWindow(editor);
//		window.setModal(true);	
//		UI.getCurrent().addWindow(window);
	}

	
	@Override
	public Class<T> getType() {
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
	public T getValue() {
		return (T) comboBox.getConvertedValue();
	}

	@Override
	public void setValue(T newValue) throws ReadOnlyException, ConversionException {
		comboBox.setValue(newValue);
	}
	
	@Override
   public void markAsDirty() {
		if (comboBox != null) {
			comboBox.markAsDirty();
		}
	}
   
	@Deprecated
	@Override
	public void requestRepaint() {
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