package w.wexpense.vaadin.view;

import w.wexpense.model.Account;

import com.vaadin.ui.Table;
import com.vaadin.ui.TreeTable;

public class AccountView extends GenericView<Account> {

	public AccountView() {
		super(Account.class);
	}
	
	@Override
   protected Table buildTable() {
	   return new TreeTable(null, this.jpaContainer);
   }
}
