package w.wexpense.vaadin7.action;

import w.wexpense.vaadin7.UIHelper;
import w.wexpense.vaadin7.WexUI;
import w.wexpense.vaadin7.container.OneToManyContainer;
import w.wexpense.vaadin7.event.MutliSelectionChangeEvent;
import w.wexpense.vaadin7.view.MultiSelectorView;

import com.vaadin.data.Container.Filter;
import com.vaadin.server.Sizeable.Unit;
import com.vaadin.ui.Component;
import com.vaadin.ui.Component.Event;
import com.vaadin.ui.Table;
import com.vaadin.ui.UI;
import com.vaadin.ui.Window;

public class AddMultiSelectionAction<T> extends ListViewAction {

	private static final long serialVersionUID = 1L;

	private String selectorName;
	private Filter filter;

	public AddMultiSelectionAction(String selectorName) {
		super("add");
		this.selectorName = selectorName;
	}

	public AddMultiSelectionAction(String selectorName, Filter filter) {
		super(selectorName);
		this.filter = filter;
	}

	@Override
	public void handleAction(final Object sender, final Object target) {
		final OneToManyContainer<T> container = (OneToManyContainer<T>) ((Table) sender).getContainerDataSource();

		final MultiSelectorView<T> selector = ((WexUI) UI.getCurrent()).getBean(MultiSelectorView.class, selectorName);
		selector.setFilter(getFilter());

		if (!container.isEmpty()) {
			selector.setValues(container.getBeans());
		}

		selector.addListener(new Component.Listener() {
			private static final long serialVersionUID = 8121179082149508635L;

			@Override
			public void componentEvent(Event event) {
				if (event instanceof MutliSelectionChangeEvent && event.getComponent() == selector && sender instanceof Table) {
					container.resetBeans(((MutliSelectionChangeEvent) event).getBeans());
				}
			}
		});

		Window w = UIHelper.displayModalWindow(selector);
		//w.setHeight(200,Unit.PIXELS);
		//w.setWidth(300, Unit.PIXELS);

	}

	@Override
	public boolean canHandle(Object target, Object sender) {
		return true;
	}

	public Filter getFilter() {
		return this.filter;
	}

//	public static <T> Filter getFilter(BeanItemContainer<T> container) {
//		String propertyId = PersistenceUtils.getIdName(container.getBeanType());
//		List<Filter> filters = new ArrayList<Filter>();
//		for (T t : container.getItemIds()) {
//			filters.add(new Compare.Equal(propertyId, container.getItem(t).getItemProperty(propertyId).getValue()));
//		}
//
//		return new Or(filters.toArray(new Filter[filters.size()]));
//	}
}
