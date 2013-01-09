package w.wexpense.vaadin;

public interface PropertyConfiguror {
	
	String visibleProperties = "visibleProperties";
	String nestedProperties = "nestedProperties";
	String propertyAlignement = ".alignement";
	String propertyExpandRatio = ".expandRatio";
	
	String getPropertyValue(String property);
	
	String[] getPropertyValues(String property);
	
}
