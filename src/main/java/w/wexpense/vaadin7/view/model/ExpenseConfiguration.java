package w.wexpense.vaadin7.view.model;

import java.math.BigDecimal;
import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

import w.wexpense.model.Expense;
import w.wexpense.model.TransactionLine;
import w.wexpense.service.StorableService;
import w.wexpense.utils.TransactionLineUtils;
import w.wexpense.vaadin7.action.ActionHandler;
import w.wexpense.vaadin7.action.ActionHelper;
import w.wexpense.vaadin7.action.ListViewAction;
import w.wexpense.vaadin7.action.RemoveAction;
import w.wexpense.vaadin7.component.OneToManyField;
import w.wexpense.vaadin7.container.OneToManyContainer;
import w.wexpense.vaadin7.support.TableColumnConfig;
import w.wexpense.vaadin7.view.EditorView;
import w.wexpense.vaadin7.view.ListView;

import com.vaadin.ui.Table;

@Configuration
public class ExpenseConfiguration {

	@Autowired
	@Qualifier("expenseService") 
	private StorableService<Expense, Long> expenseService;
	
	@Bean
	@Scope("prototype")
	public EditorView<Expense, Long>  expenseEditorView() {
		OneToManyField<TransactionLine> expenseTransactionsField = expenseTransactionsField();
		
		final EditorView<Expense, Long> editorview = new EditorView<Expense, Long>(expenseService);
		editorview.setProperties("fullId","uid","createdTs","modifiedTs","type","payee","date","amount","currency","externalReference","description","payment", "transactions");
		editorview.setField("transactions", expenseTransactionsField);
		
		ListViewAction addAction = new ListViewAction("add") {			
			@Override
			public void handleAction(Object sender, Object target) {
				BigDecimal amount = editorview.getBeanItem().getBean().getAmount();
				
				Table table = (Table) sender;
				
				@SuppressWarnings("unchecked")
            OneToManyContainer<TransactionLine> container = (OneToManyContainer<TransactionLine>) table.getContainerDataSource();
				Collection<TransactionLine> lines = container.getBeans();
				
				container.addBean(TransactionLineUtils.newTransactionLine(amount, lines));				
			}
			
			@Override
			public boolean canHandle(Object target, Object sender) {
				return true;
			}
		};
      
      ActionHandler action = new ActionHandler();
		action.addListViewAction(addAction);
		action.addListViewAction(new RemoveAction<TransactionLine>());
		expenseTransactionsField.setActionHandler(action);
		
		return editorview;
	}

	@Bean
	@Scope("prototype")
	OneToManyField<TransactionLine> expenseTransactionsField() {
      OneToManyField<TransactionLine> transactions = new OneToManyField<TransactionLine>(TransactionLine.class,
            new TableColumnConfig("fullId").collapse().rightAlign(),
            new TableColumnConfig("uid").collapse(),
            new TableColumnConfig("createdTs").collapse(),
            new TableColumnConfig("modifiedTs").collapse(),
           
            new TableColumnConfig("account"),
            new TableColumnConfig("discriminator"),
            new TableColumnConfig("factor").centerAlign().width(60),
            new TableColumnConfig("amount").rightAlign().width(100),
            new TableColumnConfig("exchangeRate"),
            new TableColumnConfig("value").rightAlign().width(100)
      		);
      transactions.setPageLength(5);
      transactions.setEditable(true);
      return transactions;
	}
	
	@Bean
	@Scope("prototype")
	public ListView<Expense> expenseListView() {
		ListView<Expense> listview = new ListView<Expense>(Expense.class);
		listview.setColumnConfigs(
	                new TableColumnConfig("id").rightAlign().collapse(),
	                new TableColumnConfig("uid").collapse(),
	                new TableColumnConfig("createdTs").collapse(),
	                new TableColumnConfig("modifiedTs").collapse(),
	                new TableColumnConfig("date").desc(),
	                new TableColumnConfig("payment.date", "Payment"),
	                new TableColumnConfig("type").centerAlign(),
	                new TableColumnConfig("amount").rightAlign(),
	                new TableColumnConfig("currency").centerAlign(),
	                new TableColumnConfig("payee").sortBy(".display").expand(1.0f),              
	                new TableColumnConfig("externalReference").collapse(),
	                new TableColumnConfig("description").collapse()
	                );
	       
	   ActionHelper.setDefaultListViewActions(listview, "expenseEditorView");
		return listview;
	}
}
