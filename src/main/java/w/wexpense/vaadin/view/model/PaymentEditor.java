package w.wexpense.vaadin.view.model;

import java.text.MessageFormat;
import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;

import w.wexpense.dta.DtaHelper;
import w.wexpense.model.Expense;
import w.wexpense.model.Payment;
import w.wexpense.service.PaymentDtaService;
import w.wexpense.vaadin.WexWindow;
import w.wexpense.vaadin.view.OneToManyEditor;

import com.vaadin.addon.jpacontainer.JPAContainer;
import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.fieldgroup.FieldGroup.CommitException;
import com.vaadin.server.ExternalResource;
import com.vaadin.ui.AbstractOrderedLayout;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Field;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Link;
import com.vaadin.ui.UI;

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
		
		generateDtaButton = new Button("Generate DTA", (Button.ClickListener) this);
		viewDtaButton = new Button("View DTA", (Button.ClickListener) this);
		downloadDtaButton = new Link("Download DTA", new ExternalResource("/wexpenses/web/dta?uid=" + getItem().getBean().getUid()));
		downloadDtaButton.setTargetName("_blank");
		
		dtaLayout.addComponent(generateDtaButton);
		dtaLayout.addComponent(viewDtaButton);
		dtaLayout.addComponent(downloadDtaButton);

		if (getItem().getBean().isNew()) {
			generateDtaButton.setVisible(false);
			viewDtaButton.setVisible(false);
			downloadDtaButton.setVisible(false);
		}
	}
	
	@Override
	public void attach() {
		super.attach();
		
		addListener();
	}
	
	protected void addListener() {
		// attach user interaction listener
		getForm().getField("date").addValueChangeListener(new Property.ValueChangeListener() {
			@Override
			public void valueChange(ValueChangeEvent event) {
				Field f = getForm().getField("filename");
				if (f.getValue() == null || f.getValue().toString().trim().length() == 0) {
					Date newDate = (Date) event.getProperty().getValue();
					String filename = MessageFormat.format("{0,date,yyyy-MM-dd}_{1}.DTA", newDate, DtaHelper.APPLICATION_ID);
					f.setValue(filename);
				}
			}
		});
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

	@Override
	public void saveOnly() throws CommitException {
		super.saveOnly();
		
		generateDtaButton.setVisible(true);
		viewDtaButton.setVisible(true);
		downloadDtaButton.setVisible(true);
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
		UI.getCurrent().addWindow(new WexWindow(editor));
	}
	
	public PaymentDtaEditor newPaymentDtaEditor() {
		return null;
	}
}
