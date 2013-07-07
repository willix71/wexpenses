package w.wexpense.vaadin7.action;

import java.io.Serializable;

import w.wexpense.model.Duplicatable;
import w.wexpense.vaadin7.view.EditorView;
import w.wexpense.vaadin7.view.ListView;
import w.wexpense.vaadin7.view.SelectorView;

public class ActionHelper {

	public static <T, ID extends Serializable, E extends EditorView<T, ID>> 
	void setDefaultListViewActions(ListView<T> view, String editorName, ListViewAction ...actions) {
		ActionHandler handler = new ActionHandler();
		handler.addListViewAction(new AddNewAction(editorName));
		handler.addListViewAction(new EditAction(editorName), true);
		handler.addListViewAction(new DeleteAction(editorName));
		if (Duplicatable.class.isAssignableFrom(view.getEntityClass())) {
			handler.addListViewAction(new DuplicateAction(editorName));
		}
		handler.addListViewAction(new RefreshAction());
		
		for(ListViewAction a: actions) {
			handler.addListViewAction(a);
		}
		view.setActionHandler(handler);
	}

	public static <T, ID extends Serializable, E extends EditorView<T, ID>> 
	void setDefaultSelectorViewActions(SelectorView<T> view, String editorName, ListViewAction ...actions) {
		ActionHandler handler = new ActionHandler();
		handler.addListViewAction(new SelectHandler(view), true);
		handler.addListViewAction(new AddNewAction(editorName));
		handler.addListViewAction(new EditAction(editorName));
		handler.addListViewAction(new DeleteAction(editorName));
		handler.addListViewAction(new RefreshAction());
		
		for(ListViewAction a: actions) {
			handler.addListViewAction(a);
		}
		view.setActionHandler(handler);
	}

	public static <T, ID extends Serializable, E extends EditorView<T, ID>> 
	void setExchangeRateSelectorViewActions(SelectorView<T> view, String editorName) {
		ActionHandler handler = new ActionHandler();
		handler.addListViewAction(new SelectHandler(view), true);
		handler.addListViewAction(new AddNewAction(editorName));
		handler.addListViewAction(new DeleteAction(editorName));
		handler.addListViewAction(new RefreshAction());
		view.setActionHandler(handler);
	}
}
