package w.wexpense.vaadin7.view.model;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

import w.wexpense.model.ExpenseType;
import w.wexpense.persistence.dao.IExpenseTypeJpaDao;
import w.wexpense.service.EntityService;
import w.wexpense.vaadin7.action.ActionHelper;
import w.wexpense.vaadin7.view.EditorView;
import w.wexpense.vaadin7.view.ListView;
import w.wexpense.vaadin7.view.TableColumnConfig;

@Configuration
public class ExpenseTypeConfiguration {

	@Autowired
	private IExpenseTypeJpaDao expenseTypeDao;
		
	@Bean 
	public EntityService<ExpenseType, Long> expenseTypeService() {
		return new EntityService<>(ExpenseType.class, expenseTypeDao);
	}
	
	@Bean
	@Scope("prototype")
	public EditorView<ExpenseType, Long> expenseTypeEditorView() {
		EditorView<ExpenseType, Long> editorview = new EditorView<ExpenseType, Long>(expenseTypeService());
		editorview.setProperties("fullId","uid","name","selectable","paymentGeneratorClassName");
		return editorview;
	}
	
	@Bean
	@Scope("prototype")
	public ListView<ExpenseType> expenseTypeListView() {
		ListView<ExpenseType> listview = new ListView<ExpenseType>(ExpenseType.class);
		listview.setColumnConfigs(
				   new TableColumnConfig("id").collapse().rightAlign(),
				   new TableColumnConfig("uid").collapse(),
				   new TableColumnConfig("createdTs").collapse(),
				   new TableColumnConfig("modifiedTs").collapse(),

				   new TableColumnConfig("name").asc(),
				   new TableColumnConfig("selectable").centerAlign(),
				   new TableColumnConfig("paymentGeneratorClassName", "Generator")
				   );
		   
		ActionHelper.setDefaultListViewActions(listview, EditorView.class, "expenseTypeEditorView");
		return listview;
	}
}
