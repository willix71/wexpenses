package w.wexpense.vaadin7.view.model;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import w.wexpense.model.Payee;
import w.wexpense.service.model.PayeeService;
import w.wexpense.vaadin7.view.EditorView;

@Component
@Scope("prototype")
public class PayeeEditorView extends EditorView<Payee, Long> {

	@Autowired
	public PayeeEditorView(PayeeService service) {
	   super(service);
	   
	   setProperties("fullId","uid","type","prefix","name","address1","address2","city","externalReference","bankDetails");
	}

}
