package w.wexpense.vaadin7.view;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Nullable;
import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;

import w.wexpense.service.PersistenceService;
import w.wexpense.vaadin7.action.ListViewAction;

import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;
import com.vaadin.data.Container;
import com.vaadin.data.Container.Filter;
import com.vaadin.event.Action;
import com.vaadin.event.ItemClickEvent;
import com.vaadin.event.ItemClickEvent.ItemClickListener;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.ui.Table;
import com.vaadin.ui.VerticalLayout;

public class ListView<T> extends GenericView<T> implements View, Action.Handler, ItemClickListener {

	private static final long serialVersionUID = 5282517667310057582L;
	
	@Autowired
	protected PersistenceService persistenceService;
	
	protected Container container;
	
	protected VerticalLayout layout;
	
	protected Table table;
	
	protected String[] alignProperties;
	
	protected String[] sortProperties;
	
	protected Filter filter;	
	
	protected List<ListViewAction> actions;
	
	protected ListViewAction defaultAction;
	
	public ListView(Class<T> entityClass) {
		super(entityClass);	
	}
	
	@PostConstruct
	public void postConstruct() {		
		int i=0;
		List<String> nestedProperties = new ArrayList<>();
		Map<String, Table.Align> alignement = new HashMap<>();
		String[] visibleProperties = new String[alignProperties.length];

		// parse the properties alignment and clean get the stripped properties name 
		for(String s: alignProperties) {
			char c = s.charAt(0);		
			if (c == '=') {
				s=s.substring(1);
				alignement.put(s,Table.Align.CENTER);
			} else if (c == '<') {
				s=s.substring(1);
				alignement.put(s,Table.Align.LEFT);
			} else if (c == '>') {
				s=s.substring(1);
				alignement.put(s,Table.Align.RIGHT);
			}
			if (s.contains(".")) {
				nestedProperties.add(s);
			}
			visibleProperties[i++] = s;
		}
		
		// build the container
		container = persistenceService.getJPAContainer(getEntityClass(), nestedProperties);

		// sort/order the container 
		if (container instanceof Container.Sortable && sortProperties != null && sortProperties.length>0) {
			Object[] ids = new String[sortProperties.length];			
			boolean[] direction = new boolean[sortProperties.length];

			for(i=0;i<sortProperties.length;i++) {
				char c = sortProperties[i].charAt(0);				
				if (c == '+' || c == '-') {
					direction[i] = c != '-';
					ids[i] = sortProperties[i].substring(1);
				}
			}
		
			((Container.Sortable) container).sort(ids, direction);
		}

		// filter the container
		if (container instanceof Container.Filterable && filter != null) {
			Container.Filterable filterable = (Container.Filterable) container;
			filterable.addContainerFilter(filter);
		}				
		
		// build the ui
		setSizeFull();
		
		layout = new VerticalLayout();
		setContent(layout);
		
		table = new Table(null, container);
		
		// set the property to display and their alignment
		table.setVisibleColumns(visibleProperties);
		for(Map.Entry<String, Table.Align> entry: alignement.entrySet()) {
			table.setColumnAlignment(entry.getKey(), entry.getValue());
		}
		
		table.setSizeFull();
		table.setImmediate(true);	
		table.setSelectable(true);
		
		// listen to events
		if (actions != null && actions.size() > 0) {
			table.addActionHandler(this);
			table.addItemClickListener(this);
		}
		
		layout.addComponent(table);
	}
	
	@Override
   public void enter(ViewChangeEvent event) {
	   // TODO Auto-generated method stub
	   
   }

	@Override
   public Action[] getActions(final Object target, final Object sender) {
	  Collection<ListViewAction> c = Collections2.filter(actions, new Predicate<ListViewAction>() {
		  public boolean apply(@Nullable ListViewAction action) {
			  return action.canHandle(target, sender);
		  }
	  });
	  return c.toArray(new Action[c.size()]);
   }

	@Override
   public void handleAction(Action action, Object sender, Object target) {
	   ((Action.Listener) action).handleAction(sender, target);	   
   }
	
	public void addListViewAction(ListViewAction action) {
		if (actions == null) actions = new ArrayList<>();
		actions.add(action);
	}
	
	public void setListViewActions(List<ListViewAction> actions) {
		this.actions = actions;
	}

	public void setDefaultAction(ListViewAction defaultAction) {
		this.defaultAction = defaultAction;
	}

	@Override
	public void itemClick(ItemClickEvent event) {
		if (event.isDoubleClick()) {
			// keep the selection active because double clicks = 2 clicks, the first selects, the second unselects !!!
			table.select(event.getItemId());
			
			entitySelected(event.getItemId());
		}
	}
	
	public void entitySelected(Object target) {
		if (defaultAction != null && defaultAction.canHandle(target, this.table)){
			defaultAction.handleAction(table, target);
		}
	}
	
	public void setAlignProperties(String... alignProperties) {
		this.alignProperties = alignProperties;
	}

	public void setSortProperties(String... sortProperties) {
		this.sortProperties = sortProperties;
	}
	
	public void setFilter(Filter filter) {
		this.filter = filter;
	}

}
