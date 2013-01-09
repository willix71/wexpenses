package w.wexpense.vaadin.view;

import com.vaadin.addon.jpacontainer.JPAContainer;
import com.vaadin.data.Container.Filter;
import com.vaadin.data.util.filter.Like;
import com.vaadin.event.FieldEvents.TextChangeEvent;
import com.vaadin.event.FieldEvents.TextChangeListener;
import com.vaadin.ui.TextField;

public class DisplayFilter extends TextField {

	private static final long serialVersionUID = -4349643141593583652L;

	private String textFilter;

	public DisplayFilter(final JPAContainer<?> jpaContainer) {
		super();
		setInputPrompt("Search for");
		addListener(new TextChangeListener() {			
			@Override
			public void textChange(TextChangeEvent event) {
				textFilter = event.getText();
				updateFilters(jpaContainer);
			}
		});
	}

	protected void updateFilters(JPAContainer<?> jpaContainer) {
		jpaContainer.setApplyFiltersImmediately(false);
		jpaContainer.removeAllContainerFilters();

		if (textFilter != null && !textFilter.equals("")) {
			Filter filter = new Like("display", "%" + textFilter + "%", false);
			jpaContainer.addContainerFilter(filter);
		}
		
		jpaContainer.applyFilters();
	}

}
