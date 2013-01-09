package w.wexpense.vaadin.fieldfactory;

import java.util.Properties;

import com.vaadin.ui.Table;

public class FieldFactoryHelper {

	private static final Object[] systemProperties = {"id", "version", "fullId", "uid", "modifiedTs", "createdTs"} ;

	public static boolean isSystemProperty(Object propertyId) {
		for(Object o: systemProperties) { 
			if (o.equals(propertyId)) return true;
		}
		return false;		
	}
	
	private static final Properties properties = new Properties();
	
	public static String getProperty(Class<?> type, String ... path) {
		String key = type.getSimpleName();
		for(String p:path) key+="."+p;
		return properties.getProperty(key);
	}
	public static String[] getPropertyArray(Class<?> type, String ... path) {
		String value = getProperty(type, path);
		if (value == null) return null;
		return value.split(",");
	}
	
	static {
		properties.put("Currency.viewer.visibleProperties", "code,name");
		properties.put("Currency.editor.visibleProperties", "code,name");
		properties.put("Country.viewer.visibleProperties", "code,name,currency");
		properties.put("Country.editor.visibleProperties", "code,name,currency");
		properties.put("City.viewer.visibleProperties", "name,zip,country");		
		properties.put("City.editor.visibleProperties", "fullId,uid,name,zip,country");
		properties.put("Payee.viewer.visibleProperties", "type,prefix,name,location,city");
		properties.put("Payee.editor.visibleProperties", "fullId,uid,type,prefix,name,location,city,externalReference");
		properties.put("PayeeType.viewer.visibleProperties", "name,selectable");
		properties.put("PayeeType.editor.visibleProperties", "fullId,uid,name,selectable");
		properties.put("Expense.viewer.visibleProperties", "date,amount,currency,payee,type");
		properties.put("Expense.editor.visibleProperties", "fullId,uid,date,amount,currency,payee,type,externalReference,transactions");
		properties.put("Expense.viewer.amount.alignement", Table.ALIGN_RIGHT);
		properties.put("Expense.viewer.payee.expandRatio", ".8");
		properties.put("ExpenseType.viewer.visibleProperties", "name,selectable");
		properties.put("ExpenseType.editor.visibleProperties", "fullId,uid,name,selectable");
		properties.put("TransactionLine.nestedProperties", "expense.date,expense.payee");
		properties.put("TransactionLine.viewer.visibleProperties", "expense.date,expense.payee,account,inAmount,outAmount");
		properties.put("TransactionLine.viewer.inAmount.alignement", Table.ALIGN_RIGHT);
		properties.put("TransactionLine.viewer.outAmount.alignement", Table.ALIGN_RIGHT);
		properties.put("TransactionLine.viewer.account.expandRatio", ".2");
		properties.put("TransactionLine.viewer.expense.payee.expandRatio", ".8");
		properties.put(".editor.visibleProperties", "");
		properties.put(".viewer.visibleProperties", "");
		properties.put(".editor.visibleProperties", "");
		properties.put(".viewer.visibleProperties", "");
		properties.put(".editor.visibleProperties", "");
	}
}
