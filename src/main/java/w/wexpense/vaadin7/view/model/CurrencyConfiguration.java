package w.wexpense.vaadin7.view.model;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

import w.wexpense.model.Currency;
import w.wexpense.persistence.dao.ICurrencyJpaDao;
import w.wexpense.service.EntityService;
import w.wexpense.vaadin7.action.ActionHelper;
import w.wexpense.vaadin7.view.EditorView;
import w.wexpense.vaadin7.view.ListView;
import w.wexpense.vaadin7.view.TableColumnConfig;

@Configuration
public class CurrencyConfiguration {

	@Autowired
	private ICurrencyJpaDao currencyDao;

	@Bean 
	public EntityService<Currency, String> currencyService() {
		return new EntityService<>(Currency.class, currencyDao);
	}
	
	@Bean
	@Scope("prototype")
	public EditorView<Currency, String> currencyEditorView() {
		EditorView<Currency, String> editorview = new EditorView<Currency, String>(currencyService());
		editorview.setProperties("code","name");
		return editorview;
	}
	
	@Bean
	@Scope("prototype")
	public ListView<Currency> currencyListView() {
		ListView<Currency> listview = new ListView<Currency>(Currency.class);
		listview.setColumnConfigs(
				   new TableColumnConfig("code").asc(),
				   new TableColumnConfig("name").asc()
				   );
		   
		ActionHelper.setDefaultListViewActions(listview, EditorView.class, "currencyEditorView");
		return listview;
	}
}
