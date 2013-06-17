package w.wexpense.vaadin7.view.model;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

import w.wexpense.model.Consolidation;
import w.wexpense.model.Expense;
import w.wexpense.model.TransactionLine;
import w.wexpense.service.StorableService;
import w.wexpense.vaadin7.action.ActionHandler;
import w.wexpense.vaadin7.action.ActionHelper;
import w.wexpense.vaadin7.action.AddMultiSelectionAction;
import w.wexpense.vaadin7.action.RemoveAction;
import w.wexpense.vaadin7.component.OneToManyField;
import w.wexpense.vaadin7.support.TableColumnConfig;
import w.wexpense.vaadin7.view.EditorView;
import w.wexpense.vaadin7.view.ListView;
import w.wexpense.vaadin7.view.MultiSelectorView;

import com.vaadin.data.Container.Filter;
import com.vaadin.data.util.filter.Compare;
import com.vaadin.data.util.filter.IsNull;
import com.vaadin.data.util.filter.Or;

@Configuration
public class ConsolidationConfiguration {

	@Autowired
	@Qualifier("consolidationService")
	private StorableService<Consolidation, Long> consolidationService;
	
	@Bean
	@Scope("prototype")
	public EditorView<Consolidation, Long> consolidationEditorView() {
		OneToManyField<TransactionLine> transactions = new OneToManyField<>(TransactionLine.class, getTransactionLinesTableColumnConfig());
		
		final EditorView<Consolidation, Long> editorview = new EditorView<Consolidation, Long>(consolidationService);
		editorview.setProperties("fullId","uid","date","institution","openingBalance","closingBalance","transactions");
		editorview.setField("transactions",  transactions);
		
		AddMultiSelectionAction<TransactionLine> addSelectionAction = new AddMultiSelectionAction<TransactionLine>("consolidationTransactionLineSelectorView") {
			@Override
         public Filter getFilter() {
				Filter filter = new IsNull("consolidation");
				Consolidation c = editorview.getInstance();
				if (!c.isNew()) {
					filter = new Or(filter, new Compare.Equal("consolidation", c));
				}
				return filter;
         }			
		};
      
      ActionHandler action = new ActionHandler();
		action.addListViewAction(addSelectionAction);
		action.addListViewAction(new RemoveAction<Expense>());
		transactions.setActionHandler(action);
		
		return editorview;
	}
	
	@Bean
	@Scope("prototype")
	public MultiSelectorView<TransactionLine> consolidationTransactionLineSelectorView() {
		MultiSelectorView<TransactionLine> selector = new MultiSelectorView<TransactionLine>(TransactionLine.class);
		selector.setColumnConfigs(getTransactionLinesTableColumnConfig());				
		return selector;
	}
	
	private TableColumnConfig[] getTransactionLinesTableColumnConfig() {
		return new TableColumnConfig[] {
				new TableColumnConfig("fullId").collapse().rightAlign(),
			   new TableColumnConfig("uid").collapse(),
			   new TableColumnConfig("createdTs").collapse(),
			   new TableColumnConfig("modifiedTs").collapse(),

			   new TableColumnConfig("consolidatedDate").desc(),
			   new TableColumnConfig("amount").rightAlign(),
			   new TableColumnConfig("exchangeRate").rightAlign(),
			   new TableColumnConfig("payee").expand(1.0f),			   
			   new TableColumnConfig("inValue").rightAlign(),
			   new TableColumnConfig("outValue").rightAlign()
		};
	}
	
	@Bean
	@Scope("prototype")
	public ListView<Consolidation> consolidationListView() {
		ListView<Consolidation> listview = new ListView<Consolidation>(Consolidation.class);
		listview.setColumnConfigs(
			   new TableColumnConfig("fullId").collapse().rightAlign(),
			   new TableColumnConfig("uid").collapse(),
			   new TableColumnConfig("createdTs").collapse(),
			   new TableColumnConfig("modifiedTs").collapse(),
			   
			   new TableColumnConfig("date").desc(),
			   new TableColumnConfig("institution").expand(1.0f),
			   new TableColumnConfig("openingBalance"),
			   new TableColumnConfig("closingBalance")
			   );
		   
		ActionHelper.setDefaultListViewActions(listview, "consolidationEditorView");
		return listview;
	}
}
