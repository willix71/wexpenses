package w.wexpense.vaadin7.view.model;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import ru.xpoft.vaadin.VaadinView;
import w.wexpense.model.Payee;
import w.wexpense.vaadin7.action.ActionHelper;
import w.wexpense.vaadin7.view.ListView;
import w.wexpense.vaadin7.view.TableColumnConfig;

@Component
@Scope("prototype")
@VaadinView(PayeeListView.NAME)
public class PayeeListView extends ListView<Payee> {

   public static final String NAME = "PayeeListView";
   
	public PayeeListView() {
	   super(Payee.class);
	   
	   setColumnConfigs(
			   new TableColumnConfig("id").collapse().rightAlign(),
			   new TableColumnConfig("uid").collapse(),
			   new TableColumnConfig("createdTs").collapse(),
			   new TableColumnConfig("modifiedTs").collapse(),

			   new TableColumnConfig("type").centerAlign().sortBy(".name"),
			   new TableColumnConfig("prefix"),
			   new TableColumnConfig("name").asc(),
			   new TableColumnConfig("city").sortBy(".display"),
			   new TableColumnConfig("externalReference").collapse()
			   );
	   
	   ActionHelper.setDefaultListViewActions(this, PayeeEditorView.class);
   }

}
