package w.wexpense.vaadin.view;

import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Map;

import javax.persistence.EntityManager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import w.wexpense.model.Account;
import w.wexpense.model.Currency;
import w.wexpense.model.ExchangeRate;
import w.wexpense.model.Expense;
import w.wexpense.model.TransactionLine;
import w.wexpense.model.enums.TransactionLineEnum;
import w.wexpense.vaadin.WexJPAContainerFactory;
import w.wexpense.vaadin.fieldfactory.OneToManyView;
import w.wexpense.vaadin.fieldfactory.RelationalFieldFactory;

import com.vaadin.addon.jpacontainer.JPAContainer;
import com.vaadin.data.Container;
import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.ui.Component;
import com.vaadin.ui.Field;
import com.vaadin.ui.Table;
import com.vaadin.ui.TableFieldFactory;
import com.vaadin.ui.TextField;

class ExpenseTransactionLineEditor extends OneToManyView<Expense, TransactionLine> {
	private static final long serialVersionUID = 701758651197792890L;
	
	private static final Logger LOGGER = LoggerFactory.getLogger(ExpenseEditor.class);
	
	private Table table;	
	private boolean fireUpdateValues = false;
	private Double currentExpenseAmount = null;
	private Map<Object, Field> amountFields = new HashMap<Object, Field>();
	
	public ExpenseTransactionLineEditor(String parentPropertyId, Class<TransactionLine> childType) {
		super(parentPropertyId, childType);
	}
	
	@Override
	protected Table buildTable() {
		table = super.buildTable();
		table.setPageLength(3);
		table.setFooterVisible(true);
		table.setImmediate(true);	
		return table;
	}

	@Override
	protected TableFieldFactory getTableFieldFactory(JPAContainer<TransactionLine> childJpaContainer, WexJPAContainerFactory jpaContainerFactory) {
		return new RelationalFieldFactory<TransactionLine>(childJpaContainer, jpaContainerFactory) {
			@Override
			public Field createField(Container container, Object itemId, Object propertyId, Component uiContext) {
				Field f = super.createField(container, itemId, propertyId, uiContext);
				if ("amount".equals(propertyId)) {
					((TextField) f).setImmediate(true);
					if (fireUpdateValues) {
						// if already firing events, add a listener now
						f.addListener(new AmountListener(itemId));
					} else {
						// defer the registering to later
						amountFields.put(itemId, f);
					}
				}
				return f;
			}
		};
	}
	
	class AmountListener implements Property.ValueChangeListener {
		private Object rowId;
		public AmountListener(Object rowId) { this.rowId = rowId; }
		@Override
		public void valueChange(ValueChangeEvent event) {
			Object newObject = event.getProperty().getValue();
			Double newAmount = 0.0;
			if (newObject != null) {
				if (newObject instanceof Number) {
					newAmount = ((Number) newObject).doubleValue();
				} else {
					newAmount = Double.valueOf(newObject.toString());
				}
			}
						
			updateValue(rowId, newAmount, true);
		}
	};
	
	@Override
	public void attach() {
		super.attach();

		// register the listeners
		for(Map.Entry<Object, Field> entry: amountFields.entrySet()) {
			entry.getValue().addListener(new AmountListener(entry.getKey()));
		}
	}

	public void enableUpdateValues(boolean enable) {
		fireUpdateValues = enable;
	}
	
	public void setCurrentAmount(Double currentAmount) {
		updateValues(currentAmount);
		this.currentExpenseAmount = currentAmount;
	}

	private void updateValue(Object rowId, Double newRowAmount, boolean refresh) {
		LOGGER.debug("Updating transaction line value {}", fireUpdateValues);
		
		if (!fireUpdateValues) return;
		
		Container container = getContainer();
		Property rateProp = container.getItem(rowId).getItemProperty("exchangeRate");
		Property accountProp = container.getItem(rowId).getItemProperty("account");
		Property valueProp = container.getItem(rowId).getItemProperty("value");
		
		double newValue = newRowAmount;
		
		Currency currency = null;
		if (rateProp.getValue()!=null) {
			ExchangeRate rate = (ExchangeRate) rateProp.getValue();				
			newValue *= rate.getRate();
			
			// get the currency of the exchange rate
			currency = rate.getBuyCurrency();
		}
		if (currency==null && accountProp.getValue()!=null) {
			// fall back on the currency of the account
			currency = ((Account) accountProp.getValue()).getCurrency();
		}
		if (currency != null && currency.getRoundingFactor()!=null) {
			// perform rounding
			newValue = Math.rint(newValue*currency.getRoundingFactor())/currency.getRoundingFactor();
		}
		valueProp.setValue(newValue);
		
		if (refresh) {
			updateTotalValue();
		}
	}
	
	private void updateValues(Double newExpenseAmount) {
		LOGGER.debug("Updating transaction lines amount {}", fireUpdateValues);
				
		if (!fireUpdateValues) return;
		if (newExpenseAmount == null) return;
		
		Container container = getContainer();
		for(Object id: container.getItemIds()) {
			// get the item property
			Property amountProp = container.getItem(id).getItemProperty("amount");
			if (currentExpenseAmount.equals(amountProp.getValue())) {
				amountProp.setValue(newExpenseAmount);
				updateValue(id, newExpenseAmount, false);
			}
		}
		
		updateTotalValue();
	}
	
	private void updateTotalValue() {
		double total = 0;
		double totalIn = 0;
		
		Container container = getContainer();
		for(Object id: container.getItemIds()) {
			// get the item property
			Property factorProp = container.getItem(id).getItemProperty("factor");
			Property amountProp = container.getItem(id).getItemProperty("amount");
					
			double amount = amountProp.getValue() != null? (Double) amountProp.getValue(): 0;
			if (factorProp.getValue() != null) {
				TransactionLineEnum factor = (TransactionLineEnum)factorProp.getValue();
				
				total += factor.getFactor() * amount;
				if (TransactionLineEnum.IN == factor) {
					totalIn += amount;
				}
			}
		}
		
		String footer = total==0?
				MessageFormat.format("Amount {0,number,0.00}",totalIn):
					MessageFormat.format("Diff {0,number,0.00}",total);
		table.setColumnFooter("amount", footer);
		table.requestRepaintAll();
	}

	@Override
	public void insert(Expense parent, EntityManager em) {
		fireUpdateValues = false;
		super.insert(parent, em);
	}
	
	@Override
	public void update(Expense parent, EntityManager em) {
		fireUpdateValues = false;
		super.update(parent, em);
	}
}