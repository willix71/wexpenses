package w.wexpense.vaadin7.view.model;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

import w.wexpense.model.Expense;
import w.wexpense.model.Payment;
import w.wexpense.service.StorableService;
import w.wexpense.vaadin7.action.ActionHandler;
import w.wexpense.vaadin7.action.ActionHelper;
import w.wexpense.vaadin7.action.AddMultiSelectionAction;
import w.wexpense.vaadin7.action.RemoveAction;
import w.wexpense.vaadin7.action.SimpleAction;
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
public class PaymentConfiguration {

	@Autowired
	@Qualifier("paymentService")
	private StorableService<Payment, Long> paymentService;

	@Bean
	@Scope("prototype")
	public EditorView<Payment, Long> paymentEditorView() {
      OneToManyField<Expense> expenses = new OneToManyField<Expense>(Expense.class, getExpenseTableColumnConfig());
      
      final EditorView<Payment, Long> editorview = new EditorView<Payment, Long>(paymentService);
		editorview.setProperties("fullId", "uid", "date", "filename", "selectable", "expenses");
		editorview.setField("expenses", expenses);
		
		AddMultiSelectionAction<Expense> addSelectionAction = new AddMultiSelectionAction<Expense>("paymentExpenseSelectorView") {
			@Override
         public Filter getFilter() {
				Filter filter = new IsNull("payment");
				Payment p = editorview.getInstance();
				if (!p.isNew()) {
					filter = new Or(filter, new Compare.Equal("payment", p));
				}
				return filter;
         }			
		};
      
      ActionHandler action = new ActionHandler();
		action.addListViewAction(addSelectionAction);
		action.addListViewAction(new RemoveAction<Expense>());
		expenses.setActionHandler(action);

		return editorview;
	}

	@Bean
	@Scope("prototype")
	public MultiSelectorView<Expense> paymentExpenseSelectorView() {
		MultiSelectorView<Expense> selector = new MultiSelectorView<Expense>(Expense.class);
		selector.setColumnConfigs(getExpenseTableColumnConfig());
		return selector;
	}

	private TableColumnConfig[] getExpenseTableColumnConfig() {
		return new TableColumnConfig[] {
				new TableColumnConfig("fullId").collapse().rightAlign(),
            new TableColumnConfig("uid").collapse(),
            new TableColumnConfig("createdTs").collapse(),
            new TableColumnConfig("modifiedTs").collapse(),
           
            new TableColumnConfig("date").desc(),
            new TableColumnConfig("type").centerAlign(),
            new TableColumnConfig("amount").rightAlign(),
            new TableColumnConfig("currency").centerAlign(),
            new TableColumnConfig("payee").expand(1.0f),           
            new TableColumnConfig("externalReference").collapse(),
            new TableColumnConfig("description").collapse()
		};
	}

	@Bean
	@Scope("prototype")
	public ListView<Payment> paymentListView() {
		ListView<Payment> listview = new ListView<Payment>(Payment.class);
		listview.setColumnConfigs(
				new TableColumnConfig("id").collapse().rightAlign(), 
				new TableColumnConfig("uid").collapse(),
		      new TableColumnConfig("createdTs").collapse(), 
		      new TableColumnConfig("modifiedTs").collapse(),

		      new TableColumnConfig("date").desc(), 
		      new TableColumnConfig("filename"), 
		      new TableColumnConfig("selectable"));

		ActionHelper.setDefaultListViewActions(listview, "paymentEditorView");
		return listview;
	}
}
