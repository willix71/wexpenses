package w.wexpense.dta;

import static w.wexpense.dta.DtaHelper.getTransactionLine;
import static w.wexpense.dta.DtaHelper.pad;
import static w.wexpense.dta.DtaHelper.zeroPad;
import static w.wexpense.model.enums.TransactionLineEnum.IN;

import java.util.ArrayList;
import java.util.List;

import w.wexpense.model.Expense;
import w.wexpense.model.Payment;

public class BvoDtaFormater extends AbstractDtaFormater {

	public static final String TRANSACTION_TYPE = "826";
	
	public BvoDtaFormater() {
		super(TRANSACTION_TYPE);
	}
	
	@Override
	public List<String> format(Payment payment, int index, Expense expense) {
		List<String> lines = new ArrayList<String>();
		lines.add(formatLine01(payment, index, expense));
		lines.add(formatLine02(payment, index, expense));
		lines.add(formatLine03(payment, index, expense));
		return lines;
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
		padAccountNumber(line03, expense.getPayee().getExternalReference());
		
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
	
	protected void padAccountNumber(StringBuilder sb, String accountNumber) {
		String[] parts = accountNumber.split("-");
		sb.append(zeroPad(Integer.parseInt(parts[0]), 2));
		sb.append(zeroPad(Integer.parseInt(parts[1]), 6));
		sb.append(zeroPad(Integer.parseInt(parts[2]), 1));
	}
}
