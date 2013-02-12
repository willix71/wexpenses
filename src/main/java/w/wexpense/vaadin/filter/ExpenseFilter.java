package w.wexpense.vaadin.filter;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import w.wexpense.model.Currency;
import w.wexpense.model.ExpenseType;
import w.wexpense.vaadin.WexJPAContainerFactory;
import w.wexpense.vaadin.fieldfactory.WexDateField;

import com.vaadin.addon.jpacontainer.JPAContainer;
import com.vaadin.addon.jpacontainer.fieldfactory.PropertyTranslator;
import com.vaadin.addon.jpacontainer.fieldfactory.SingleSelectTranslator;
import com.vaadin.data.Container;
import com.vaadin.data.Container.Filter;
import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.util.IndexedContainer;
import com.vaadin.data.util.ObjectProperty;
import com.vaadin.data.util.filter.And;
import com.vaadin.data.util.filter.Compare;
import com.vaadin.data.util.filter.Like;
import com.vaadin.event.FieldEvents.TextChangeEvent;
import com.vaadin.event.FieldEvents.TextChangeListener;
import com.vaadin.ui.AbstractSelect;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.DateField;
import com.vaadin.ui.Field;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.NativeSelect;
import com.vaadin.ui.TextField;

public class ExpenseFilter extends HorizontalLayout implements WexFilter {

	private static final long serialVersionUID = -4349643141593583652L;
	
	private final Logger LOGGER = LoggerFactory.getLogger(getClass());
	
	@Autowired
	private WexJPAContainerFactory jpaContainerFactory;
	
	private JPAContainer<?> jpaContainer;
	
	private Map<String, Filter> filters = new HashMap<String, Filter>();
	
	private String dateField = "date";
	private Date dateValue;
	
	public ExpenseFilter() {
		super();
	}

	@Override
	public void setJPAContainer(JPAContainer<?> jpaContainer) {
		this.jpaContainer = jpaContainer;
		buildFilter();
	}	
	
	protected void buildFilter() {				
		addComponent(getDateFieldFilter());
		
		addComponent(getDateFilter());

		addComponent(getAmountField("amount"));
		
		addComponent(getComboBox(Currency.class, "currency"));

		addComponent(getTextField("payee.display", "payee"));
		
		addComponent(getComboBox(ExpenseType.class, "type"));
		
		addComponent(getTextField("externalReference", "external reference"));
	}
	
	protected void updateFilters() {
		jpaContainer.setApplyFiltersImmediately(false);
		jpaContainer.removeAllContainerFilters();
		
		if (filters.size() > 0) {
			// converting values to an array of filters
			Filter[] fs = new Filter[filters.size()];
			int i=0;
			for(Filter f: filters.values()) {
				fs[i++] = f;
			}
			
			LOGGER.info("Applying filter {}", fs);
			
			jpaContainer.addContainerFilter(new And(fs));
		}
		jpaContainer.applyFilters();
	}

	protected Field getDateFieldFilter() {
		IndexedContainer c = new IndexedContainer();
		c.addItem("date");
		c.addItem("createdTs");
		c.addItem("modifiedTs");

		NativeSelect dateType = new NativeSelect("", c);
		dateType.setNullSelectionAllowed(false);
		dateType.setImmediate(true);
		dateType.setValue(dateField);
		dateType.addListener(new Property.ValueChangeListener() {			
			@Override
			public void valueChange(ValueChangeEvent event) {
				dateField = (String) event.getProperty().getValue();
				updateDateFilter();
			}
		});
		return dateType;
	}
	
	protected Field getDateFilter() {
		DateField dateFilter = new WexDateField();
		dateFilter.setCaption("date");
		dateFilter.setImmediate(true);
		dateFilter.setDateFormat("dd.MM.yyyy");
		dateFilter.setResolution(DateField.RESOLUTION_DAY);
		dateFilter.addListener(new Property.ValueChangeListener() {			
			@Override
			public void valueChange(ValueChangeEvent event) {
				dateValue = (Date) event.getProperty().getValue();
				updateDateFilter();
			}
		});
		return dateFilter;
	}
	
	protected void updateDateFilter() {
		if (dateValue == null) {
			filters.remove("date");
		}	else {
			filters.put("date", new Compare.GreaterOrEqual(dateField,dateValue));
		}
		updateFilters();
	}
	
	private TextField getAmountField(final String propertyId) {
		final TextField txtfield = new TextField(propertyId);
		txtfield.setWidth("120px");
		txtfield.addListener(new TextChangeListener() {			
			@Override
			public void textChange(TextChangeEvent event) {
				String ref = event.getText();
				if (ref == null || ref.trim().length()==0) {
					filters.remove(propertyId);
				} else {
					if (ref.startsWith(">=")) {
						Double value = Double.parseDouble(ref.substring(2));
						filters.put(propertyId, new Compare.GreaterOrEqual(propertyId, value));						
					} else if (ref.startsWith("<=")) {						
						Double value = Double.parseDouble(ref.substring(2));
						filters.put(propertyId, new Compare.LessOrEqual(propertyId, value));						
					} else if (ref.startsWith(">")) {
						Double value = Double.parseDouble(ref.substring(1));
						filters.put(propertyId, new Compare.Greater(propertyId, value));												
					} else if (ref.startsWith("<")) {
						Double value = Double.parseDouble(ref.substring(1));
						filters.put(propertyId, new Compare.Less(propertyId, value));												
					} else if (ref.startsWith("=")) {
						Double value = Double.parseDouble(ref.substring(1));
						filters.put(propertyId, new Compare.Equal(propertyId, value));						
					} else {
						Double value = Double.parseDouble(ref);
						filters.put(propertyId, new Compare.Equal(propertyId, value));
					}					
				}
				txtfield.setComponentError(null);
				updateFilters();
			}
		});
		return txtfield;
	}
	
	private TextField getTextField(final String propertyId, String caption) {
		final TextField txtfield = new TextField(caption);
		txtfield.addListener(new TextChangeListener() {			
			@Override
			public void textChange(TextChangeEvent event) {
				String ref = event.getText();
				if (ref == null || ref.trim().length()==0) {
					filters.remove( propertyId);
				} else {
					filters.put( propertyId, new Like( propertyId, "%" + ref + "%", false));
				}
				
				updateFilters();
			}
		});
		return txtfield;
	}
	
	private ComboBox getComboBox(Class<?> entityClass, final String propertyId) {
		final ComboBox box = new ComboBox(propertyId);
		box.setImmediate(true);
		box.setMultiSelect(false);
		
		Container c = jpaContainerFactory.getJPAContainer(entityClass);
		box.setContainerDataSource(c);
		PropertyTranslator t = new SingleSelectTranslator(box);
		box.setPropertyDataSource(t);
		
		box.setItemCaptionMode(NativeSelect.ITEM_CAPTION_MODE_ITEM);
		box.setFilteringMode(AbstractSelect.Filtering.FILTERINGMODE_CONTAINS);
		box.setWidth("60px");
		box.addListener(new Property.ValueChangeListener() {			
				@Override
				public void valueChange(ValueChangeEvent event) {
					Object o = event.getProperty().getValue();	
					
					if (o==null) {
						filters.remove(propertyId);
					}	else {
						filters.put(propertyId, new Compare.Equal(propertyId,o));
					}
					
					updateFilters();
				}			

		});
		return box;
	}
}
