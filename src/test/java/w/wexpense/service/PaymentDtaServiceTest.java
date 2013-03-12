package w.wexpense.service;

import static w.wexpense.dta.DtaCommonTestData.createPaymentData;
import junit.framework.Assert;

import org.junit.Test;

import w.wexpense.dta.BvoDtaFormaterTest;
import w.wexpense.dta.BvrDtaFormaterTest;
import w.wexpense.dta.IbanDtaFormaterTest;
import w.wexpense.model.Payment;
import w.wexpense.model.PaymentDta;
import w.wexpense.service.model.PaymentDtaService;

public class PaymentDtaServiceTest {

	@Test
	public void testBvo() throws Exception {
		Payment payment = createPaymentData(15,2,2013,"test.dta", 
				BvoDtaFormaterTest.getBvoExpense(),
				BvrDtaFormaterTest.getBvrExpense(),
				IbanDtaFormaterTest.getIbanExpense()); 
		
		PaymentDtaService service = new PaymentDtaService(null);
		
		int i = 1;
		for (PaymentDta dta: service.getPaymentDtas(payment)) {	
			String line = dta.getData();
			System.out.println(line + "]]");
			Assert.assertEquals("line "+i+"'s length is not 128",128, line.length());
		}
	}
}
