package w.wexpense.vaadin.view;

import w.wexpense.model.Expense;
import w.wexpense.model.TransactionLine;

public class ExpenseEditor extends GenericEditor<Expense> {

	private static final long serialVersionUID = 701758651197792890L;

	private ExpenseTransactionLineView<Expense, TransactionLine> lines;
	
	public ExpenseEditor() {
		super(Expense.class);
	}

	public void buildExtra() {
		lines = new ExpenseTransactionLineView<Expense, TransactionLine>(getItem().getBean(), getJpaContainer(), "transactions", TransactionLine.class);
		addComponent(lines);
	}

}
