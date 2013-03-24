package w.wexpense.vaadin7.view.model;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import w.wexpense.model.Account;
import w.wexpense.service.model.AccountService;
import w.wexpense.vaadin7.view.EditorView;

import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.fieldgroup.BeanFieldGroup;
import com.vaadin.ui.AbstractComponent;
import com.vaadin.ui.Field;

@Component
@Scope("prototype")
public class AccountEditorView extends EditorView<Account, Long> {

	@Autowired
	public AccountEditorView(AccountService service) {
	   super(service);
	   
	   setProperties("fullId","uid","parent","name","fullName","number","fullNumber","type","externalReference","currency","selectable");
	}

	@Override
   public void setInstance(Account t) {
	   super.setInstance(t);
	   
	   final BeanFieldGroup<Account> bfg = super.fieldGroup;

	   Property.ValueChangeListener listener = new Property.ValueChangeListener() {
			private static final long serialVersionUID = -1;
			@Override
			public void valueChange(ValueChangeEvent event) {
				System.out.println(event.getProperty().getValue());
				System.out.println(bfg.getField("name").getValue());
				System.out.println(bfg.getField("name").getPropertyDataSource().getValue());
				bfg.getItemDataSource().getBean().updateFullNameAndNumber();
				System.out.println(bfg.getItemDataSource().getBean().getFullName());
				AccountEditorView.super.formLayout.markAsDirty();
				bfg.getField("fullName").markAsDirtyRecursive();
				bfg.getField("fullNumber").markAsDirtyRecursive();
			}
		};
	   
		Field<?> f = bfg.getField("name");
	   ((AbstractComponent) f).setImmediate(true);
	   f.setBuffered(false);
	   f.addValueChangeListener(listener);
	   
	   f = bfg.getField("number");	   
	   ((AbstractComponent) f).setImmediate(true);
	   f.setBuffered(false);
	   f.addValueChangeListener(listener);
	   
	   f = bfg.getField("parent");
	   ((AbstractComponent) f).setImmediate(true);
	   f.setBuffered(false);
	   f.addValueChangeListener(listener);
   }
	
}
