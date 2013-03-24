package w.wexpense.vaadin7.view.model;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import ru.xpoft.vaadin.VaadinView;
import w.wexpense.model.Expense;
import w.wexpense.vaadin7.view.ListView;
import w.wexpense.vaadin7.view.TableColumnConfig;

@Component
@Scope("prototype")
@VaadinView(ExpenseListView.NAME)
public class ExpenseListView extends ListView<Expense> {

   public static final String NAME = "ExpenseListView";
   
	public ExpenseListView() {
	   super(Expense.class);
	   
	   setColumnConfigs(
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
   }

}
