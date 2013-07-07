package w.wexpense.vaadin7.component;

import w.wexpense.model.ExchangeRate;
import w.wexpense.vaadin7.UIHelper;
import w.wexpense.vaadin7.WexUI;
import w.wexpense.vaadin7.event.EntityChangeEvent;
import w.wexpense.vaadin7.event.SelectionChangeEvent;
import w.wexpense.vaadin7.view.EditorView;
import w.wexpense.vaadin7.view.SelectorView;

import com.vaadin.server.ThemeResource;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Component;
import com.vaadin.ui.CustomField;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.UI;
import com.vaadin.ui.Window;

public class ExchangeRateField extends CustomField<ExchangeRate> implements Button.ClickListener {

	private HorizontalLayout layout;
	private Label label;	
	private Button btnAdd;
	private Button btnSelect;
	private ExchangeRate value;
	
	public ExchangeRateField() {		
		layout = new HorizontalLayout();		
		layout.setSizeFull();
		
		label = new Label("xxx");
		label.setSizeFull();
		layout.addComponent(label);
		
		btnSelect = new Button();			
		btnSelect.setStyleName("link");
		btnSelect.setIcon(new ThemeResource("../runo/icons/16/arrow-down.png"));
		btnSelect.addClickListener((Button.ClickListener) this);
		layout.addComponent(btnSelect);
		
		btnAdd = new Button();			
		btnAdd.setStyleName("link");
		btnAdd.setIcon(new ThemeResource("../runo/icons/16/document-add.png"));
		btnAdd.addClickListener((Button.ClickListener) this);
		layout.addComponent(btnAdd);
		
		layout.setExpandRatio(label, 100);
	}

	@Override
   protected Component initContent() {
		return layout;
   }

	@Override
   public Class<? extends ExchangeRate> getType() {
	   return ExchangeRate.class;
   }
	
	@Override
   protected void setInternalValue(ExchangeRate newValue) {
	   super.setInternalValue(newValue);
	   this.value = newValue;
	   label.setValue(newValue!=null?newValue.getRate()+"":"");
   }
	
	@Override
	public ExchangeRate getValue() {
		return value;
	}
	
	@Override
   public void buttonClick(ClickEvent event) {
		if (btnSelect == event.getSource()){
			final SelectorView<ExchangeRate> selector = ((WexUI) UI.getCurrent()).getBean(SelectorView.class, "exchangeRateSelectorView");
			
			if (value!=null) {
				selector.setValue(value);
			}
			
			selector.addListener(new Component.Listener() {
				private static final long serialVersionUID = 8121179082149508635L;
	
				@Override
				public void componentEvent(Event event) {
					if (event instanceof SelectionChangeEvent && event.getComponent() == selector) {
						setValue(((SelectionChangeEvent<ExchangeRate>)event).getBean());
					}
				}
			});
			Window w = UIHelper.displayModalWindow(selector);
			w.setHeight(400,Unit.PIXELS);
			w.setWidth(500, Unit.PIXELS);
		} else if (btnAdd == event.getSource()) {
			final EditorView<ExchangeRate, Long> editor = ((WexUI) UI.getCurrent()).getBean(EditorView.class, "exchangeRateEditorView");
			
			if (value!=null) {
				editor.setInstance(value);
			} else {
				editor.newInstance();
			}
			
			editor.addListener(new Component.Listener() {
				private static final long serialVersionUID = 8121179082149508635L;
				
				@Override
				public void componentEvent(Event event) {
					if (event instanceof EntityChangeEvent && event.getComponent() == editor) {
						setInternalValue(null);
						setValue((ExchangeRate) ((EntityChangeEvent)event).getObject());
					}
				}
			});
			Window w = UIHelper.displayModalWindow(editor);
		}
   }
}
