package w.wexpense.vaadin7.view.model;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

import w.wexpense.model.Expense;
import w.wexpense.vaadin7.action.ActionHelper;
import w.wexpense.vaadin7.filter.ExpenseFilter;
import w.wexpense.vaadin7.support.TableColumnConfig;
import w.wexpense.vaadin7.view.ListView;

@Configuration
public class ExpenseConfiguration {

	/*
	class AmountChangeListener implements Property.ValueChangeListener {
		Component viewer;
		Expense x;
		BigDecimal oldAmount;
		
		public void setViewer(Component viewer) {
			this.viewer = viewer;
		}
		
		public void setExpense(Expense x) {
			this.x = x;
			this.oldAmount = x.getAmount();
		}

		public void valueChange(Property.ValueChangeEvent event) {
			
			if (x.getTransactions() != null) {
				TransactionLineUtils.updateAmount(x.getTransactions(), x.getAmount(), oldAmount);
				//viewer.markAsDirtyRecursive();
				((OneToManyField) viewer).itemChange();

			}
			this.oldAmount = x.getAmount();
		}
	}
	
	@Autowired
	@Qualifier("expenseService") 
	private StorableService<Expense, Long> expenseService;
	
	@Bean
	@Scope("prototype")
	public EditorView<Expense, Long>  expenseEditorView() {
		final AmountChangeListener amountChangeListener = new AmountChangeListener();
		
		final OneToManyField<TransactionLine> expenseTransactionsField = expenseTransactionsField(amountChangeListener);
		
		final EditorView<Expense, Long> editorview = new EditorView<Expense, Long>(expenseService) {
			@Override
         public void initFields() {
	         super.initFields();
	         Field<?> f = fieldGroup.getField("amount");
	         ((AbstractComponent) f).setImmediate(true);
	         f.addValueChangeListener(amountChangeListener);
         }
			
			@Override
         public void initFields(Expense expense) {
	         super.initFields(expense);
	         amountChangeListener.setExpense(expense);
         }
      	
      };
      amountChangeListener.setViewer(expenseTransactionsField);
      
		editorview.setProperties("fullId","uid","createdTs","modifiedTs","type","payee","date","amount","currency","externalReference","description","payment", "transactions");
		editorview.setCustomField("transactions", expenseTransactionsField);
		
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
	OneToManyField<TransactionLine> expenseTransactionsField(final AmountChangeListener amountChangeListener) {
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
      		) {
      	 
      	protected void initField(Object rowId, Object colId, Property<?> property, Field<?> field) {
      		if ("amount".equals(colId)) {
      			((AbstractComponent) field).setImmediate(true);
      			field.addValueChangeListener(amountChangeListener);
      		}
    		}
      };
      transactions.setPageLength(5);
      transactions.setEditable(true);
      return transactions;
	}
	*/
	
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
	       
		listview.addFilterSource(getExpenseFilter());		
	   ActionHelper.setDefaultListViewActions(listview, "expenseEditorView");
		return listview;
	}
	
	@Bean
	@Scope("prototype")
	public ExpenseFilter getExpenseFilter() {
		return new ExpenseFilter();
	}
}
