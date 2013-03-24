package w.wexpense.vaadin7;

import w.wexpense.vaadin7.view.EditorView;
import w.wexpense.vaadin7.view.GenericView;

import com.vaadin.ui.UI;
import com.vaadin.ui.Window;

public class UIHelper {
	
	public static <T> EditorView<T,?> getEditorView(Class<T> clazz) {
		String className = clazz.getSimpleName();
		String name = className.substring(0, 1).toLowerCase() + className.substring(1) + "EditorView";
		@SuppressWarnings("unchecked")
      EditorView<T,?> editor = ((WexUI) UI.getCurrent()).getBean(EditorView.class, name);
		return editor;
	}
	
	public static Window displayWindow(GenericView<?> editor) {
		Window window = new Window();
		window.setContent(editor);
		editor.setWindow(window);
		window.center();
		UI.getCurrent().addWindow(window);
		return window;
	}
	
	public static Window displayModalWindow(GenericView<?> editor) {
		Window window = new Window();
		window.setModal(true);
		window.setContent(editor);
		window.center();
		editor.setWindow(window);
		UI.getCurrent().addWindow(window);
		return window;
	}
}
