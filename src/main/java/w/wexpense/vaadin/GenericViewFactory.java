package w.wexpense.vaadin;

import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import com.vaadin.addon.jpacontainer.EntityItem;
import com.vaadin.addon.jpacontainer.JPAContainer;
import com.vaadin.data.Container.Filter;
import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.util.filter.Like;
import com.vaadin.event.FieldEvents.TextChangeEvent;
import com.vaadin.event.FieldEvents.TextChangeListener;
import com.vaadin.event.ItemClickEvent;
import com.vaadin.event.ItemClickEvent.ItemClickListener;
import com.vaadin.ui.AbstractLayout;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Component;
import com.vaadin.ui.ComponentContainer;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Table;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;

public class GenericViewFactory<T> extends AbstractViewFactory<T> {
	
	private GenericEditorFactory<T> editorFactory;

	private List<GenericViewFactory<?>> subViews;

	public GenericViewFactory(Class<T> entityClass) {
		super(entityClass);
	}
	
	public GenericViewFactory(Class<T> entityClass, GenericEditorFactory<T> editorFactory) {
		super(entityClass);
		this.editorFactory = editorFactory;
	}
	
	public List<GenericViewFactory<?>> getSubViews() {
		return subViews;
	}
	public void setSubViews(List<GenericViewFactory<?>> subViews) {
		this.subViews = subViews;
	}
	
	public Component newInstance() {
		return new GenericView();
	}

	class GenericView extends VerticalLayout implements Button.ClickListener, ComponentContainer {
		private static final long serialVersionUID = 5282517667310057582L;
		
		private JPAContainer<T> jpaContainer;

		private AbstractLayout toolbar;
		private Button newButton;
		private Button deleteButton;
		private Button editButton;
		private Button refreshButton;
		private Table table;

		private TextField searchField;
		private String textFilter;

		public GenericView() {
			jpaContainer = buildJPAContainer();
			
			buildTable();
			buildToolbar();

			addComponent(toolbar);
			addComponent(table);
			setExpandRatio(table, 1);
			setSizeFull();
		}

		private void buildTable() {
			table = new Table(null, jpaContainer) {
			    protected String formatPropertyValue(Object rowId, Object colId, Property property) {
			        if (property == null || property.getValue() == null) {
			            return "";
			        }
			        if (Date.class.isAssignableFrom(property.getType())) {
			      	  return new SimpleDateFormat("dd/MM/yyyy").format(property.getValue());			      	  
			        }
			        if (Number.class.isAssignableFrom(property.getType())) {
			      	  return MessageFormat.format("{0,number,0.00}", property.getValue());
			        }
			        return property.toString();
			    }
			};
			table.setSelectable(true);
			table.setImmediate(true);
			table.setSizeFull();
			
			table.addListener(new Property.ValueChangeListener() {
				@Override
				public void valueChange(ValueChangeEvent event) {
					boolean modificationEnabled = event.getProperty().getValue() != null;
					
					deleteButton.setEnabled(modificationEnabled);
					editButton.setEnabled(modificationEnabled);
				}
			});
			table.addListener(new ItemClickListener() {
				@Override
				public void itemClick(ItemClickEvent event) {
					if (event.isDoubleClick()) {
						table.select(event.getItemId());

						editEntity();
					}
				}
			});

			String[] propertyIds=getProperties("viewer","visibleProperties");
			table.setVisibleColumns(propertyIds);
			for(String pid: propertyIds) {
				String p = getProperty("viewer", pid, "alignement");
				if (p!=null) table.setColumnAlignment(pid, p);
				p = getProperty("viewer", pid, "expandRatio");
				if (p!=null) table.setColumnExpandRatio(pid, Float.valueOf(p));
			}
			
		}

		private void buildToolbar() {
			HorizontalLayout toolbar = new HorizontalLayout();

			if (editorFactory != null) {
				newButton = new Button("Add");
				newButton.addListener(this);
	
				deleteButton = new Button("Delete");
				deleteButton.addListener(this);
				deleteButton.setEnabled(false);
	
				editButton = new Button("Edit");
				editButton.addListener(this);
				editButton.setEnabled(false);
				
				refreshButton = new Button("Refresh");
				refreshButton.addListener(this);
				
				toolbar.addComponent(newButton);
				toolbar.addComponent(deleteButton);
				toolbar.addComponent(editButton);
				toolbar.addComponent(refreshButton);
			}

			searchField = new TextField();
			searchField.setInputPrompt("Search for");
			searchField.addListener(new TextChangeListener() {
				
				@Override
				public void textChange(TextChangeEvent event) {
					textFilter = event.getText();
					updateFilters();
				}
			});

			toolbar.addComponent(searchField);
			toolbar.setWidth("100%");
			toolbar.setExpandRatio(searchField, 1);
			toolbar.setComponentAlignment(searchField, Alignment.TOP_RIGHT);

			this.toolbar = toolbar;
		}

		@Override
		public String getCaption() {
			return getEntityClass().getSimpleName();
		}

		public void buttonClick(ClickEvent event) {
			if (event.getButton() == newButton) {
				addEntity();
			} else if (event.getButton() == deleteButton) {
				deleteEntity();
			} else if (event.getButton() == editButton) {
				editEntity();
			} else if (event.getButton() == refreshButton) {
				refreshContainer();
			}
		}

		protected void addEntity() {
			Component c = editorFactory.newInstance(null);
			newWindow(c);
		}

		protected void editEntity() {
			EntityItem<T> item = (EntityItem<T>) table.getItem(table.getValue());
			Component c = editorFactory.newInstance(item.getEntity());
			newWindow(c);			
		}
		
		protected void deleteEntity() {
			jpaContainer.removeItem(table.getValue());
		}

		protected void newWindow(Component component) {
			getApplication().getMainWindow().addWindow(new ClosableWindow(component));
		}
		
		protected void updateFilters() {
			jpaContainer.setApplyFiltersImmediately(false);
			jpaContainer.removeAllContainerFilters();

			if (textFilter != null && !textFilter.equals("")) {
				Filter filter = new Like("display", "%" + textFilter + "%", false);
				jpaContainer.addContainerFilter(filter);
			}
			jpaContainer.applyFilters();
		}

		/**
		 * Method to refresh containers in this view. E.g. if a bidirectional
		 * reference is edited from the opposite direction or if we knew that an
		 * other user had edited visible values.
		 */
		public void refreshContainer() {
			jpaContainer.refresh();
		}

		public JPAContainer<T> getJpaContainer() {
			return jpaContainer;
		}
	}
}