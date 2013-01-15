package w.wexpense.vaadin;

public interface PropertyConfiguror {

	String[] systemProperties = { "id", "version", "fullId", "uid", "modifiedTs", "createdTs" };
	String propertyIncludeNonSelectable = ".includeNonSelectable";
	String visibleProperties = "visibleProperties";
	String nestedProperties = "nestedProperties";
	String propertyAlignement = ".alignement";
	String propertyExpandRatio = ".expandRatio";
	String propertyDateResolution = ".dateResolution";
	String propertyDateFormat = ".dateFormat";
	String propertyFormat = ".format";
	String propertyHeader = ".header";

	String getPropertyValue(String property, String defaultValue);

	String getPropertyValue(String property);

	String[] getPropertyValues(String property, String[] defaultValues);

	String[] getPropertyValues(String property);

}
