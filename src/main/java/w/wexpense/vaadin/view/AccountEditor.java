package w.wexpense.vaadin.view;

import w.wexpense.model.Account;

import com.vaadin.addon.jpacontainer.JPAContainer;
import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeEvent;

public class AccountEditor extends GenericEditor<Account> {

	private static final long serialVersionUID = 701758651197792890L;
	
	public AccountEditor() {
		super(Account.class);
	}
	
	@Override
	public void attach() {
		super.attach();

		addListener();
	}
	
	public void setInstance(Account instance, JPAContainer<Account> jpaContainer) {
		super.setInstance(instance, jpaContainer);
		if (isNew()) {
			getItem().getBean().updateFullNameAndNumber();
		}
	}
		
	protected void addListener() {
		Property.ValueChangeListener listener = new Property.ValueChangeListener() {
			private static final long serialVersionUID = -1;
			@Override
			public void valueChange(ValueChangeEvent event) {
				getItem().getBean().updateFullNameAndNumber();
				getForm().getField("fullName").requestRepaint();
				getForm().getField("fullNumber").requestRepaint();
			}
		};
		getForm().getField("name").addListener(listener);	
		getForm().getField("number").addListener(listener);		
		getForm().getField("parent").addListener(listener);
	}
}
