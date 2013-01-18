package w.wexpense.dta;

import static w.wexpense.dta.DtaCommonTestData.createPaymentData;

import java.util.List;

import junit.framework.Assert;

import org.junit.Test;

import w.wexpense.model.Payment;

public class EndDtaFormaterTest {

	String expected = 
	"01000000            00000130215       WEX0100002890000,00                                                                       ";
	@Test
	public void testBvo() {
		Payment payment = createPaymentData(15,2,2013,"test.dta"); 
		List<String> l = new EndDtaFormater().format(payment, 2);
		Assert.assertEquals(1, l.size());
		Assert.assertEquals(128, l.get(0).length());
		Assert.assertEquals(expected, l.get(0));
	}
}
