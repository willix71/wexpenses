package w.wexpense.vaadin.view.model;

import java.math.BigDecimal;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import w.wexpense.model.Currency;
import w.wexpense.model.Expense;
import w.wexpense.model.TransactionLine;
import w.wexpense.utils.ExpenseUtils;
import w.wexpense.vaadin.view.OneToManyEditor;

import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.ui.AbstractOrderedLayout;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;

public class ExpenseEditor extends OneToManyEditor<Expense, TransactionLine> {

	private static final long serialVersionUID = 701758651197792890L;

	private static final Logger LOGGER = LoggerFactory.getLogger(ExpenseEditor.class);

	public ExpenseEditor() {
		super(Expense.class);
	}
	
	protected AbstractOrderedLayout buildButtons() {
		AbstractOrderedLayout layout = super.buildButtons();
		
		layout.addComponent(new Button("debug", new Button.ClickListener() {
			@Override
			public void buttonClick(ClickEvent event) {
				LOGGER.info(ExpenseUtils.toString(getItem().getBean()));
			}
		}));
		
		return layout;
	}
	
	@Override
	public void attach() {
		super.attach();

		addListener();

		((ExpenseTransactionLineEditor) getSubEditor()).setCurrentAmount(getItem().getBean().getAmount());
		((ExpenseTransactionLineEditor) getSubEditor()).enableUpdateValues(true);
	}
	
	protected void addListener() {
		// attach user interaction listener
		getForm().getField("amount").addListener(new Property.ValueChangeListener() {
			@Override
			public void valueChange(ValueChangeEvent event) {
				BigDecimal newAmount = getItem().getBean().getAmount();

				LOGGER.debug("Updating transaction lines with new amount {}", newAmount);
				
				((ExpenseTransactionLineEditor) getSubEditor()).updateAmount(newAmount);
			}
		});
		
		getForm().getField("payee").addListener(new Property.ValueChangeListener() {
			@Override
			public void valueChange(ValueChangeEvent event) {
				Expense expense = getItem().getBean();
				try {
					Currency currency = expense.getPayee().getCity().getCountry().getCurrency();
					
					LOGGER.debug("Updating currency to {}", currency);
					expense.setCurrency(currency);
					
					// refresh field
					getForm().getField("currency").requestRepaint();
				} catch(NullPointerException e) {
					LOGGER.warn("No currency defined");
				}
			}
		});
	}

	@Override
	protected Expense save() {	
		((ExpenseTransactionLineEditor) getSubEditor()).enableUpdateValues(false);
		return super.save();
	}
}
