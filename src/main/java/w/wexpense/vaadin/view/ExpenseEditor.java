package w.wexpense.vaadin.view;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import w.wexpense.model.Expense;
import w.wexpense.model.TransactionLine;

import com.vaadin.addon.jpacontainer.JPAContainer;
import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeEvent;

public class ExpenseEditor extends OneToManyEditor<Expense, TransactionLine> {

	private static final long serialVersionUID = 701758651197792890L;

	private static final Logger LOGGER = LoggerFactory.getLogger(ExpenseEditor.class);

	public ExpenseEditor() {
		super(Expense.class);
	}
	
	@Override
	public void setInstance(Expense instance, JPAContainer<Expense> jpaContainer) {
		super.setInstance(instance, jpaContainer);
		
		if (instance != null) {
			((ExpenseTransactionLineEditor) getSubEditor()).setCurrentAmount(instance.getAmount().doubleValue());
		}
	}
	
	@Override
	public void attach() {
		super.attach();
		
		addListener();
		((ExpenseTransactionLineEditor) getSubEditor()).enableUpdateValues(true);
	}
	
	protected void addListener() {
		// attach user interaction listener
		getForm().getField("amount").addListener(new Property.ValueChangeListener() {
			@Override
			public void valueChange(ValueChangeEvent event) {
				// get new value
				Object newObject = event.getProperty().getValue();
				Double newAmount = newObject instanceof Number ? 
						((Number) newObject).doubleValue() : 
							Double.valueOf(newObject.toString());
				
				LOGGER.debug("Updating transaction lines with new amount {}", newAmount);
				
				((ExpenseTransactionLineEditor) getSubEditor()).setCurrentAmount(newAmount);
			}
		});
	}

	@Override
	protected Expense save() {	
		((ExpenseTransactionLineEditor) getSubEditor()).enableUpdateValues(false);
		return super.save();
	}
}
