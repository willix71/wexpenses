package w.wexpense.vaadin;

import w.wexpense.model.Expense;

import com.vaadin.ui.Component;

public class ExpenseEditorFactory extends GenericEditorFactory<Expense> {

	public ExpenseEditorFactory() {
		super(Expense.class);
	}

	public Component newInstance(Expense expense) {
		return new ExpenseEditor(expense);
	}

	class ExpenseEditor extends GenericEditorFactory<Expense>.GenericEditor {
		
		public ExpenseEditor(Expense expense) {
			super(expense);
		}

		@Override
      public Expense save() {
			Expense x = super.save();
			return x;
      }
	}
}
