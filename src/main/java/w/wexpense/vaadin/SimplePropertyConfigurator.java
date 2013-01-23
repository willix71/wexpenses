package w.wexpense.vaadin;

import java.util.Map;
import java.util.Properties;

import com.google.common.base.Joiner;

public class SimplePropertyConfigurator implements PropertyConfiguror {
	
	private Properties values = new Properties();
		
	@Override
	public void setPropertyValue(String key, String v) {
		values.put(key,v);
	}
	
	@Override
	public String getPropertyValue(String key) {
		return getPropertyValue(key,null);
	}
	
	@Override
	public String getPropertyValue(String key, String defaultValue) {
		String s = values.getProperty(key);
		return s==null?defaultValue:s;
	}
	
	@Override
	public void setPropertyValues(String key, String[] v) {
		values.put(key,Joiner.on(",").join(v));
	}
	
	@Override
	public String[] getPropertyValues(String key) {
		return getPropertyValues(key,null);		
	}
	
	@Override
	public String[] getPropertyValues(String key, String[] defaultValues) {
		String s = values.getProperty(key);
		return s==null?null:s.split(",");		
	}

	public void setVisibleProperties(String value) {
		values.put(visibleProperties, value);
	}
	
	public void setNestedProperties(String value) {
		values.put(nestedProperties, value);
	}
	
	public void setWindowHeight(String value) {
		values.put(windowHeight, value);
	}
	
	public void setWindowWidth(String value) {
		values.put(windowWidth, value);
	}
	
	public void setOtherProperies(Map<String, String> m) {
		values.putAll(m);
	}
}
