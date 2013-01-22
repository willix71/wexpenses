package w.wexpense.vaadin.view;

import org.springframework.beans.factory.annotation.Autowired;

import w.wexpense.model.Expense;
import w.wexpense.model.Payment;
import w.wexpense.service.PaymentDtaService;
import w.wexpense.vaadin.ClosableWindow;

import com.vaadin.addon.jpacontainer.JPAContainer;
import com.vaadin.terminal.ExternalResource;
import com.vaadin.ui.AbstractOrderedLayout;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Link;

public class PaymentEditor extends OneToManyEditor<Payment, Expense> {

	private static final long serialVersionUID = 7495790122461487109L;

	@Autowired
	private PaymentDtaService paymentDtaService;
	
	private HorizontalLayout dtaLayout = new HorizontalLayout();;
	private Button generateDtaButton;
	private Button viewDtaButton;
	private Link downloadDtaButton;
	
	public PaymentEditor() {
		super(Payment.class);
	}

	@Override
	protected AbstractOrderedLayout buildButtons() {
		AbstractOrderedLayout saveCancel = super.buildButtons();

		HorizontalLayout combinedLayout = new HorizontalLayout();
		combinedLayout.addComponent(saveCancel);
		combinedLayout.addComponent(dtaLayout);
		combinedLayout.setComponentAlignment(dtaLayout, Alignment.MIDDLE_RIGHT);
		return combinedLayout;
	}
	
	@Override
	public void setInstance(Payment instance, JPAContainer<Payment> jpaContainer) {
		super.setInstance(instance, jpaContainer);
		
		if (! getItem().getBean().isNew()) {
			generateDtaButton = new Button("Generate DTA", (Button.ClickListener) this);
			viewDtaButton = new Button("View DTA", (Button.ClickListener) this);
			downloadDtaButton = new Link("Download DTA", new ExternalResource("/wexpenses/web/dta?uid="+instance.getUid()));
			downloadDtaButton.setTargetName("_blank");
			
			dtaLayout.addComponent(generateDtaButton);
			dtaLayout.addComponent(viewDtaButton);
			dtaLayout.addComponent(downloadDtaButton);
		}
	}
	
	@Override
	public void buttonClick(ClickEvent event) {
		if (event.getButton() == generateDtaButton) {
			try {
				generateDtas();
			} catch(Exception e) {
				e.printStackTrace();
			}
		} else if (event.getButton() == viewDtaButton) {
			viewDtas();
		} else {
			super.buttonClick(event);
		}
	}

	public void generateDtas() throws Exception {
		// we need to refresh the instance else we can't follow lazy links because 
		// the instance is not attached to the current running entity manager
		Payment payment = reloadInstance(getItem().getBean());
		paymentDtaService.generatePaymentDtas(payment);
	}
	
	public void viewDtas() {
		PaymentDtaEditor editor = newPaymentDtaEditor();
		editor.setInstance(getItem().getBean());
		getApplication().getMainWindow().addWindow(new ClosableWindow(editor, "128ex",null));
	}
	
	public PaymentDtaEditor newPaymentDtaEditor() {
		return null;
	}
}
