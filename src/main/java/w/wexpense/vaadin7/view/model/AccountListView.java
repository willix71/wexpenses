package w.wexpense.vaadin7.view.model;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import ru.xpoft.vaadin.VaadinView;
import w.wexpense.model.Account;
import w.wexpense.vaadin7.action.ActionHelper;
import w.wexpense.vaadin7.view.ParentableListView;
import w.wexpense.vaadin7.view.TableColumnConfig;

@Component
@Scope("prototype")
@VaadinView(AccountListView.NAME)
public class AccountListView extends ParentableListView<Account> {

   public static final String NAME = "AccountListView";
   
	public AccountListView() {
	   super(Account.class);
	   
	   setColumnConfigs(
			   new TableColumnConfig("id").collapse().rightAlign(),
			   new TableColumnConfig("uid").collapse(),
			   new TableColumnConfig("createdTs").collapse(),
			   new TableColumnConfig("modifiedTs").collapse(),

			   new TableColumnConfig("name"),
			   new TableColumnConfig("fullName"),
			   new TableColumnConfig("number"),
			   new TableColumnConfig("fullNumber").asc(),
			   new TableColumnConfig("type"),
			   new TableColumnConfig("currency.code"),
			   new TableColumnConfig("externalReference").collapse()
			   );
	   
	   ActionHelper.setDefaultListViewActions(this, AccountEditorView.class);
   }



}
