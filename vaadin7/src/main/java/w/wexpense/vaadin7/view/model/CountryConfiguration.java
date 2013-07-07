package w.wexpense.vaadin7.view.model;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

import w.wexpense.model.Country;
import w.wexpense.service.StorableService;
import w.wexpense.vaadin7.action.ActionHelper;
import w.wexpense.vaadin7.support.TableColumnConfig;
import w.wexpense.vaadin7.view.EditorView;
import w.wexpense.vaadin7.view.ListView;

@Configuration
public class CountryConfiguration {

	@Autowired
	@Qualifier("countryService")
	private StorableService<Country, String> countryService;
	
	@Bean
	@Scope("prototype")
	public EditorView<Country, String> countryEditorView() {
		EditorView<Country, String> editorview = new EditorView<Country, String>(countryService);
		editorview.setProperties("code","name","currency");
		return editorview;
	}
	
	@Bean
	@Scope("prototype")
	public ListView<Country> countryListView() {
		ListView<Country> listview = new ListView<Country>(Country.class);
		listview.setColumnConfigs(
				   new TableColumnConfig("code").asc(),
				   new TableColumnConfig("name").asc(),
				   new TableColumnConfig("currency.code")
				   );
		   
		ActionHelper.setDefaultListViewActions(listview, "countryEditorView");
		return listview;
	}
}
