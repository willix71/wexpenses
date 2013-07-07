package w.wexpense.vaadin7.view.model;

import java.math.BigDecimal;
import java.text.MessageFormat;
import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Scope;

import com.vaadin.data.Container;
import com.vaadin.data.Container.Filter;
import com.vaadin.data.Container.ItemSetChangeEvent;
import com.vaadin.data.util.filter.Compare;
import com.vaadin.data.util.filter.IsNull;
import com.vaadin.data.util.filter.Or;

import w.wexpense.model.Consolidation;
import w.wexpense.model.TransactionLine;
import w.wexpense.service.StorableService;
import w.wexpense.utils.TransactionLineUtils;
import w.wexpense.vaadin7.action.ActionHandler;
import w.wexpense.vaadin7.action.AddMultiSelectionAction;
import w.wexpense.vaadin7.action.RemoveAction;
import w.wexpense.vaadin7.component.OneToManyField;
import w.wexpense.vaadin7.container.OneToManyContainer;
import w.wexpense.vaadin7.view.EditorView;

@org.springframework.stereotype.Component
@Scope("prototype")
public class ConsolidationEditorView extends EditorView<Consolidation, Long> {

	private OneToManyField<TransactionLine> consolidationTransactionsField;
	
	@Autowired
	public ConsolidationEditorView(@Qualifier("consolidationService") StorableService<Consolidation, Long> storeService) {
	   super(storeService);
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

		// init action for one to many fields
		AddMultiSelectionAction<TransactionLine> addSelectionAction = new AddMultiSelectionAction<TransactionLine>("consolidationTransactionLineSelectorView") {
			@Override
         public Filter getFilter() {
				Filter filter = new IsNull("consolidation");
				Consolidation c = getInstance();
				if (!c.isNew()) {
					filter = new Or(filter, new Compare.Equal("consolidation", c));
				}
				return filter;
         }			
		};
      
      ActionHandler action = new ActionHandler();
		action.addListViewAction(addSelectionAction);
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
}
