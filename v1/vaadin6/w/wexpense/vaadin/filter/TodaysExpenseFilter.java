package w.wexpense.vaadin.filter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import w.utils.DateUtils;

import com.vaadin.addon.jpacontainer.JPAContainer;
import com.vaadin.data.Container.Filter;
import com.vaadin.data.util.filter.Compare;
import com.vaadin.ui.HorizontalLayout;

public class TodaysExpenseFilter extends HorizontalLayout implements WexFilter {

	private static final long serialVersionUID = -4349643141593583652L;
	
	private final Logger LOGGER = LoggerFactory.getLogger(getClass());
	
	@Override
	public void setJPAContainer(JPAContainer<?> jpaContainer) {
		jpaContainer.setApplyFiltersImmediately(false);
		jpaContainer.removeAllContainerFilters();
		Filter today = new Compare.GreaterOrEqual("modifiedTs", DateUtils.getDate());
		jpaContainer.addContainerFilter(today);
		jpaContainer.applyFilters();		
	}	

}
