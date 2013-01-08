package w.wexpense.vaadin;

import w.wexpense.model.Expense;
import w.wexpense.model.TransactionLine;
import w.wexpense.vaadin.fieldfactory.OneToManyFieldFactory;
import w.wexpense.vaadin.fieldfactory.SimpleFieldFactory;

import com.vaadin.addon.jpacontainer.EntityItem;
import com.vaadin.data.Item;
import com.vaadin.ui.Component;

public class ExpenseEditorFactory extends GenericEditorFactory<Expense> {

	public ExpenseEditorFactory() {
		super(Expense.class);
	}

	public Component newInstance(Item item) {
		return new ExpenseEditor(item);
	}

	class ExpenseEditor extends GenericEditorFactory<Expense>.GenericEditor {

		OneToManyFieldFactory transactionsEditor;
		
		public ExpenseEditor(Item entityItem) {
			super(entityItem);
		}

		@Override
		protected void setFormFieldFactory() {
			transactionsEditor = 
					new OneToManyFieldFactory(getEntityManager(), TransactionLine.class, "expense");
			transactionsEditor.setVisibleColumns(new Object[] {"factor", "amount", "account", "description"});

			SimpleFieldFactory<Expense> factory = new SimpleFieldFactory<Expense>(getJpaContainer());
			factory.addCustomFieldFactory("transactions", transactionsEditor);
			
			setFormFieldFactory(factory);
		}
		
		@Override
      public EntityItem<Expense> save() {
			EntityItem<Expense> xItem = super.save();
			
			((TwoStepCommit) getField("transactions")).postCommit(xItem);
			return xItem;
      }
	}
}
