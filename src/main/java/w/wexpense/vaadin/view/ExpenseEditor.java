package w.wexpense.vaadin.view;

import javax.persistence.EntityManager;

import w.wexpense.model.Expense;
import w.wexpense.model.TransactionLine;
import w.wexpense.vaadin.fieldfactory.OneToManyView;

public class ExpenseEditor extends GenericEditor<Expense> {

	private static final long serialVersionUID = 701758651197792890L;

	private OneToManyView<Expense, TransactionLine> transactionLinesEditor;
	
	public ExpenseEditor() {
		super(Expense.class);
	}

	public void buildExtra() {
		transactionLinesEditor.setInstance(getItem().getBean(), getJpaContainer());
		addComponent(transactionLinesEditor);
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
	
	public void setTransactionLinesEditor(OneToManyView<Expense, TransactionLine> transactionLinesEditor) {
		this.transactionLinesEditor = transactionLinesEditor;
	}
}
