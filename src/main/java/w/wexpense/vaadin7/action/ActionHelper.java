package w.wexpense.vaadin7.action;

import java.io.Serializable;

import w.wexpense.vaadin7.view.EditorView;
import w.wexpense.vaadin7.view.ListView;

public class ActionHelper {

	public static <T, ID extends Serializable, E extends EditorView<T, ID>> 
	void setDefaultListViewActions(ListView<T> view, Class<E> editorClass) {
		setDefaultListViewActions(view, editorClass, null);
	}
	
	public static <T, ID extends Serializable, E extends EditorView<T, ID>> 
	void setDefaultListViewActions(ListView<T> view, Class<E> editorClass, String editorName) {
		view.addListViewAction(new AddAction(editorClass, editorName), true);
		view.addListViewAction(new EditAction(editorClass, editorName));
		view.addListViewAction(new DeleteAction(editorClass, editorName));
		view.addListViewAction(new RefreshAction());
	}

}
