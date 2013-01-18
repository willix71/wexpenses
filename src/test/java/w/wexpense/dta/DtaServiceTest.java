package w.wexpense.dta;

import static w.wexpense.dta.DtaCommonTestData.createPaymentData;

import java.util.List;

import junit.framework.Assert;

import org.junit.Test;

import w.wexpense.model.Payment;

public class DtaServiceTest {

	@Test
	public void testBvo() throws Exception {
		Payment payment = createPaymentData(15,2,2013,"test.dta", 
				BvoDtaFormaterTest.getBvoExpense(),
				BvrDtaFormaterTest.getBvrExpense(),
				IbanDtaFormaterTest.getIbanExpense()); 
		DtaService service = new DtaService();
		List<String> lines = service.getPaymentDtaLines(payment);
		Assert.assertNotNull(lines);
		int i = 1;
		for (String line: lines) {	
			System.out.println(line + "]]");
			Assert.assertEquals("line "+i+"'s length is not 128",128, line.length());
		}
	}
}
