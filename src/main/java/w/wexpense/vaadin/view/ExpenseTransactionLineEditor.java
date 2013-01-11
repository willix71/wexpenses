package w.wexpense.vaadin.view;

import java.text.MessageFormat;

import javax.persistence.EntityManager;

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
	
	private Table table;
	
	private boolean fireUpdateValues = false;
	
	public ExpenseTransactionLineEditor(String parentPropertyId, Class<TransactionLine> childType) {
		super(parentPropertyId, childType);
	}
	
	public void setInstance(Expense parentEntity, JPAContainer<Expense> parentJpaContainer) {
		super.setInstance(parentEntity, parentJpaContainer);
		fireUpdateValues = true;
	}
	
	@Override
	protected Table buildTable() {
		table = super.buildTable();
		table.setPageLength(3);
		table.setFooterVisible(true);
		table.setImmediate(true);	
		return table;
	}

	Property.ValueChangeListener updateValuesListener = new Property.ValueChangeListener() {
		@Override
		public void valueChange(ValueChangeEvent event) {
			if (fireUpdateValues) updateValues();
		}
	};

	@Override
	protected TableFieldFactory getTableFieldFactory(JPAContainer<TransactionLine> childJpaContainer, WexJPAContainerFactory jpaContainerFactory) {
		return new RelationalFieldFactory<TransactionLine>(childJpaContainer, jpaContainerFactory) {
			@Override
			public Field createField(Container container, Object itemId, Object propertyId, Component uiContext) {
				Field f = super.createField(container, itemId, propertyId, uiContext);
				if ("amount".equals(propertyId)) {
					((TextField) f).setImmediate(true);
					f.addListener(updateValuesListener);
				}
//	TODO hock up the combobox				
//				if ("exchangeRate".equals(propertyId)) {
//					Property p = f.getPropertyDataSource();
//				}
				return f;
			}
		};
	}
	
	public void updateValues() {
		double total = 0;
		Container container = getContainer();
		for(Object id: container.getItemIds()) {
			// get the item property
			Property factorProp = container.getItem(id).getItemProperty("factor");
			Property amountProp = container.getItem(id).getItemProperty("amount");
			Property rateProp = container.getItem(id).getItemProperty("exchangeRate");
			Property accountProp = container.getItem(id).getItemProperty("account");
			Property valueProp = container.getItem(id).getItemProperty("value");
			
			if (amountProp.getValue() == null) continue;
			Double amount = (Double) amountProp.getValue();
			TransactionLineEnum factor = (TransactionLineEnum) factorProp.getValue();
			if (factor != null) {
				total += factor.getFactor() * amount;
			}
			
			double value = amount;

			Currency currency = null;
			if (rateProp.getValue()!=null) {
				ExchangeRate rate = (ExchangeRate) rateProp.getValue();				
				value *= rate.getRate();
				
				currency = rate.getBuyCurrency();
			}
			if (currency == null && accountProp.getValue()!=null) {
				currency = ((Account) accountProp.getValue()).getCurrency();
			}
			if (currency != null && currency.getRoundingFactor()!=null) {
				value = Math.rint(value*currency.getRoundingFactor())/currency.getRoundingFactor();
			}
			valueProp.setValue(value);
		}	

		table.setColumnFooter("amount", MessageFormat.format("{0,number,0.00}",total));
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