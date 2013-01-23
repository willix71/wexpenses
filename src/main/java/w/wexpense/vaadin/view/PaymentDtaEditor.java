package w.wexpense.vaadin.view;

import org.springframework.beans.factory.annotation.Autowired;

import w.wexpense.model.Payment;
import w.wexpense.model.PaymentDta;
import w.wexpense.vaadin.PropertyConfiguror;
import w.wexpense.vaadin.WexJPAContainerFactory;
import w.wexpense.vaadin.fieldfactory.SimpleFieldFactory;

import com.vaadin.addon.jpacontainer.JPAContainer;
import com.vaadin.data.util.filter.Compare.Equal;
import com.vaadin.ui.Table;

public class PaymentDtaEditor extends ConfigurableView<PaymentDta> {

	private static final long serialVersionUID = -2981650003785171306L;
	
	@Autowired
	protected WexJPAContainerFactory jpaContainerFactory;
	
	protected JPAContainer<PaymentDta> jpaContainer;
	
	protected Table table;
	
	public PaymentDtaEditor() {
		super(PaymentDta.class);
	}
	
	public void setInstance(Payment payment) {
		this.jpaContainer = jpaContainerFactory.getJPAContainer(PaymentDta.class, propertyConfiguror.getPropertyValues(PropertyConfiguror.nestedProperties));
		this.jpaContainer.addContainerFilter(new Equal("payment", payment));
		this.jpaContainer.applyFilters();
		this.jpaContainer.setAutoCommit(true);
		
		this.table = new WexTable(jpaContainer, propertyConfiguror);
		this.table.setTableFieldFactory(new SimpleFieldFactory(propertyConfiguror));
		this.table.setSizeFull();
		this.table.setEditable(true);
		this.table.setImmediate(true);		
		this.table.setWriteThrough(true);
		
		addComponent(this.table);
	}	
}
