package w.wexpense.dta;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import w.wexpense.model.Expense;
import w.wexpense.model.Payment;
import static w.wexpense.dta.DtaHelper.*;
import static w.wexpense.model.enums.TransactionLineEnum.OUT;
import static w.wexpense.model.enums.TransactionLineEnum.IN;

public class BvoDtaFormater implements DtaFormater {

	public static final String TRANSACTION_TYPE = "826";
	
	@Override
	public List<String> format(Payment payment, int index, Expense expense) {
		List<String> lines = new ArrayList<String>();
		lines.add(formatLine01(payment, index, expense));
		lines.add(formatLine02(payment, index, expense));
		lines.add(formatLine03(payment, index, expense));
		return lines;
	}

	protected String formatLine01(Payment payment, int index, Expense expense) {
		StringBuilder line01 = new StringBuilder();
		line01.append("01");
		line01.append(DtaHelper.getHeader(TRANSACTION_TYPE, payment, index, expense));
		line01.append(pad(DtaHelper.APPLICATION_ID,5));
		line01.append("ID");
		line01.append(zeroPad(expense.getId().intValue(), 9));
		
		String iban = getTransactionLine(OUT, expense).getAccount().getExternalReference();
		line01.append(pad(iban,24));
		
		// date (blank mean as indicated in the header)
		line01.append(pad(6));
		// currency
		line01.append(pad(expense.getCurrency().getCode(),3));
		// amount
		String amount = new DecimalFormat("0.00").format(expense.getAmount()).replace(".", ",");
		line01.append(pad(amount,12));
		
		// reserved
		line01.append(pad(14));
		return line01.toString();
	}
	
	protected String formatLine02(Payment payment, int index, Expense expense) {
		StringBuilder line02 = new StringBuilder();
		line02.append("02");
		line02.append(pad("William Keyser",20));
		line02.append(pad("11 ch du Grand Noyer",20));
		line02.append(pad(20));
		line02.append(pad("1197 Prangins",20));
		line02.append(pad(getTransactionLine(IN, expense).getAccount().getFullName(),46));
		return line02.toString();
	}
	
	protected String formatLine03(Payment payment, int index, Expense expense) {
		StringBuilder line03 = new StringBuilder();
		line03.append("03");
		
		// beneficiary
		line03.append("/C/");
		
		// ISR party number
		line03.append(pad(expense.getPayee().getExternalReference(),9));
		
		line03.append(pad(expense.getPayee().getPrefix() + expense.getPayee().getName(), 20));
		line03.append(pad(expense.getPayee().getPostalBox(), 20));
		line03.append(pad(expense.getPayee().getLocation(), 20));
		line03.append(pad(expense.getPayee().getCity().toString(), 20));
		
		// reason for payment
		line03.append(pad(expense.getExternalReference(),27));
		
		// ISR check digit ???
		line03.append(pad(2));
		// reserved
		line03.append(pad(5));
		return line03.toString();
	}
}
