package w.wexpense.vaadin7.action;

import java.io.Serializable;

import w.wexpense.vaadin7.view.EditorView;
import w.wexpense.vaadin7.view.ListView;
import w.wexpense.vaadin7.view.model.PayeeEditorView;

public class ActionHelper {

	public static <T, ID extends Serializable, E extends EditorView<T, ID >> void setDefaultListViewActions(ListView<T> view, Class<E> editorClass) {
	   view.addListViewAction(new AddAction(PayeeEditorView.class));
	   view.addListViewAction(new EditAction(PayeeEditorView.class));
	   view.addListViewAction(new DeleteAction(PayeeEditorView.class));
	   view.addListViewAction(new RefreshAction());
	   
	   view.setDefaultAction(new EditAction(PayeeEditorView.class));
	}
}
