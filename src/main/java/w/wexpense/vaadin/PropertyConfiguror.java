package w.wexpense.vaadin;

public interface PropertyConfiguror {

	String[] systemProperties = { "id", "version", "fullId", "uid", "modifiedTs", "createdTs" };
	String propertyIncludeNonSelectable = ".includeNonSelectable";
	String visibleProperties = "visibleProperties";
	String nestedProperties = "nestedProperties";
	String windowHeight = "window.height";
	String windowWidth = "window.width";
	String propertyWidth = ".width";
	String propertyHeight = ".height";
	String propertyAlignement = ".alignement";
	String propertyExpandRatio = ".expandRatio";
	String propertyDateResolution = ".dateResolution";
	String propertyDateFormat = ".dateFormat";
	String propertyFormat = ".format";
	String propertyHeader = ".header";

	void setPropertyValue(String property, String value);
	
	String getPropertyValue(String property, String defaultValue);

	String getPropertyValue(String property);

	void setPropertyValues(String property, String[] values);

	String[] getPropertyValues(String property, String[] defaultValues);

	String[] getPropertyValues(String property);

}
