package w.wexpense.vaadin.view;

import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import w.wexpense.vaadin.PropertyConfiguror;

import com.vaadin.data.Container;
import com.vaadin.data.Property;
import com.vaadin.ui.Table;
import com.vaadin.ui.TreeTable;

public class WexTreeTable extends TreeTable {

	private static final long serialVersionUID = 4938477934043248461L;

	private PropertyConfiguror propertyConfiguror;
	
	public WexTreeTable(Container dataSource, PropertyConfiguror propertyConfiguror) {
		super(null, dataSource);
		this.propertyConfiguror = propertyConfiguror;
		
		String[] visibleProperties = propertyConfiguror.getPropertyValues(PropertyConfiguror.visibleProperties);
		if (visibleProperties != null) {
			setVisibleColumns(visibleProperties);
		}

		for(String pid: visibleProperties) {
			String p = propertyConfiguror.getPropertyValue(pid + PropertyConfiguror.propertyAlignement);
			if (p!=null) setColumnAlignment(pid, Table.Align.CENTER.convertStringToAlign(p));
			p = propertyConfiguror.getPropertyValue(pid + PropertyConfiguror.propertyExpandRatio);
			if (p!=null) setColumnExpandRatio(pid, Float.valueOf(p));
			p = propertyConfiguror.getPropertyValue(pid + PropertyConfiguror.propertyHeader);
			setColumnHeader(pid, p);
		}		
	}
	
	@Override
	protected String formatPropertyValue(Object rowId, Object colId, Property<?> property) {
        if (property == null || property.getValue() == null) {
            return "";
        }
        String format = propertyConfiguror.getPropertyValue(colId + PropertyConfiguror.propertyFormat);
        if (format != null) {
        	return MessageFormat.format(format, property.getValue());
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
