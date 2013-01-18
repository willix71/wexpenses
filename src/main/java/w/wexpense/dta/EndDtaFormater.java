package w.wexpense.dta;

import static w.wexpense.dta.DtaHelper.pad;
import static w.wexpense.dta.DtaHelper.zeroPad;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import w.wexpense.model.Expense;
import w.wexpense.model.Payment;

public class EndDtaFormater {

	public static final String TRANSACTION_TYPE = "890";	
	

	public List<String> format(Payment payment, int index) {
		List<String> lines = new ArrayList<String>();
		lines.add(formatLine01(payment, index));
		return lines;
	}
	
	protected String formatLine01(Payment payment, int index) {
		StringBuilder line01 = new StringBuilder();
		line01.append("01");
		line01.append(zeroPad(0,6));
		line01.append(DtaHelper.getHeader(TRANSACTION_TYPE, payment, index, null));
		
		double d = 0;
		for(Expense expense: payment.getExpenses()) {
			d += expense.getAmount().doubleValue();
		}
		String amount = new DecimalFormat("0.00").format(d).replace(".", ",");
		line01.append(pad(amount,16));
		
		line01.append(pad(59));
		
		return line01.toString();
	}
}
