package w.wexpense.vaadin.view;

import w.wexpense.model.Payment;
import w.wexpense.model.PaymentDta;

import com.vaadin.addon.jpacontainer.JPAContainer;
import com.vaadin.ui.Table;
import com.vaadin.ui.VerticalLayout;

public class PaymentDtaEditor extends VerticalLayout {

	private static final long serialVersionUID = -2981650003785171306L;
	
	protected Table table;
	
	protected JPAContainer<PaymentDta> jpaContainer;
	
	public PaymentDtaEditor() {
		
	}
	
	public void setInstance(Payment payment) {		
	
	}
}
