package w.wexpense.vaadin7.view.model;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

import w.wexpense.model.Payee;
import w.wexpense.service.StorableService;
import w.wexpense.vaadin7.action.ActionHelper;
import w.wexpense.vaadin7.filter.DisplayFilter;
import w.wexpense.vaadin7.support.TableColumnConfig;
import w.wexpense.vaadin7.view.EditorView;
import w.wexpense.vaadin7.view.ListView;

@Configuration
public class PayeeConfiguration {

	@Autowired
	@Qualifier("payeeService") 
	private StorableService<Payee, Long> payeeService;
	
	@Bean
	@Scope("prototype")
	public EditorView<Payee, Long> payeeEditorView() {
		EditorView<Payee, Long> editorview = new EditorView<Payee, Long>(payeeService);
		editorview.setProperties("fullId","uid","type","prefix","name","address1","address2","city","iban","postalAccount","bankDetails");
		return editorview;
	}
	
	@Bean
	@Scope("prototype")
	public ListView<Payee> payeeListView() {
		ListView<Payee> listview = new ListView<Payee>(Payee.class);
		listview.setColumnConfigs(
				   new TableColumnConfig("fullId").collapse().rightAlign(),
				   new TableColumnConfig("uid").collapse(),
				   new TableColumnConfig("createdTs").collapse(),
				   new TableColumnConfig("modifiedTs").collapse(),

				   new TableColumnConfig("type").centerAlign().sortBy(".name"),
				   new TableColumnConfig("prefix"),
				   new TableColumnConfig("name").asc(),
				   new TableColumnConfig("city").sortBy(".display"),
				   new TableColumnConfig("externalReference").collapse()
				   );
		listview.addFilterSource(new DisplayFilter());
		ActionHelper.setDefaultListViewActions(listview, "payeeEditorView");
		return listview;
	}
}
