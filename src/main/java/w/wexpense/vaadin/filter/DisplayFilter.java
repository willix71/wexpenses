package w.wexpense.vaadin.filter;

import com.vaadin.addon.jpacontainer.JPAContainer;
import com.vaadin.data.Container.Filter;
import com.vaadin.data.util.filter.Like;
import com.vaadin.event.FieldEvents.TextChangeEvent;
import com.vaadin.event.FieldEvents.TextChangeListener;
import com.vaadin.ui.TextField;

public class DisplayFilter extends TextField implements WexFilter {

	private static final long serialVersionUID = -4349643141593583652L;
	
	private JPAContainer<?> jpaContainer;
	
	public DisplayFilter() {
		super();
		setInputPrompt("Search for");
		addListener(new TextChangeListener() {			
			@Override
			public void textChange(TextChangeEvent event) {
				String textFilter = event.getText();
				updateFilters(textFilter);
			}
		});
	}

	@Override
	public void setJPAContainer(JPAContainer<?> jpaContainer) {
		this.jpaContainer = jpaContainer;
	}
	
	protected void updateFilters(String textFilter) {
		jpaContainer.setApplyFiltersImmediately(false);
		jpaContainer.removeAllContainerFilters();

		if (textFilter != null && !textFilter.equals("")) {
			Filter filter = new Like("display", "%" + textFilter + "%", false);
			jpaContainer.addContainerFilter(filter);
		}
		
		jpaContainer.applyFilters();
	}

}
