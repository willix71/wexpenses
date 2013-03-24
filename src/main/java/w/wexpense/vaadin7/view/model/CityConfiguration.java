package w.wexpense.vaadin7.view.model;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

import w.wexpense.model.City;
import w.wexpense.persistence.dao.ICityJpaDao;
import w.wexpense.service.EntityService;
import w.wexpense.vaadin7.action.ActionHelper;
import w.wexpense.vaadin7.view.EditorView;
import w.wexpense.vaadin7.view.ListView;
import w.wexpense.vaadin7.view.TableColumnConfig;

@Configuration
public class CityConfiguration {

	@Autowired
	private ICityJpaDao cityDao;
		
	@Bean 
	public EntityService<City, Long> cityService() {
		return new EntityService<>(City.class, cityDao);
	}
	
	@Bean
	@Scope("prototype")
	public EditorView<City, Long> cityEditorView() {
		EditorView<City, Long> editorview = new EditorView<City, Long>(cityService());
		editorview.setProperties("fullId","uid","zip","name","country");
		return editorview;
	}
	
	@Bean
	@Scope("prototype")
	public ListView<City> cityListView() {
		ListView<City> listview = new ListView<City>(City.class);
		listview.setColumnConfigs(
				   new TableColumnConfig("id").collapse().rightAlign(),
				   new TableColumnConfig("uid").collapse(),
				   new TableColumnConfig("createdTs").collapse(),
				   new TableColumnConfig("modifiedTs").collapse(),

				   new TableColumnConfig("zip").rightAlign(),
				   new TableColumnConfig("name").asc(),
				   new TableColumnConfig("country.code")
				   );
		   
		ActionHelper.setDefaultListViewActions(listview, EditorView.class, "cityEditorView");
		return listview;
	}
}
