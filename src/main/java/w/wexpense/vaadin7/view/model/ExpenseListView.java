package w.wexpense.vaadin7.view.model;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import ru.xpoft.vaadin.VaadinView;
import w.wexpense.model.Expense;
import w.wexpense.vaadin7.view.ListView;

@Component
@Scope("prototype")
@VaadinView(ExpenseListView.NAME)
public class ExpenseListView extends ListView<Expense> {

   public static final String NAME = "ExpenseListView";
   
	public ExpenseListView() {
	   super(Expense.class);
	   
	   setAlignProperties("date","payment.date","=type",">amount","currency","payee");
	   setSortProperties("-date");
   }

}
