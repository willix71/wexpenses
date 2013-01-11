package w.wexpense.vaadin.view;

import javax.persistence.EntityManager;

import w.wexpense.model.Expense;
import w.wexpense.model.TransactionLine;
import w.wexpense.vaadin.fieldfactory.OneToManyView;

import com.vaadin.addon.beanvalidation.BeanValidationForm;
import com.vaadin.addon.jpacontainer.JPAContainer;
import com.vaadin.data.Container;
import com.vaadin.data.Item;
import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.ui.Field;

public class ExpenseEditor extends GenericEditor<Expense> {

	private static final long serialVersionUID = 701758651197792890L;

	private OneToManyView<Expense, TransactionLine> transactionLinesEditor;

	private Double amount = null;

	public ExpenseEditor() {
		super(Expense.class);
	}

	public void buildExtra() {
		transactionLinesEditor.setInstance(getItem().getBean(),
				getJpaContainer());
		addComponent(transactionLinesEditor);
	}

	@Override
	public BeanValidationForm<Expense> buildForm() {
		BeanValidationForm<Expense> form = super.buildForm();

		form.getField("amount").addListener(new Property.ValueChangeListener() {
			@Override
			public void valueChange(ValueChangeEvent event) {
				Container tlc = transactionLinesEditor.getContainer();
				
				// get new value
				Object newObject = event.getProperty().getValue();
				Double newAmount = newObject instanceof Number ? 
						((Number) newObject).doubleValue() : 
							Double.valueOf(newObject.toString());
						
				if (amount != null) {
					// loop threw the transaction lines
					for (Object id : tlc.getItemIds()) {
						// get the item property
						Property p = tlc.getItem(id).getItemProperty("amount");
						if (amount.equals(p.getValue())) {
							p.setValue(newAmount);
						}
					}
				}
				// set the new value
				amount = newAmount;

				((ExpenseTransactionLineEditor) transactionLinesEditor).updateValues();
			}
		});
		
		return form;
	}

	public void setInstance(Expense instance, JPAContainer<Expense> jpaContainer) {
		super.setInstance(instance, jpaContainer);
		if (instance != null) {
			amount = instance.getAmount();
		}
	}

	@Override
	protected Expense insert(EntityManager em) {
		Expense t = super.insert(em);
		transactionLinesEditor.insert(t, em);
		return t;
	}

	@Override
	protected Expense update(EntityManager em) {
		Expense t = super.update(em);
		transactionLinesEditor.update(t, em);
		return t;
	}

	public void setTransactionLinesEditor(
			OneToManyView<Expense, TransactionLine> transactionLinesEditor) {
		this.transactionLinesEditor = transactionLinesEditor;
	}
}
