package w.wexpense.vaadin7.view;

import w.wexpense.model.Parentable;

import com.vaadin.addon.jpacontainer.HierarchicalEntityContainer;
import com.vaadin.data.Container;
import com.vaadin.ui.Table;
import com.vaadin.ui.TreeTable;

public class ParentableListView<T extends Parentable<T>> extends ListView<T> {

	public ParentableListView(Class<T> entityClass) {
		super(entityClass);
	}

	@Override
	protected Table createTable(Container container) {
		((HierarchicalEntityContainer<?>) container).setParentProperty("parent");
		return new TreeTable(null, container);
	}
}
