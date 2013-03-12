package w.wexpense.vaadin7.action;

import java.io.Serializable;

import w.wexpense.vaadin7.WexUI;
import w.wexpense.vaadin7.view.EditorView;

import com.vaadin.ui.UI;
import com.vaadin.ui.Window;

public class EditAction<T, ID extends Serializable, E extends EditorView<T, ID >> extends ListViewAction {

	Class<E> editorType;
	
	public EditAction(Class<E> editorClass) {
		super("edit");
		this.editorType = editorClass;
	}

	@Override
	public void handleAction(Object sender, Object target) {
		if (target != null) {
			@SuppressWarnings("unchecked")
         ID id = (ID) target;

			EditorView<T, ID> editor = ((WexUI) UI.getCurrent()).getBean(editorType);

			editor.loadInstance(id);
			
			Window window = new Window();
			window.setContent(editor);
			editor.setWindow(window);
			UI.getCurrent().addWindow(window);
		}
	}

	@Override
	public boolean canHandle(Object target, Object sender) {
		return target != null;
	}

}
