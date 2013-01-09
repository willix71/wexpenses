package w.wexpense.vaadin.view;

import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import w.wexpense.vaadin.PropertyConfiguror;

import com.vaadin.data.Container;
import com.vaadin.data.Property;
import com.vaadin.ui.Table;

public class WexTable extends Table {

	private static final long serialVersionUID = 4938477934043248461L;

	private PropertyConfiguror propertyConfiguror;
	
	public WexTable(Container dataSource, PropertyConfiguror propertyConfiguror) {
		super(null, dataSource);
		this.propertyConfiguror = propertyConfiguror;
	}
	
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
}
