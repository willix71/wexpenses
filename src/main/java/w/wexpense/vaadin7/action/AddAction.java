package w.wexpense.vaadin7.action;

import java.io.Serializable;

import w.wexpense.vaadin7.WexUI;
import w.wexpense.vaadin7.view.EditorView;

import com.vaadin.ui.UI;
import com.vaadin.ui.Window;

public class AddAction<T, ID extends Serializable, E extends EditorView<T, ID >> extends ListViewAction {

	private Class<E> editorType;
	
	public AddAction(Class<E> editorClass) {
		super("add");
		this.editorType = editorClass;
	}

	@Override
	public void handleAction(Object sender, Object target) {
		if (target != null) {
			EditorView<T, ID> editor = ((WexUI) UI.getCurrent()).getBean(editorType);
			
			editor.newInstance();
			
			Window window = new Window();
			window.setContent(editor);
			editor.setWindow(window);
			UI.getCurrent().addWindow(window);
		}
	}

	@Override
	public boolean canHandle(Object target, Object sender) {
		return true;
	}

}
