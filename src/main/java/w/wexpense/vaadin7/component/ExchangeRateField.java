package w.wexpense.vaadin7.component;

import w.wexpense.model.ExchangeRate;
import w.wexpense.vaadin7.UIHelper;
import w.wexpense.vaadin7.WexUI;
import w.wexpense.vaadin7.event.SelectionChangeEvent;
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
	private Button btn;
	private ExchangeRate value;
	
	public ExchangeRateField() {		
		layout = new HorizontalLayout();		
		layout.setSizeFull();
		
		label = new Label("xxx");
		layout.addComponent(label);
		
		btn = new Button();			
		btn.setStyleName("link");
		btn.setIcon(new ThemeResource("../runo/icons/16/document-add.png"));
		btn.addClickListener((Button.ClickListener) this);
		layout.addComponent(btn);
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
		final SelectorView<ExchangeRate> selector = ((WexUI) UI.getCurrent()).getBean(SelectorView.class, "exchangeRateSelectorView");
		
		if (value!=null) {
			selector.setValue(value);
		}
		
		selector.addListener(new Component.Listener() {
			private static final long serialVersionUID = 8121179082149508635L;

			@Override
			public void componentEvent(Event event) {
				if (event instanceof SelectionChangeEvent && event.getComponent() == selector) {
					setValue(null);
					setValue(((SelectionChangeEvent<ExchangeRate>)event).getBean());
				}
			}
		});
		Window w = UIHelper.displayModalWindow(selector);
		w.setHeight(400,Unit.PIXELS);
		w.setWidth(500, Unit.PIXELS);
   }

}
