package w.wexpense.vaadin.view;

import java.math.BigDecimal;
import java.text.MessageFormat;
import java.util.Collection;

import javax.persistence.EntityManager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import w.wexpense.model.Expense;
import w.wexpense.model.TransactionLine;
import w.wexpense.model.enums.TransactionLineEnum;
import w.wexpense.utils.TransactionLineUtils;
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
	private BigDecimal currentExpenseAmount = null;
	
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
		return new RelationalFieldFactory<TransactionLine>(propertyConfiguror, childJpaContainer, jpaContainerFactory, this) {
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
	
	private class AmountListener implements Property.ValueChangeListener {
		private static final long serialVersionUID = 2635280167376554849L;
		private Object rowId;
		public AmountListener(Object rowId) { this.rowId = rowId; }
		@Override
		public void valueChange(ValueChangeEvent event) {
			if (fireUpdateValues) {
				((TransactionLine) rowId).updateValue();
				updateTotals();
				ExpenseTransactionLineEditor.this.requestRepaint();
			}
		}
	};

	public void enableUpdateValues(boolean enable) {
		fireUpdateValues = enable;
	}
	
	public void setCurrentAmount(BigDecimal currentAmount) {
		this.currentExpenseAmount = currentAmount;
		updateTotals();
	}

	public void updateAmount(BigDecimal newExpenseAmount) {
		TransactionLineUtils.updateAmount((Collection<TransactionLine>) getChildContainer().getItemIds(), newExpenseAmount, this.currentExpenseAmount);
		this.currentExpenseAmount = newExpenseAmount;
		updateTotals();
		ExpenseTransactionLineEditor.this.requestRepaint();
	}
	
	public void updateTotals() {
		BigDecimal[] deltaAndTotalOut = TransactionLineUtils.getDeltaAndTotalOut((Collection<TransactionLine>) getChildContainer().getItemIds());
		
		String footer = deltaAndTotalOut[0].intValue()==0?
				MessageFormat.format("Amount {0,number,0.00}",deltaAndTotalOut[1]):
					MessageFormat.format("Diff {0,number,0.00}",deltaAndTotalOut[0]);
		table.setColumnFooter("amount", footer);
		table.requestRepaintAll();
	}

	@Override
	public TransactionLine newEntity() {
		TransactionLine newInstance = super.newEntity();
		
		BigDecimal[] deltaAndTotalOut = TransactionLineUtils.getDeltaAndTotalOut((Collection<TransactionLine>) getChildContainer().getItemIds());
		int delta = deltaAndTotalOut[0].intValue();
		if (delta > 0) {
			newInstance.setAmount(deltaAndTotalOut[0].abs());
			newInstance.setFactor(TransactionLineEnum.OUT);
		} else if (delta < 0) {
			newInstance.setAmount(deltaAndTotalOut[0].abs());
			newInstance.setFactor(TransactionLineEnum.IN);
		} else {
			if (deltaAndTotalOut[1].intValue() == 0) {
				newInstance.setAmount(newInstance.getExpense().getAmount());
				newInstance.setFactor(TransactionLineEnum.OUT);
			} else {
				newInstance.setAmount(deltaAndTotalOut[1]);
				newInstance.setFactor(TransactionLineEnum.IN);
			}
		}
		return newInstance;
	}
	
	@Override
	public void addEntity() {
		TransactionLine newInstance = newEntity();		
		getChildContainer().addBean(newInstance); 
	}
	
	@Override
	public void save(Expense parent, EntityManager em) {
		fireUpdateValues = false;
		super.save(parent, em);
	}
}