package w.wexpense.vaadin.view;


import com.vaadin.event.Action;
import com.vaadin.event.ItemClickEvent;
import com.vaadin.event.ItemClickEvent.ItemClickListener;
import com.vaadin.ui.Table;
import com.vaadin.ui.Window.Notification;

public abstract class AbstractView<T> extends ConfigurableView<T> implements Action.Handler, ItemClickListener {

	private static final long serialVersionUID = 6499289439725418193L;
	
	protected Table table;

	protected final Action addAction = new Action("Add");
	protected final Action editAction = new Action("Edit");
	protected final Action removeAction = new Action("Remove");
	protected final Action deleteAction = new Action("Delete");
	protected final Action refreshAction = new Action("Refresh");

	protected Action[] entitySelectedActions = null;
	protected Action[] noEntitySelectedActions = null;

	public AbstractView(Class<T> entityClass) {
		super(entityClass);
	}
	
	@Override
	public Action[] getActions(Object target, Object sender) {
		return target == null ? noEntitySelectedActions : entitySelectedActions;
	}

	@Override
	public void handleAction(Action action, Object sender, Object target) {
		if (addAction == action) {
			addEntity();
		} else if (editAction == action) {
			editEntity(target);
		} else if (removeAction == action) {
			removeEntity(target);
		} else if (deleteAction == action) {
			deleteEntity(target);
		} else if (refreshAction == action) {
			refreshContainer();
		}
	}

	public void addEntity() {
	}

	public void editEntity(Object target) {
	}

	public void removeEntity(Object target) {
	}

	public void deleteEntity(Object target) {
	}
	
	public void refreshContainer() {
		getWindow().showNotification(
				entityClass.getSimpleName(), "refreshed...",
                Notification.TYPE_HUMANIZED_MESSAGE);	
	}

	@Override
	public void itemClick(ItemClickEvent event) {
		if (event.isDoubleClick()) {
			// keep the selection active (double = 2 clicks, one selects, one deselects)
			table.select(event.getItemId());

			entitySelected(event);
		}
	}
	
	public void entitySelected(ItemClickEvent event) {		
	}
}
