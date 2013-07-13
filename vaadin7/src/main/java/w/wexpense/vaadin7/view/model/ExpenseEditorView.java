package w.wexpense.vaadin7.view.model;

import java.math.BigDecimal;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Scope;

import w.wexpense.model.ExchangeRate;
import w.wexpense.model.Expense;
import w.wexpense.model.Payee;
import w.wexpense.model.TransactionLine;
import w.wexpense.service.StorableService;
import w.wexpense.utils.TransactionLineUtils;
import w.wexpense.vaadin7.UIHelper;
import w.wexpense.vaadin7.WexUI;
import w.wexpense.vaadin7.action.ActionHandler;
import w.wexpense.vaadin7.action.ListViewAction;
import w.wexpense.vaadin7.action.RemoveAction;
import w.wexpense.vaadin7.component.ExchangeRateField;
import w.wexpense.vaadin7.component.OneToManyField;
import w.wexpense.vaadin7.container.OneToManyContainer;
import w.wexpense.vaadin7.event.FieldCreationEvent;
import w.wexpense.vaadin7.support.TableColumnConfig;
import w.wexpense.vaadin7.view.EditorView;

import com.vaadin.data.Container;
import com.vaadin.data.Container.ItemSetChangeEvent;
import com.vaadin.data.Property;
import com.vaadin.data.util.BeanItem;
import com.vaadin.ui.AbstractComponent;
import com.vaadin.ui.Field;
import com.vaadin.ui.Table;
import com.vaadin.ui.UI;

@org.springframework.stereotype.Component
@Scope("prototype")
public class ExpenseEditorView extends EditorView<Expense, Long> {

	private OneToManyField<TransactionLine> expenseTransactionsField;
	
	private BigDecimal oldAmount;
	
	private Property.ValueChangeListener amountListener = new Property.ValueChangeListener() {
		@Override
		public void valueChange(Property.ValueChangeEvent event) {
			updateAmount();
		}
	};
	
	private List<ExchangeRateField> exchangeRateFields = new ArrayList<ExchangeRateField>();
	private com.vaadin.ui.Component.Listener exchangeRateListener = new com.vaadin.ui.Component.Listener() {
		@Override
		public void componentEvent(com.vaadin.ui.Component.Event event) {			
			updateExchangeRate(((ExchangeRateField) event.getSource()).getValue());
		}
	};
	
	@Autowired
	public ExpenseEditorView(@Qualifier("expenseService") StorableService<Expense, Long> storeService) {
	   super(storeService);
	}
	
	
	@Override
	public void postConstruct() {
		super.postConstruct();

		expenseTransactionsField = initExpenseTransactionsField();
		expenseTransactionsField.setActionHandler(initExpenseTransactionsActionHanler());
		expenseTransactionsField.addListener(new com.vaadin.ui.Component.Listener() {
			private static final long serialVersionUID = 8121179082149508635L;

			@Override
			public void componentEvent(Event event) {
				if (event instanceof FieldCreationEvent && event.getComponent() == expenseTransactionsField) {
					FieldCreationEvent fcevnt = (FieldCreationEvent) event;
					if ("amount".equals(fcevnt.getColId())) {
						((AbstractComponent) fcevnt.getField()).setImmediate(true);
						fcevnt.getField().addValueChangeListener(amountListener);
					} else if ("exchangeRate".equals(fcevnt.getColId()) && fcevnt.getField() instanceof ExchangeRateField) {
						exchangeRateFields.add((ExchangeRateField) fcevnt.getField());
						fcevnt.getField().addListener(exchangeRateListener);
					} else if (("factor").equals(fcevnt.getColId())) {
						((AbstractComponent) fcevnt.getField()).setImmediate(true);
						fcevnt.getField().addValueChangeListener(new Property.ValueChangeListener() {
							@Override
							public void valueChange(Property.ValueChangeEvent event) {
								expenseTransactionsField.itemChange();
							}
						});
					}
				}
			}
		});    
		
		this.setProperties("fullId","uid","createdTs","modifiedTs","type","payee","date","amount","currency","externalReference","description","payment", "transactions");
		this.setCustomField("transactions", expenseTransactionsField);
   }
	
	@Override
	public void initFields() {
		UIHelper.addValueChangeListener(getField("amount"), amountListener);
		
		UIHelper.addValueChangeListener(getField("date"), new Property.ValueChangeListener() {
			@Override
			public void valueChange(Property.ValueChangeEvent event) {
				updateTransactionLineDate();
			}
		});

		final Field<?> currency = UIHelper.setImmediate(getField("currency"));
		
		UIHelper.addValueChangeListener(getField("payee"),new Property.ValueChangeListener() {
			@Override
			public void valueChange(Property.ValueChangeEvent event) {
				Payee payee = getInstance().getPayee();
				if (payee.getCity()!=null && payee.getCity().getCountry()!=null) {
					getInstance().setCurrency(payee.getCity().getCountry().getCurrency());
					currency.markAsDirty();
				}
			}
		});
	}
	
