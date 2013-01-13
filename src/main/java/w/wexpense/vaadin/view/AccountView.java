package w.wexpense.vaadin.view;

import w.wexpense.model.Account;
import w.wexpense.vaadin.PropertyConfiguror;

public class AccountView extends GenericView<Account> {

	public AccountView() {
		super(Account.class);
	}
	
	@Override
	protected void initTable() {
		this.jpaContainer = jpaContainerFactory.getJPAContainer(entityClass, propertyConfiguror.getPropertyValues(PropertyConfiguror.nestedProperties));
		this.jpaContainer.setParentProperty("parent");
		
		this.table = new WexTreeTable(jpaContainer, propertyConfiguror);
	}
}
