package w.wexpense.vaadin7.view.model;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

import w.wexpense.model.PayeeType;
import w.wexpense.persistence.dao.IPayeeTypeJpaDao;
import w.wexpense.service.EntityService;
import w.wexpense.vaadin7.action.ActionHelper;
import w.wexpense.vaadin7.view.EditorView;
import w.wexpense.vaadin7.view.ListView;
import w.wexpense.vaadin7.view.TableColumnConfig;

@Configuration
public class PayeeTypeConfiguration {

	@Autowired
	private IPayeeTypeJpaDao payeeTypeDao;
		
	@Bean 
	public EntityService<PayeeType, Long> payeeTypeService() {
		return new EntityService<>(PayeeType.class, payeeTypeDao);
	}
	
	@Bean
	@Scope("prototype")
	public EditorView<PayeeType, Long> payeeTypeEditorView() {
		EditorView<PayeeType, Long> editorview = new EditorView<PayeeType, Long>(payeeTypeService());
		editorview.setProperties("fullId","uid","name","selectable");
		return editorview;
	}
	
	@Bean
	@Scope("prototype")
	public ListView<PayeeType> payeeTypeListView() {
		ListView<PayeeType> listview = new ListView<PayeeType>(PayeeType.class);
		listview.setColumnConfigs(
				   new TableColumnConfig("id").collapse().rightAlign(),
				   new TableColumnConfig("uid").collapse(),
				   new TableColumnConfig("createdTs").collapse(),
				   new TableColumnConfig("modifiedTs").collapse(),

				   new TableColumnConfig("name").asc(),
				   new TableColumnConfig("selectable").centerAlign()
				   );
		   
		ActionHelper.setDefaultListViewActions(listview, EditorView.class, "payeeTypeEditorView");
		return listview;
	}
}
