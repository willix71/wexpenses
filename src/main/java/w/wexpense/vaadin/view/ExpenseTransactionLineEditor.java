package w.wexpense.vaadin.view;

import java.text.MessageFormat;

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
import w.wexpense.vaadin.fieldfactory.RelationalFieldFactory;

import com.vaadin.addon.jpacontainer.JPAContainer;
import com.vaadin.data.Container;
import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.event.Action;
import com.vaadin.ui.Component;
import com.vaadin.ui.Field;
import com.vaadin.ui.TableFieldFactory;
import com.vaadin.ui.TextField;

class ExpenseTransactionLineEditor extends OneToManySubEditor<TransactionLine, Expense> {
	private static final long serialVersionUID = 701758651197792890L;
	
	private static final Logger LOGGER = LoggerFactory.getLogger(ExpenseEditor.class);
	

	private boolean fireUpdateValues = false;
	private Double currentExpenseAmount = null;
	
	public ExpenseTransactionLineEditor() {
		super(TransactionLine.class, "transactions");
		
		super.entitySelectedActions = new Action[] { addAction, deleteAction };
		super.noEntitySelectedActions = new Action[] { addAction };
	}
	
	@Override
	protected void buildTable() {
		super.buildTable();
		table.setFooterVisible(true);
	}

	@Override
	protected TableFieldFactory getTableFieldFactory(JPAContainer<TransactionLine> childJpaContainer, WexJPAContainerFactory jpaContainerFactory) {
		return new RelationalFieldFactory<TransactionLine>(propertyConfiguror, childJpaContainer, jpaContainerFactory) {
			@Override
			public Field createField(Container container, Object itemId, Object propertyId, Component uiContext) {
				Field f = super.createField(container, itemId, propertyId, uiContext);
				if ("amount".equals(propertyId)) {
					((TextField) f).setImmediate(true);
					f.addListener(new AmountListener(itemId));
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

	public void enableUpdateValues(boolean enable) {
		fireUpdateValues = enable;
	}
	
	public void setCurrentAmount(Double currentAmount) {
		updateValues(currentAmount);
		this.currentExpenseAmount = currentAmount;
	}

	private void updateValue(Object rowId, Double newRowAmount, boolean refresh) {
		LOGGER.debug("Updating transaction line value {}", fireUpdateValues);
		
		if (fireUpdateValues) {
			Container container = getChildContainer();
			Property rateProp = container.getItem(rowId).getItemProperty("exchangeRate");
			Property accountProp = container.getItem(rowId).getItemProperty("account");
			Property valueProp = container.getItem(rowId).getItemProperty("value");

			double newValue = newRowAmount == null ? 0 : newRowAmount;

			Currency currency = null;
			if (rateProp.getValue() != null) {
				ExchangeRate rate = (ExchangeRate) rateProp.getValue();
				newValue *= rate.getRate();

				// get the currency of the exchange rate
				currency = rate.getBuyCurrency();
			}
			if (currency == null && accountProp.getValue() != null) {
				// fall back on the currency of the account
				currency = ((Account) accountProp.getValue()).getCurrency();
			}
			if (currency != null && currency.getRoundingFactor() != null) {
				// perform rounding
				newValue = Math.rint(newValue * currency.getRoundingFactor()) / currency.getRoundingFactor();
			}
			valueProp.setValue(newValue);

			if (refresh) {
				updateTotalValue();
			}
		}
	}
	
	private void updateValues(Double newExpenseAmount) {
		LOGGER.debug("Updating transaction lines amount {}", fireUpdateValues);
				
		if (fireUpdateValues) {
			Container container = getChildContainer();
			for (Object id : container.getItemIds()) {
				// get the item property
				Property amountProp = container.getItem(id).getItemProperty("amount");
				if (currentExpenseAmount.equals(amountProp.getValue())) {
					amountProp.setValue(newExpenseAmount);
					updateValue(id, newExpenseAmount, false);
				}
			}
		}
		updateTotalValue();
	}
	
	public void updateTotalValue() {
		double total = 0;
		double totalIn = 0;
		
		Container container = getChildContainer();
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
	public void save(Expense parent, EntityManager em) {
		fireUpdateValues = false;
		super.save(parent, em);
	}
}