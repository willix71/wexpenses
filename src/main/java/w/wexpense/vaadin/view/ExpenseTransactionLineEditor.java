package w.wexpense.vaadin.view;

import w.wexpense.model.Expense;
import w.wexpense.model.TransactionLine;
import w.wexpense.vaadin.fieldfactory.OneToManyView;

import com.vaadin.data.Container;
import com.vaadin.ui.Table;

class ExpenseTransactionLineEditor extends OneToManyView<Expense, TransactionLine> {
	private static final long serialVersionUID = 701758651197792890L;
	
	public Table table;
	public ExpenseTransactionLineEditor(String parentPropertyId, Class<TransactionLine> childType) {
		super(parentPropertyId, childType);
	}
	
	@Override
	protected Table buildTable() {
		table = super.buildTable();
		table.setFooterVisible(true);
		table.setColumnFooter("amount", "total");		
		return table;
	}

	public Table getTable() {
		return table;
	}	
}