package w.wexpense.vaadin7.view.model;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

import w.wexpense.model.ExchangeRate;
import w.wexpense.service.StorableService;
import w.wexpense.vaadin7.action.ActionHelper;
import w.wexpense.vaadin7.filter.ExchangeRateFilter;
import w.wexpense.vaadin7.support.TableColumnConfig;
import w.wexpense.vaadin7.view.EditorView;
import w.wexpense.vaadin7.view.ListView;
import w.wexpense.vaadin7.view.SelectorView;

@Configuration
public class ExchangeRateConfiguration {

	@Autowired
	@Qualifier("exchangeRateService") 
	private StorableService<ExchangeRate, Long> exchangeRateService;
	
	@Bean
	@Scope("prototype")
	public EditorView<ExchangeRate, Long> exchangeRateEditorView() {
		EditorView<ExchangeRate, Long> editorview = new EditorView<ExchangeRate, Long>(exchangeRateService);
		editorview.setProperties("fullId","uid","date","institution","fromCurrency","toCurrency","rate","fee");
		return editorview;
	}
	
	@Bean
	@Scope("prototype")
	public ListView<ExchangeRate> exchangeRateListView() {
		ListView<ExchangeRate> listview = new ListView<ExchangeRate>(ExchangeRate.class);
		listview.setColumnConfigs(getTableColumnConfig());
		listview.addFilterSource(getExchangeRateFilter());
		ActionHelper.setDefaultListViewActions(listview, "exchangeRateEditorView");
		return listview;
	}
	
	@Bean
	@Scope("prototype")
	public SelectorView<ExchangeRate> exchangeRateSelectorView() {
		SelectorView<ExchangeRate> selectorview = new SelectorView<ExchangeRate>(ExchangeRate.class);
		selectorview.setColumnConfigs(getTableColumnConfig());
		ActionHelper.setExchangeRateSelectorViewActions(selectorview, "exchangeRateEditorView");
		return selectorview;
	}
	
	@Bean
	@Scope("prototype")
	public ExchangeRateFilter getExchangeRateFilter() {
		return new ExchangeRateFilter();
	}
	
	private TableColumnConfig[] getTableColumnConfig() {
		return new TableColumnConfig[]{
			   new TableColumnConfig("fullId").collapse().rightAlign(),
			   new TableColumnConfig("uid").collapse(),
			   new TableColumnConfig("createdTs").collapse(),
			   new TableColumnConfig("modifiedTs").collapse(),

			   new TableColumnConfig("date").desc(),
			   new TableColumnConfig("institution").sortBy(".display").expand(1.0f), 
			   new TableColumnConfig("fromCurrency").centerAlign(),
			   new TableColumnConfig("toCurrency").centerAlign(),
			   new TableColumnConfig("rate"),
			   new TableColumnConfig("fee")
		};
	}
}
