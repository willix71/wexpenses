package w.wexpense.vaadin.fieldfactory;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import junit.framework.Assert;

import org.junit.Test;

public class WexDateFieldTest {

   final String DAY;
   final String MONTH;
   final String YEAR;
   final String TIME = " 000000";
   public WexDateFieldTest() {
      Calendar c = Calendar.getInstance();
      int i = c.get(Calendar.YEAR);
      YEAR = i + "";
      i = c.get(Calendar.MONTH) + 1;
      MONTH = i < 10 ? "0" + i: "" + i;
      i = c.get(Calendar.DAY_OF_MONTH);
      DAY = i < 10 ? "0" + i: "" + i;
   }
	   
	protected void assertEquals(String expected, Date actual) {
		SimpleDateFormat formater = new SimpleDateFormat("ddMMyyyy HHmmss");
		Assert.assertEquals(expected, formater.format(actual));
	}
	      
	@Test
	public void testWexDateField() {
		assertEquals("23"+MONTH+YEAR+TIME,WexDateField.getEasyDate("23"));
		assertEquals("1212"+YEAR+TIME,WexDateField.getEasyDate("12.12"));
		assertEquals("1212"+YEAR+" 121200",WexDateField.getEasyDate("12.12 12:12"));
		assertEquals(DAY+MONTH+YEAR+TIME,WexDateField.getEasyDate(" "));
		assertEquals(DAY+MONTH+YEAR+" 002001",WexDateField.getEasyDate(" 0:20:1"));
	}
}
