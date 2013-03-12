package w.wexpense.vaadin7.action;

import java.io.Serializable;

import w.wexpense.vaadin7.WexUI;
import w.wexpense.vaadin7.view.EditorView;

import com.vaadin.ui.UI;

public class DeleteAction<T, ID extends Serializable, E extends EditorView<T, ID >> extends ListViewAction {

	Class<E> editorType;
	
	public DeleteAction(Class<E> editorClass) {
		super("delete");
		this.editorType = editorClass;
	}

	@Override
	public void handleAction(Object sender, Object target) {
		if (target != null) {
			@SuppressWarnings("unchecked")
         ID id = (ID) target;

			EditorView<T, ID> editor = ((WexUI) UI.getCurrent()).getBean(editorType);

			editor.delete(id);			
		}
	}

	@Override
	public boolean canHandle(Object target, Object sender) {
		return target != null;
	}

}
