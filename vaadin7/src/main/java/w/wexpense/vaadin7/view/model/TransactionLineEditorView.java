package w.wexpense.vaadin7.view.model;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;

import w.wexpense.model.DBable;
import w.wexpense.model.TransactionLine;
import w.wexpense.vaadin7.component.RelationalFieldFactory;
import w.wexpense.vaadin7.container.ContainerService;
import w.wexpense.vaadin7.view.GenericView;

import com.vaadin.data.Property;
import com.vaadin.data.fieldgroup.BeanFieldGroup;
import com.vaadin.data.fieldgroup.FieldGroup;
import com.vaadin.data.util.BeanItem;
import com.vaadin.ui.AbstractComponent;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.DefaultFieldFactory;
import com.vaadin.ui.Field;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;

@org.springframework.stereotype.Component
@Scope("prototype")
public class TransactionLineEditorView extends GenericView<TransactionLine> {

	private static final long serialVersionUID = 5282517667310057582L;

	@Autowired
	protected ContainerService persistenceService;

	protected String[] properties = {"fullId","uid","account","discriminator","factor","amount","exchangeRate","value","balance","period","date","consolidation","description"};

	protected boolean readOnly = false;
	
	protected boolean initialized = false;

	protected VerticalLayout layout;

	protected FormLayout formLayout;
	
	protected BeanFieldGroup<TransactionLine> fieldGroup;

	protected Map<String, Field<?>> customFields = new HashMap<>();
	
	protected HorizontalLayout buttonLayout;	
	
	protected Button[] buttons = new Button[] {
			new Button("close", new Button.ClickListener() {
				@Override
				public void buttonClick(ClickEvent event) {
					try {
						save();
						close();
					} catch (FieldGroup.CommitException e) {
						throw new RuntimeException(e);
					}
				}
			})
	};

	public TransactionLineEditorView() {
		super(TransactionLine.class);

	}

	@PostConstruct
	public void postConstruct() {
		formLayout = new FormLayout();		
		formLayout.setSpacing(true);
		formLayout.setMargin(true);
		
		fieldGroup = new BeanFieldGroup<TransactionLine>(getEntityClass());
		fieldGroup.setBuffered(false);
		
		Label shiftRight = new Label();
      buttonLayout = new HorizontalLayout(shiftRight);               
      buttonLayout.setExpandRatio(shiftRight, 1);
      buttonLayout.setWidth(100, Unit.PERCENTAGE);
      buttonLayout.addComponents(buttons);
		
		layout = new VerticalLayout();

		layout.addComponent(formLayout);
		layout.addComponent(buttonLayout);
		setContent(layout);
	}


	public void setBeanItem(BeanItem<TransactionLine> bean) {
		fieldGroup.setReadOnly(readOnly);

		// We need an item data source before we create the fields to be able to
		// find the properties, otherwise we have to specify them by hand
		fieldGroup.setItemDataSource(bean);

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
				// to have a validation straight away
				((AbstractComponent)f).setImmediate(true);
				
				formLayout.addComponent(f);
			}
			initFields();
			
			initialized = true;
		} 
		
		initFields(bean.getBean());
	}

	public void initFields() {
		final Field<?> valueField = fieldGroup.getField("value");
		fieldGroup.getField("amount").addValueChangeListener(new Property.ValueChangeListener() {
			@Override
			public void valueChange(Property.ValueChangeEvent event) {
				valueField.markAsDirty();
			}
		});
		fieldGroup.getField("exchangeRate").addListener(new com.vaadin.ui.Component.Listener() {
			@Override
			public void componentEvent(com.vaadin.ui.Component.Event event) {			
				valueField.markAsDirty();
			}
		});
	}
	
	public void initFields(TransactionLine t) {
		if (readOnly) {
			for (Object propertyId : properties) {
				fieldGroup.getField(propertyId).setReadOnly(true);			
			}
		} else {
			// set the system properties to readonly
			for (Object propertyId : properties) {
				fieldGroup.getField(propertyId).setReadOnly(DBable.SYSTEM_PROPERTIES.contains(propertyId));
			}
		}
	}

	public TransactionLine getInstance() {
		return fieldGroup.getItemDataSource().getBean();
	}
	
	public BeanItem<TransactionLine> getBeanItem() {
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
	
	public void save() throws FieldGroup.CommitException {				

		if (!fieldGroup.isValid()) {
			throw new FieldGroup.CommitException("Not valid");
		}
		
		// will not do much because we are not in a buffered mode
		fieldGroup.commit();
	}	
}
