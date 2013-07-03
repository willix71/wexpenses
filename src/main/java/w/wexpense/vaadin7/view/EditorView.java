package w.wexpense.vaadin7.view;

import java.io.Serializable;
import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.vaadin.dialogs.ConfirmDialog;

import w.wexpense.model.Codable;
import w.wexpense.model.DBable;
import w.wexpense.model.Duplicatable;
import w.wexpense.persistence.PersistenceUtils;
import w.wexpense.service.ContainerService;
import w.wexpense.service.StorableService;
import w.wexpense.vaadin7.component.RelationalFieldFactory;
import w.wexpense.vaadin7.event.EntityChangeEvent;
import w.wexpense.vaadin7.menu.EnabalebalMenuBar;

import com.vaadin.data.fieldgroup.BeanFieldGroup;
import com.vaadin.data.fieldgroup.FieldGroup;
import com.vaadin.data.util.BeanItem;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.DefaultFieldFactory;
import com.vaadin.ui.Field;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.MenuBar.Command;
import com.vaadin.ui.MenuBar.MenuItem;
import com.vaadin.ui.Notification;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;

public class EditorView<T, ID extends Serializable> extends GenericView<T> {

	private static final long serialVersionUID = 5282517667310057582L;

	public final EnabalebalMenuBar.Enabalebal<T> NEW_DISABLER = new EnabalebalMenuBar.Enabalebal<T> () {
		@Override
      public boolean isEnabled(T t) {
	      return PersistenceUtils.getIdValue(t) != null;
      }	
	};

	@Autowired
	protected ContainerService persistenceService;

	protected StorableService<T, ID> storeService;

	protected String[] properties;

	protected boolean readOnly = false;
	
	protected boolean initialized = false;

	protected VerticalLayout layout;

	protected FormLayout formLayout;
	
	protected BeanFieldGroup<T> fieldGroup;

	protected Map<String, Field<?>> customFields = new HashMap<>();
	
	protected HorizontalLayout buttonLayout;	
	
	protected EnabalebalMenuBar<T> menuBar;
	
	protected Button[] buttons = new Button[] {
			new Button("Save", new Button.ClickListener() {
				@Override
				public void buttonClick(ClickEvent event) {
					try {
						save();
						close();
					} catch (FieldGroup.CommitException e) {
						throw new RuntimeException(e);
					}
				}
			}), new Button("Cancel", new Button.ClickListener() {
				@Override
				public void buttonClick(ClickEvent event) {
					cancel();
					close();
				}
			})
	};

	public EditorView(StorableService<T, ID> storeService) {
		super(storeService.getEntityClass());

		this.storeService = storeService;
		
		menuBar = new EnabalebalMenuBar<T>();
		menuBar.setWidth("100%");

		MenuItem editMenu = menuBar.addItem("edit", null);		
		menuBar.addItem(editMenu, "save", null, new Command() {
	         public void menuSelected(MenuItem selectedItem) { 
	         	try {
						setInstance(save());
						Notification.show("saved...", Notification.Type.TRAY_NOTIFICATION);
					} catch (FieldGroup.CommitException e) {
						throw new RuntimeException(e);
					}
	         }
	      });
		
		menuBar.addItem(editMenu, "reset", NEW_DISABLER, new Command() {
	         @SuppressWarnings("unchecked")
            public void menuSelected(MenuItem selectedItem) { 
					cancel();
					loadInstance((ID) PersistenceUtils.getIdValue(fieldGroup.getItemDataSource().getBean()));
					Notification.show("reset...", Notification.Type.TRAY_NOTIFICATION);
	         }
	      });
		
		if (Duplicatable.class.isAssignableFrom(storeService.getEntityClass())) {
			menuBar.addItem(editMenu, "duplicate", NEW_DISABLER, new Command() {
	         @SuppressWarnings("unchecked")
            public void menuSelected(MenuItem selectedItem) { 
					setInstance(((Duplicatable<T>) fieldGroup.getItemDataSource().getBean()).duplicate());
					Notification.show("duplicated...", Notification.Type.TRAY_NOTIFICATION);
	         }
	      });
		}
		
		menuBar.addItem(editMenu, "delete", NEW_DISABLER, new Command() {
         public void menuSelected(MenuItem selectedItem) { 
				delete();
         }
      });
		menuBar.addItem(editMenu, "close", null, new Command() {
         public void menuSelected(MenuItem selectedItem) { 
				close();
         }
      });		
	}

	@PostConstruct
	public void postConstruct() {

		formLayout = new FormLayout();		
		formLayout.setSpacing(true);
		formLayout.setMargin(true);
		
		fieldGroup = new BeanFieldGroup<T>(getEntityClass());
		fieldGroup.setBuffered(false);
		
		Label shiftRight = new Label();
      buttonLayout = new HorizontalLayout(shiftRight);               
      buttonLayout.setExpandRatio(shiftRight, 1);
      buttonLayout.setWidth(100, Unit.PERCENTAGE);
      buttonLayout.addComponents(buttons);
		
		layout = new VerticalLayout();
		if (menuBar != null) {
			layout.addComponent(menuBar);
		}
		layout.addComponent(formLayout);
		layout.addComponent(buttonLayout);
		setContent(layout);
	}

