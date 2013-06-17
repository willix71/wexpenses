package w.wexpense.vaadin7.view.model;

import java.io.Serializable;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

import w.wexpense.model.TransactionLine;
import w.wexpense.service.StorableService;
import w.wexpense.vaadin7.action.ActionHandler;
import w.wexpense.vaadin7.action.AddNewAction;
import w.wexpense.vaadin7.action.EditAction;
import w.wexpense.vaadin7.action.RefreshAction;
import w.wexpense.vaadin7.support.TableColumnConfig;
import w.wexpense.vaadin7.view.EditorView;
import w.wexpense.vaadin7.view.ListView;

import com.vaadin.addon.jpacontainer.JPAContainerItem;
import com.vaadin.ui.Table;

@Configuration
public class TransactionLineConfiguration {

	@Autowired
	@Qualifier("transactionLineService") 
	private StorableService<TransactionLine, Long> transactionLineService;
	
	@Bean
	@Scope("prototype")
	public EditorView<TransactionLine, Long> transactionLineEditorView() {
		EditorView<TransactionLine, Long> editorview = new EditorView<TransactionLine, Long>(transactionLineService);
		editorview.setProperties("fullId","uid","expense","account","discriminator","factor","amount","value","consolidatedDate","consolidation","description");
		return editorview;
	}
	
	@Bean
	@Scope("prototype")
	public ListView<TransactionLine> transactionLineListView() {
		ListView<TransactionLine> listview = new ListView<TransactionLine>(TransactionLine.class);
		listview.setColumnConfigs(
				   new TableColumnConfig("fullId").collapse().rightAlign(),
				   new TableColumnConfig("uid").collapse(),
				   new TableColumnConfig("createdTs").collapse(),
				   new TableColumnConfig("modifiedTs").collapse(),

				   new TableColumnConfig("expense.date", "Date").rightAlign().asc(),
				   new TableColumnConfig("expense.payee", "Payee").expand(1.0f),
				   new TableColumnConfig("account"),
				   new TableColumnConfig("discriminator"),
				   new TableColumnConfig("amount"),
				   new TableColumnConfig("expense.currency", "Currency"),
				   new TableColumnConfig("inValue", "In"),
				   new TableColumnConfig("outValue", "Out"),
				   new TableColumnConfig("description").collapse(),
				   new TableColumnConfig("date", "consolidated\nDate").collapse(),
				   new TableColumnConfig("consolidation.date", "consolidation\nDate").collapse()
				   );
		   
		listview.setActionHandler(getTransactionLineActionHandler("expenseEditorView"));
		
		return listview;
	}
	
	public ActionHandler getTransactionLineActionHandler(String editorName) {
		ActionHandler handler = new ActionHandler();
		handler.addListViewAction(new AddNewAction(editorName));
		handler.addListViewAction(new EditAction(editorName) {
			@Override
			public Serializable getInstanceId(Object sender, Object target) {
				com.vaadin.data.Container c = ((Table) sender) .getContainerDataSource();
				return ((JPAContainerItem<TransactionLine>) c.getItem(target)).getEntity().getExpense().getId();
			}
		}, true);
		handler.addListViewAction(new RefreshAction());
		return handler;
		
	}
	
}