	@Override
   public void setInstance(Expense expense) {
      super.setInstance(expense);
      oldAmount = expense.getAmount();
   }
	
	private void updateExchangeRate(ExchangeRate newExchangeRate) {
		Expense x = getInstance();
		for (TransactionLine line : x.getTransactions()) {
			if (newExchangeRate!=null && newExchangeRate.equals(line.getExchangeRate())) {
				line.setExchangeRate(newExchangeRate);
			}
		}
		updateAmount() ;
	}
	
	private void updateAmount() {
		Expense x = getInstance();
		if (x.getTransactions() != null) {
			TransactionLineUtils.updateAmount(x.getTransactions(), x.getAmount(), oldAmount);
			expenseTransactionsField.itemChange();
		}
		oldAmount = x.getAmount();
	}
	
	private void updateTransactionLineDate() {
		Expense x = getInstance();
		Date xd = x.getDate();
		if (xd != null && x.getTransactions() != null) {
			for(TransactionLine line: x.getTransactions()) {
				if (line.getDate()==null || line.getDate().equals(xd)) {
					line.setDate(xd);
				}
			}
		}
	}
	
	private OneToManyField<TransactionLine> initExpenseTransactionsField() {
      final OneToManyField<TransactionLine> xtransactionsField = new OneToManyField<TransactionLine>(
      		TransactionLine.class, super.persistenceService, 
            new TableColumnConfig("fullId").collapse().rightAlign(),
            new TableColumnConfig("uid").collapse(),
            new TableColumnConfig("createdTs").collapse(),
            new TableColumnConfig("modifiedTs").collapse(),
           
            new TableColumnConfig("account"),
            new TableColumnConfig("discriminator"),
            new TableColumnConfig("factor").centerAlign().width(60),
            new TableColumnConfig("amount").rightAlign().width(100),
            new TableColumnConfig("exchangeRate"),
            new TableColumnConfig("value").rightAlign().width(100)
      		);
      xtransactionsField.setPageLength(5);
      xtransactionsField.setEditable(true);
		xtransactionsField.addFooterListener(new Container.ItemSetChangeListener() {			
			@Override
			public void containerItemSetChange(ItemSetChangeEvent event) {
				@SuppressWarnings("unchecked")
				OneToManyContainer<TransactionLine> otmContainer = (OneToManyContainer<TransactionLine>) event.getContainer();
				BigDecimal[] values = TransactionLineUtils.getDeltaAndTotals(otmContainer.getBeans());
				if (values[1].equals(values[2])) {
					xtransactionsField.setFooter("amount", MessageFormat.format("{0,number,0.00}",values[1]));
				} else {
					xtransactionsField.setFooter("amount", MessageFormat.format("Diff {0,number,0.00}",values[0]));
				}
			}
		});
		return xtransactionsField;
	}
	
	private ActionHandler initExpenseTransactionsActionHanler() {
		// set the expenseTransactionsField menu actions
		ListViewAction addAction = new ListViewAction("add") {			
			@Override
			public void handleAction(Object sender, Object target) {
				Table table = (Table) sender;
				
				@SuppressWarnings("unchecked")
            OneToManyContainer<TransactionLine> container = (OneToManyContainer<TransactionLine>) table.getContainerDataSource();
				Collection<TransactionLine> lines = container.getBeans();
				
				TransactionLine tl = TransactionLineUtils.newTransactionLine(getInstance(), lines);
				container.addBean(tl);				
			}
			
			@Override
			public boolean canHandle(Object target, Object sender) {
				return true;
			}
		};
      
		ListViewAction editAction = new ListViewAction("edit") {			
			@Override
			public void handleAction(Object sender, Object target) {
				if (target != null) {
					Table table = (Table) sender;
					
					@SuppressWarnings("unchecked")
					OneToManyContainer<TransactionLine> container = (OneToManyContainer<TransactionLine>) table.getContainerDataSource();
					
					BeanItem<TransactionLine> item = container.getItem(target);
					TransactionLineEditorView editor = ((WexUI) UI.getCurrent()).getBean(TransactionLineEditorView.class, "transactionLineEditorView");
					editor.setBeanItem(item);					
					UIHelper.displayModalWindow(editor);
				}
			}
			@Override
			public boolean canHandle(Object target, Object sender) {
				return target != null;
			}
		};
      ActionHandler actionHandler = new ActionHandler();
      actionHandler.addListViewAction(addAction);
      actionHandler.addListViewAction(editAction);
      actionHandler.addListViewAction(new RemoveAction<TransactionLine>());
      return actionHandler;
	}
}
