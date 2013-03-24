package w.wexpense.vaadin7.action;

import java.io.Serializable;

import w.wexpense.vaadin7.UIHelper;
import w.wexpense.vaadin7.WexUI;
import w.wexpense.vaadin7.event.EntityChangeEvent;
import w.wexpense.vaadin7.view.EditorView;

import com.vaadin.addon.jpacontainer.JPAContainer;
import com.vaadin.ui.Component;
import com.vaadin.ui.Component.Event;
import com.vaadin.ui.Table;
import com.vaadin.ui.UI;

public class AddAction<T, ID extends Serializable, E extends EditorView<T, ID >> extends ListViewAction {
	
	private static final long serialVersionUID = 1L;

	private Class<E> editorType;
	private String editorName;
	
	public AddAction(Class<E> editorType) {
		super("add");
		this.editorType = editorType;
	}

	public AddAction(Class<E> editorType, String editorName) {
		this(editorType);
		this.editorName = editorName;
	}
	
	@Override
	public void handleAction(final Object sender, final Object target) {
		final EditorView<T, ID> editor = ((WexUI) UI.getCurrent()).getBean(editorType, editorName);	
		editor.newInstance();
		editor.addListener(new Component.Listener() {
			private static final long serialVersionUID = 8121179082149508635L;

			@Override
			public void componentEvent(Event event) {
				if (event instanceof EntityChangeEvent && event.getComponent() == editor && sender instanceof Table) {
					com.vaadin.data.Container c = ((Table) sender) .getContainerDataSource();
					if (c instanceof JPAContainer) {
						((JPAContainer<?>) c).refresh();
					}
				}
			}
		});
		
		UIHelper.displayWindow(editor); 
	}

	@Override
	public boolean canHandle(Object target, Object sender) {
		return true;
	}
}
