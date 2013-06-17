package w.wexpense.vaadin7.view;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;

import w.wexpense.model.Parentable;
import w.wexpense.service.ContainerService;
import w.wexpense.vaadin7.action.ActionHandler;
import w.wexpense.vaadin7.support.TableColumnConfig;

import com.vaadin.addon.jpacontainer.HierarchicalEntityContainer;
import com.vaadin.data.Container;
import com.vaadin.data.Container.Filter;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.ui.Table;
import com.vaadin.ui.TreeTable;
import com.vaadin.ui.VerticalLayout;

public class ListView<T> extends GenericView<T> implements View {

    private static final long serialVersionUID = 5282517667310057582L;
   
    @Autowired
    protected ContainerService persistenceService;
   
    protected Container container;
   
    protected Filter filter;   
       
    protected Table table;
   
    protected TableColumnConfig[] columnConfigs;
   
    protected VerticalLayout layout;
   
    protected ActionHandler actionHandler;
   
    public ListView(Class<T> entityClass) {
        super(entityClass);   
    }
   
    @PostConstruct
    public void postConstruct() {           
        // build the container
        container = persistenceService.getContainer(getEntityClass(), columnConfigs);
       
        // filter the container
        if (container instanceof Container.Filterable && filter != null) {
            Container.Filterable filterable = (Container.Filterable) container;
            filterable.addContainerFilter(filter);
        }               
       
        // build the GUI     
        layout = new VerticalLayout();
        layout.setSizeFull();
        setContent(layout);
       
        table = createTable(container);   
        table.setSizeFull();
        table.setImmediate(true);   
        table.setSelectable(true);
        table.setMultiSelect(false);
        table.setColumnCollapsingAllowed(true);
        TableColumnConfig.configure(table, columnConfigs);
       
        // listen to events
        if (actionHandler != null) {
            actionHandler.setTable(table);
        }
       
        layout.addComponent(table);
        layout.setExpandRatio(table,1);
    }
   
    protected Table createTable(Container container) {
        if (Parentable.class.isAssignableFrom(getEntityClass()) && container instanceof HierarchicalEntityContainer) {
            ((HierarchicalEntityContainer<?>) container).setParentProperty("parent");
            return new TreeTable(null, container);
        } else {
            return new Table(null, container);
        }
    }
   
	@Override
	public void enter(ViewChangeEvent event) {
		setSizeFull();
	}

	public void setFilter(Filter filter) {
		this.filter = filter;

		if (container instanceof Container.Filterable && container != null) {
			Container.Filterable filterable = (Container.Filterable) container;
			filterable.removeAllContainerFilters();
			if (filter != null) {
				filterable.addContainerFilter(filter);
			}
		}
	}

	public void setActionHandler(ActionHandler actionHandler) {
		this.actionHandler = actionHandler;
	}

	public void setColumnConfigs(TableColumnConfig... columnConfigs) {
		this.columnConfigs = columnConfigs;
	}
}