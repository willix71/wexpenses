package w.wexpense.vaadin;

public interface PropertyConfiguror {
	
	String[] systemProperties = {"id", "version", "fullId", "uid", "modifiedTs", "createdTs"} ;
	String visibleProperties = "visibleProperties";
	String nestedProperties = "nestedProperties";
	String propertyAlignement = ".alignement";
	String propertyExpandRatio = ".expandRatio";
	
	String getPropertyValue(String property);
	
	String[] getPropertyValues(String property);
	
}