package w.wexpense.vaadin7.view.model;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import w.wexpense.model.Expense;
import w.wexpense.model.TransactionLine;
import w.wexpense.service.model.ExpenseService;
import w.wexpense.vaadin7.component.OneToManyField;
import w.wexpense.vaadin7.view.EditorView;

import com.vaadin.ui.Field;

@Component
@Scope("prototype")
public class ExpenseEditorView extends EditorView<Expense, Long> {

	@Autowired
	public ExpenseEditorView(ExpenseService service) {
	   super(service);	  
	   setProperties("fullId","uid","createdTs","modifiedTs","type","payee","date","amount","currency","externalReference","description","payment");
	}


	public void setInstance(Expense t) {
		super.setInstance(t);
		
		Field<?> f = new OneToManyField<>(TransactionLine.class);
		fieldGroup.bind(f, "transactions");
		formLayout.addComponent(f);
	}
	
}
