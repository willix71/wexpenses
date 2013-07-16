package w.wexpense.vaadin7.view.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;

import w.log.extras.Log;
import w.wexpense.model.Account;
import w.wexpense.model.Consolidation;
import w.wexpense.model.TransactionLine;
import w.wexpense.service.model.IConsolidationService;
import w.wexpense.utils.TransactionLineUtils;
import w.wexpense.vaadin7.action.ActionHandler;
import w.wexpense.vaadin7.action.ActionHelper;
import w.wexpense.vaadin7.action.AddMultiSelectionAction;
import w.wexpense.vaadin7.action.EditAction;
import w.wexpense.vaadin7.action.RemoveAction;
import w.wexpense.vaadin7.component.OneToManyField;
import w.wexpense.vaadin7.container.OneToManyContainer;
import w.wexpense.vaadin7.view.EditorView;
import w.wexpense.vaadin7.view.MultiSelectorView;

import com.vaadin.data.Container;
import com.vaadin.data.Container.Filter;
import com.vaadin.data.Container.ItemSetChangeEvent;
import com.vaadin.data.util.BeanItem;
import com.vaadin.data.util.filter.And;
import com.vaadin.data.util.filter.Compare;
import com.vaadin.data.util.filter.Or;
import com.vaadin.ui.Table;

@org.springframework.stereotype.Component
@Scope("prototype")
public class ConsolidationEditorView extends EditorView<Consolidation, Long> {

	@Log
	private static Logger LOGGER;
	
	private class AddMultiTransactionLinesAction extends AddMultiSelectionAction<TransactionLine> {
		public AddMultiTransactionLinesAction(boolean resetMode) {
			super("consolidationTransactionLineSelectorView", resetMode);
			
		} 
		
		@Override
		public void prepareSelector(MultiSelectorView<TransactionLine> selector, OneToManyContainer<TransactionLine> container, boolean resetMode) {
			Filter f = getAccountFilter();
			if (f == null) {
				f = ActionHelper.parentFilter("consolidation", ConsolidationEditorView.this.getInstance());
			} else {
				f = new And(f, ActionHelper.parentFilter("consolidation", ConsolidationEditorView.this.getInstance()));
			}
			
			if (!resetMode && !container.isEmpty()) {
				f = new And(f, ActionHelper.excludeFilter(container.getBeans()));
			}
			selector.setFilter(f);
			
			if (resetMode && !container.isEmpty()) {
				selector.setValues(container.getBeans());
			}
      }			
	};
		
	private OneToManyField<TransactionLine> consolidationTransactionsField;
	
	private IConsolidationService consolidationService;
	
	@Autowired
	public ConsolidationEditorView(IConsolidationService consolidationService) {
	   super(consolidationService);
	   this.consolidationService = consolidationService;
	}
	
	@Override
	public void postConstruct() {
		super.postConstruct();
		
		consolidationTransactionsField = initConsolidationTransactionsField();

		this.setProperties("fullId","uid","date","institution","openingBalance","closingBalance","transactions");
		this.setCustomField("transactions",  consolidationTransactionsField);

		
	}
	
	private OneToManyField<TransactionLine> initConsolidationTransactionsField() {
		final OneToManyField<TransactionLine> tlField = new OneToManyField<TransactionLine>(TransactionLine.class, super.persistenceService, ConsolidationConfiguration.getTransactionLinesTableColumnConfig());
     
		EditAction editAction = new EditAction("expenseEditorView") {
			@Override
			public Serializable getInstanceId(Object sender, Object target) {
				com.vaadin.data.Container c = ((Table) sender) .getContainerDataSource();
				@SuppressWarnings("unchecked")
				BeanItem<TransactionLine> i = (BeanItem<TransactionLine>) c.getItem(target);
				return i.getBean().getExpense().getId();
			}
		};
		
      ActionHandler action = new ActionHandler();
		action.addListViewAction(new AddMultiTransactionLinesAction(true));
		action.addListViewAction(new AddMultiTransactionLinesAction(false));
		action.addListViewAction(editAction);
		action.addListViewAction(new RemoveAction<TransactionLine>());
		tlField.setActionHandler(action);
		
      // set footer
		tlField.addFooterListener(new Container.ItemSetChangeListener() {
			@Override
			public void containerItemSetChange(ItemSetChangeEvent event) {
				@SuppressWarnings("unchecked")
				OneToManyContainer<TransactionLine> otmContainer = (OneToManyContainer<TransactionLine>) event.getContainer();
				Collection<TransactionLine> tls = otmContainer.getBeans();
				BigDecimal[] values = TransactionLineUtils.getDeltaAndTotals(otmContainer.getBeans());
				tlField.setFooter("outValue", MessageFormat.format("{0,number,0.00}",values[1]));
				tlField.setFooter("inValue", MessageFormat.format("{0,number,0.00}",values[2]));				
				tlField.setFooter("payee", MessageFormat.format("{0,number,0} X",tls.size()));
			}
		});
      
		return tlField;
	}
	
	private Filter getAccountFilter() {
		List<Account> accounts = consolidationService.getConsolidationAccounts(getInstance().getInstitution());
		if (accounts != null && accounts.size() > 0) {
			List<Filter> filters = new ArrayList<Filter>();
			for(Account a: accounts) {
				filters.add(new Compare.Equal("account", a));
			}
			return new Or(filters.toArray(new Filter[filters.size()]));
		}
		
		return null;
	}
}
