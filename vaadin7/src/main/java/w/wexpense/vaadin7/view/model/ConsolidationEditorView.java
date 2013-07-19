package w.wexpense.vaadin7.view.model;

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
import w.wexpense.vaadin7.action.EditConsolidationTransactionLineAction;
import w.wexpense.vaadin7.action.RemoveAction;
import w.wexpense.vaadin7.component.OneToManyField;
import w.wexpense.vaadin7.container.OneToManyContainer;
import w.wexpense.vaadin7.filter.FilterHelper;
import w.wexpense.vaadin7.view.EditorView;
import w.wexpense.vaadin7.view.MultiSelectorView;

import com.vaadin.data.Container;
import com.vaadin.data.Container.Filter;
import com.vaadin.data.Container.ItemSetChangeEvent;
import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.util.filter.And;
import com.vaadin.data.util.filter.Compare;
import com.vaadin.data.util.filter.Or;

@org.springframework.stereotype.Component
@Scope("prototype")
public class ConsolidationEditorView extends EditorView<Consolidation, Long> {

	@Log
	private static Logger LOGGER;
	
	@Autowired
	private EditConsolidationTransactionLineAction editConsolidationTransactionLineAction;
	
	private class AddMultiTransactionLinesAction extends AddMultiSelectionAction<TransactionLine> {
		public AddMultiTransactionLinesAction(boolean resetMode) {
			super("consolidationTransactionLineSelectorView", resetMode);			
		} 
		
		@Override
		public void prepareSelector(MultiSelectorView<TransactionLine> selector, OneToManyContainer<TransactionLine> container, boolean resetMode) {
			Filter f = FilterHelper.and(getAccountFilter(),ActionHelper.parentFilter("consolidation", ConsolidationEditorView.this.getInstance()));
			
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

		this.setProperties("fullId","uid","date","institution","openingBalance","closingBalance","deltaBalance","transactions");
		this.setCustomField("transactions",  consolidationTransactionsField);
	}
	
	@Override
   public void initFields() {
	   super.initFields();
	   Property.ValueChangeListener listener = new Property.ValueChangeListener() {
			@Override
			public void valueChange(ValueChangeEvent event) {
				updateDelta();				
			}
		};
	   fieldGroup.getField("openingBalance").addValueChangeListener(listener);
	   fieldGroup.getField("closingBalance").addValueChangeListener(listener);
   }

	private OneToManyField<TransactionLine> initConsolidationTransactionsField() {
		final OneToManyField<TransactionLine> tlField = new OneToManyField<TransactionLine>(TransactionLine.class, super.persistenceService, ConsolidationConfiguration.getTransactionLinesTableColumnConfig());
     
		
		
      ActionHandler action = new ActionHandler();
		action.addListViewAction(new AddMultiTransactionLinesAction(true));
		action.addListViewAction(new AddMultiTransactionLinesAction(false));
		action.addListViewAction(editConsolidationTransactionLineAction);
		action.addListViewAction(new RemoveAction<TransactionLine>());
		tlField.setActionHandler(action);
		
      // set footer
		tlField.addFooterListener(new Container.ItemSetChangeListener() {
			@Override
			public void containerItemSetChange(ItemSetChangeEvent event) {
				@SuppressWarnings("unchecked")
				OneToManyContainer<TransactionLine> otmContainer = (OneToManyContainer<TransactionLine>) event.getContainer();
				Collection<TransactionLine> tls = otmContainer.getBeans();
				BigDecimal[] values = TransactionLineUtils.getValueDeltaAndTotals(otmContainer.getBeans());
				tlField.setFooter("outValue", MessageFormat.format("{0,number,0.00}",values[1]));
				tlField.setFooter("inValue", MessageFormat.format("{0,number,0.00}",values[2]));				
				tlField.setFooter("payee", MessageFormat.format("{0,number,0} X",tls.size()));
				
				updateDelta(values[0]);				
			}
		});
      
		return tlField;
	}
	
	private void updateDelta() {
		BigDecimal[] values = TransactionLineUtils.getValueDeltaAndTotals(getInstance().getTransactions());
		updateDelta(values[0]);	
	}
	
	private void updateDelta(BigDecimal delta) {
		getInstance().updateDeltaBalance(delta);				
		fieldGroup.getField("deltaBalance").markAsDirty();
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
