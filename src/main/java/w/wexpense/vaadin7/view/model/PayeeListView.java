package w.wexpense.vaadin7.view.model;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import ru.xpoft.vaadin.VaadinView;
import w.wexpense.model.Payee;
import w.wexpense.vaadin7.action.ActionHelper;
import w.wexpense.vaadin7.view.ListView;

@Component
@Scope("prototype")
@VaadinView(PayeeListView.NAME)
public class PayeeListView extends ListView<Payee> {

   public static final String NAME = "PayeeListView";
   
	public PayeeListView() {
	   super(Payee.class);
	   
	   setAlignProperties(new String[]{"=type","prefix","name","city"});
	   setSortProperties("+name");
	   
	   ActionHelper.setDefaultListViewActions(this, PayeeEditorView.class);
   }

}
