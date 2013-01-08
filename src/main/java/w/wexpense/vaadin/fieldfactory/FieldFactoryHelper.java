package w.wexpense.vaadin.fieldfactory;

public class FieldFactoryHelper {

	private static final Object[] systemProperties = {"id", "version", "fullId", "uid", "modifiedTs", "createdTs"} ;

	public static boolean isSystemProperty(Object propertyId) {
		for(Object o: systemProperties) { 
			if (o.equals(propertyId)) return true;
		}
		return false;		
	}
}
