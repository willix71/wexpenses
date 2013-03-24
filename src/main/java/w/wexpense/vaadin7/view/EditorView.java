package w.wexpense.vaadin7.view;

import java.io.Serializable;
import java.text.MessageFormat;
import java.util.Arrays;
import java.util.List;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.vaadin.dialogs.ConfirmDialog;

import w.wexpense.persistence.PersistenceUtils;
import w.wexpense.service.EntityService;
import w.wexpense.service.PersistenceService;
import w.wexpense.service.StorableService;
import w.wexpense.vaadin7.component.RelationalFieldFactory;
import w.wexpense.vaadin7.event.EntityChangeEvent;

import com.vaadin.data.fieldgroup.BeanFieldGroup;
import com.vaadin.data.fieldgroup.FieldGroup;
import com.vaadin.data.util.BeanItem;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Field;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Notification;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;

public class EditorView<T, ID extends Serializable> extends GenericView<T> {

	private static final long serialVersionUID = 5282517667310057582L;

	public static final List<String> systemProperties = Arrays.asList("id",
			"version", "fullId", "uid", "modifiedTs", "createdTs");

	@Autowired
	protected PersistenceService persistenceService;

	protected StorableService<T, ID> storeService;

	protected String[] properties;

	protected boolean readOnly = false;

	protected VerticalLayout layout;

	protected FormLayout formLayout;
	
	protected BeanFieldGroup<T> fieldGroup;

	protected HorizontalLayout buttonLayout;

	protected Button[] buttons = new Button[] {
			new Button("Save", new Button.ClickListener() {
				@Override
				public void buttonClick(ClickEvent event) {
					try {
						save();
					} catch (FieldGroup.CommitException e) {
						throw new RuntimeException(e);
					}
				}
			}), new Button("Cancel", new Button.ClickListener() {
				@Override
				public void buttonClick(ClickEvent event) {
					cancel();
				}
			}), new Button("Delete", new Button.ClickListener() {
				@Override
				public void buttonClick(ClickEvent event) {
					delete();
				}
			}) };

	public EditorView(EntityService<T, ID> storeService) {
		super(storeService.getEntityClass());

		this.storeService = storeService;
	}

	@PostConstruct
	public void postConstruct() {

		formLayout = new FormLayout();		
		
		fieldGroup = new BeanFieldGroup<T>(getEntityClass());

		buttonLayout = new HorizontalLayout(buttons);

		layout = new VerticalLayout();
		layout.addComponent(formLayout);
		layout.addComponent(buttonLayout);
		setContent(layout);
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
		formLayout.setSpacing(true);
		formLayout.setMargin(true);
		fieldGroup.setReadOnly(readOnly);

		fieldGroup.setFieldFactory(new RelationalFieldFactory(persistenceService));

		// We need an item data source before we create the fields to be able to
		// find the properties, otherwise we have to specify them by hand
		fieldGroup.setItemDataSource(new BeanItem<T>(t));

		formLayout.removeAllComponents();
		
		// Loop through the properties
		for (Object propertyId : properties) {
			// build fields for them and add the fields to this UI
			Field<?> f = fieldGroup.buildAndBind(propertyId);
			f.setReadOnly(readOnly || systemProperties.contains(propertyId));
			formLayout.addComponent(f);
		}
	}

	public void setProperties(String... properties) {
		this.properties = properties;
	}

	public void setReadOnly(boolean readOnly) {
		this.readOnly = readOnly;
	}

	public void save() throws FieldGroup.CommitException {
		fieldGroup.commit();

		T t = storeService.save(fieldGroup.getItemDataSource().getBean());
		
		fireEvent(new EntityChangeEvent(this, PersistenceUtils.getIdValue(t), t));
		
		close();
	}

	public void cancel() {
		fieldGroup.discard();

		close();
	}

	public void setEnalbleDelete(boolean visible) {
		buttons[2].setVisible(visible);
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
							
							fireEvent(new EntityChangeEvent(EditorView.this, PersistenceUtils.getIdValue(t), t));
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
														
							fireEvent(new EntityChangeEvent(EditorView.this, PersistenceUtils.getIdValue(t), t));
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
