package w.wexpense.vaadin.view;

import org.springframework.beans.factory.annotation.Autowired;

import w.wexpense.model.Expense;
import w.wexpense.model.Payment;
import w.wexpense.service.PaymentDtaService;

import com.vaadin.ui.AbstractOrderedLayout;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.HorizontalLayout;

public class PaymentEditor extends OneToManyEditor<Payment, Expense> {

	private static final long serialVersionUID = 7495790122461487109L;

	@Autowired
	private PaymentDtaService paymentDtaService;
	
	private Button generateDtaButton;
	private Button viewDtaButton;

	
	public PaymentEditor() {
		super(Payment.class);
	}

	@Override
	protected AbstractOrderedLayout buildButtons() {
		AbstractOrderedLayout saveCancel = super.buildButtons();
		
		HorizontalLayout otherLayout = new HorizontalLayout();
		
		generateDtaButton = new Button("Generate DTA", (Button.ClickListener) this);
		viewDtaButton = new Button("View DTA", (Button.ClickListener) this);
		otherLayout.addComponent(generateDtaButton);
		otherLayout.addComponent(viewDtaButton);
				
		HorizontalLayout combinedLayout = new HorizontalLayout();
		combinedLayout.addComponent(saveCancel);
		combinedLayout.addComponent(otherLayout);
		combinedLayout.setComponentAlignment(otherLayout, Alignment.MIDDLE_RIGHT);
		return combinedLayout;
	}
	
	@Override
	public void buttonClick(ClickEvent event) {
		if (event.getButton() == generateDtaButton) {
			try {
				generate();
			} catch(Exception e) {
				e.printStackTrace();
			}
		} else if (event.getButton() == viewDtaButton) {
			System.out.println("viewDta");
		} else {
			super.buttonClick(event);
		}
	}

	public void generate() throws Exception {
		// we need to refresh the instance else we can't follow lazy links because 
		// the instance is not attached to the current running entity manager
		Payment payment = reloadInstance(getItem().getBean());
		paymentDtaService.generateDtaPayment(payment);
	}
}
