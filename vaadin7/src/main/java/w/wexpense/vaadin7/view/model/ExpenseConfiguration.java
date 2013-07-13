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
