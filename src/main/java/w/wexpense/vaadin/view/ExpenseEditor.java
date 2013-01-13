package w.wexpense.vaadin.view;

import javax.annotation.PostConstruct;
import javax.persistence.EntityManager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import w.wexpense.model.Expense;
import w.wexpense.model.TransactionLine;

import com.vaadin.addon.jpacontainer.JPAContainer;
import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeEvent;

public class ExpenseEditor extends GenericEditor<Expense> {

	private static final long serialVersionUID = 701758651197792890L;

	private static final Logger LOGGER = LoggerFactory.getLogger(ExpenseEditor.class);
	
	private OneToManyView<Expense, TransactionLine> transactionLinesEditor;

	public ExpenseEditor() {
		super(Expense.class);
	}

	@PostConstruct
	@Override
	public void buildLayout() {
		buildForm();
		addComponent(transactionLinesEditor);
		buildButtons();
	}
	
	@Override
	public void setInstance(Expense instance, JPAContainer<Expense> jpaContainer) {
		super.setInstance(instance, jpaContainer);
		
		transactionLinesEditor.setInstance(getItem().getBean(), getJpaContainer());
		if (instance != null) {
			((ExpenseTransactionLineEditor) transactionLinesEditor).setCurrentAmount(instance.getAmount());
		}

	}
	
	@Override
	public void attach() {
		super.attach();
		
		addListener();
		((ExpenseTransactionLineEditor) transactionLinesEditor).enableUpdateValues(true);
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
						
//				Container tlc = transactionLinesEditor.getContainer();									
//				// loop threw the transaction lines
//				for (Object id : tlc.getItemIds()) {
//					// get the item property
//					Property p = tlc.getItem(id).getItemProperty("amount");
//					if (currentAmount==null?p.getValue()==null:currentAmount.equals(p.getValue())) {
//						p.setValue(newAmount);
//					}
//				}
				
				LOGGER.debug("Updating transaction lines with new amount {}", newAmount);
				
				((ExpenseTransactionLineEditor) transactionLinesEditor).setCurrentAmount(newAmount);
			}
		});
	}

	@Override
	protected Expense save() {	
		((ExpenseTransactionLineEditor) transactionLinesEditor).enableUpdateValues(false);
		return super.save();
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