	public EnabalebalMenuBar<T> getMenuBar() {
		return menuBar;
	}
	
	public T newInstance(Object ...args) {
		T t = storeService.newInstance(args);
		setInstance(t);
		return t;
	}

	public T loadInstance(ID id) {
		T t = storeService.load(id);
		setInstance(t);
		return t;
	}

	public void setInstance(T t) {
		fieldGroup.setReadOnly(readOnly);

		// We need an item data source before we create the fields to be able to
		// find the properties, otherwise we have to specify them by hand
		fieldGroup.setItemDataSource(new BeanItem<T>(t));

		if (!initialized) {
			fieldGroup.setFieldFactory(new RelationalFieldFactory(persistenceService));

			// Loop through the properties
			for (Object propertyId : properties) {
				// build fields for them and add the fields to this UI
				Field<?> f = customFields.get(propertyId); 
				if (f != null) {
					f.setCaption(DefaultFieldFactory.createCaptionByPropertyId(propertyId));
					fieldGroup.bind(f, propertyId);
				} else {
					f = fieldGroup.buildAndBind(propertyId);
				}
				formLayout.addComponent(f);
			}
			initMenus();
			initFields();
			
			initialized = true;
		} 
		
		initMenus(t);
		initFields(t);
	}
	public void initMenus() {
		// void
	}
	public void initMenus(T t) {
		menuBar.enableMenu(t);
	}
	public void initFields() {
		// void
	}
	public void initFields(T t) {
		if (readOnly) {
			for (Object propertyId : properties) {
				fieldGroup.getField(propertyId).setReadOnly(true);			
			}
		} else {			
			if (Codable.class.isAssignableFrom(getEntityClass())) {
				if (((Codable) t).getCode() != null) {
					// we dont want the code to change because it's the entity's id
					fieldGroup.getField("code").setReadOnly(true);
				}
			} else if (DBable.class.isAssignableFrom(getEntityClass())) {
				// set the system properties to readonly
				for (Object propertyId : properties) {
					fieldGroup.getField(propertyId).setReadOnly(DBable.SYSTEM_PROPERTIES.contains(propertyId));			
				}
			}
		}
	}
	
	@SuppressWarnings("unchecked")
	public T duplicate(ID id) {
		T t = storeService.load(id);
		setInstance(((Duplicatable<T>) t).duplicate());
		return t;
	}
	
	@SuppressWarnings("unchecked")
   public void duplicate() {
		setInstance(((Duplicatable<T>) fieldGroup.getItemDataSource().getBean()).duplicate());
	}
	
	public T getInstance() {
		return fieldGroup.getItemDataSource().getBean();
	}
	
	public BeanItem<T> getBeanItem() {
		return fieldGroup.getItemDataSource();
	}
	
	public void setProperties(String... properties) {
		this.properties = properties;
	}

	public void setReadOnly(boolean readOnly) {
		this.readOnly = readOnly;
	}

	public void setCustomField(String propertyId, Field<?> field) {
		customFields.put(propertyId, field);
	}
	
	public Field<?> getField(String propertyId) {
		return fieldGroup.getField(propertyId);
	}
	
	public T save() throws FieldGroup.CommitException {
		fieldGroup.commit();

		T t = storeService.save(fieldGroup.getItemDataSource().getBean());
		
		fireEntityChange(t);

		return t;
	}

	public void fireEntityChange(T t) {
		fireEvent(new EntityChangeEvent(this, PersistenceUtils.getIdValue(t), t));
	}
	
	public void cancel() {
		fieldGroup.discard();
	}
	
	public void delete() {
		final T t = fieldGroup.getItemDataSource().getBean();

		final String text = t.toString();
		ConfirmDialog.show(UI.getCurrent(), "Delete", text, "yes", "no",
				new ConfirmDialog.Listener() {
					private static final long serialVersionUID = 1L;

					public void onClose(ConfirmDialog dialog) {
						if (dialog.isConfirmed()) {
							storeService.delete(t);
							close();
							Notification.show(text, "deleted...", 
									Notification.Type.TRAY_NOTIFICATION);
							
							fireEntityChange(t);
						}
					}
				});
	}

	public void delete(final T t) {
		final String text = t.toString();
		ConfirmDialog.show(UI.getCurrent(), "Delete", text, "yes", "no",
				new ConfirmDialog.Listener() {
					private static final long serialVersionUID = 1L;

					public void onClose(ConfirmDialog dialog) {
						if (dialog.isConfirmed()) {
							storeService.delete(t);
							Notification.show(text, "deleted...",
									Notification.Type.TRAY_NOTIFICATION);
							
							fireEntityChange(t);;
						}
					}
				});
	}

	public void delete(final ID id) {
		final String text = MessageFormat.format("{0} id=''{1}''", getEntityClass().getSimpleName(), id);
		ConfirmDialog.show(UI.getCurrent(), "Delete", text, "yes", "no",
				new ConfirmDialog.Listener() {
					private static final long serialVersionUID = 1L;

					public void onClose(ConfirmDialog dialog) {
						if (dialog.isConfirmed()) {
							storeService.delete(id);							
							Notification.show(text, "deleted...",
									Notification.Type.TRAY_NOTIFICATION);
							
							fireEvent(new EntityChangeEvent(EditorView.this, id, null));
						}
					}
				});
	}

}
