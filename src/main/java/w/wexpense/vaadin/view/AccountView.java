package w.wexpense.vaadin.view;

import w.wexpense.model.Account;
import w.wexpense.vaadin.PropertyConfiguror;

public class AccountView extends GenericView<Account> {

	private static final long serialVersionUID = 6499289439725418193L;

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
