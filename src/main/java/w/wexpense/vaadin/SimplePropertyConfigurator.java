package w.wexpense.vaadin;

import java.util.Map;
import java.util.Properties;

public class SimplePropertyConfigurator implements PropertyConfiguror {
	
	private Properties values = new Properties();
	
	public String getPropertyValue(String key) {
		return values.getProperty(key);
	}
	
	public String[] getPropertyValues(String key) {
		String s = values.getProperty(key);
		return s==null?null:s.split(",");		
	}

	public void setVisibleProperties(String value) {
		values.put(visibleProperties, value);
	}
	
	public void setNestedProperties(String value) {
		values.put(nestedProperties, value);
	}
	
	public void setOtherProperies(Map<String, String> m) {
		values.putAll(m);
	}
}
