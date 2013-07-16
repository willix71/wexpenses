package w.wexpense.dta;

import static w.wexpense.dta.DtaHelper.formatLine01;
import static w.wexpense.dta.DtaHelper.pad;
import static w.wexpense.dta.DtaHelper.stripBlanks;
import static w.wexpense.dta.DtaHelper.zeroPad;
import static w.wexpense.model.enums.TransactionLineEnum.OUT;

import java.util.ArrayList;
import java.util.List;

import w.wexpense.model.Expense;
import w.wexpense.model.Payee;
import w.wexpense.model.Payment;

import com.google.common.base.Preconditions;
public class BvoDtaFormater implements DtaFormater {

	public static final String TRANSACTION_TYPE = "826";
	
	@Override
	public List<String> format(Payment payment, int index, Expense expense) {
		check(expense); 
		List<String> lines = new ArrayList<String>();
		lines.add(formatLine01(TRANSACTION_TYPE, payment.getDate(), index, expense, true));
		lines.add(formatLine02(payment, index, expense));
		lines.add(formatLine03(payment, index, expense));
		return lines;
	}
	
	public void check(Expense expense) {
		// Must have an externalReference and a payee.externalReference
		Preconditions.checkNotNull(DtaHelper.getTransactionLine(OUT, expense).getAccount().getOwner(), "Out account must have a bank details");		
		Preconditions.checkNotNull(DtaHelper.getTransactionLine(OUT, expense).getAccount().getOwner().getIban(), "Out account's bank details must have an Iban");
		Preconditions.checkNotNull(expense.getExternalReference(), "External reference is mandatory for BVO payments (826)");
		Preconditions.checkNotNull(expense.getPayee().getPostalAccount(), "Payee's postal account is mandatory for BVO payments (826)");
	}	
	
	protected String formatLine02(Payment payment, int index, Expense expense) {
		StringBuilder line02 = new StringBuilder();
		line02.append("02");
		
		Payee payee = DtaHelper.getTransactionLine(OUT, expense).getAccount().getOwner();
		line02.append(pad(payee.getName(),20));
		line02.append(pad(payee.getAddress1(),20));
		line02.append(pad(payee.getAddress2(),20));
		line02.append(pad(payee.getCity().toString(),20));
		
		line02.append(pad(DtaHelper.getExpenseHint(expense),46));
		return line02.toString();
	}
		
	protected String formatLine03(Payment payment, int index, Expense expense) {
		StringBuilder line03 = new StringBuilder();
		line03.append("03");
		
		// beneficiary
		line03.append("/C/");
		
		// ISR party number
		line03.append(paddedAccountNumber(expense.getPayee().getPostalAccount()));
		
		line03.append(pad(expense.getPayee().getPrefixedName(), 20));
		line03.append(pad(expense.getPayee().getAddress1(), 20));
		line03.append(pad(expense.getPayee().getAddress2(), 20));
		line03.append(pad(expense.getPayee().getCity().toString(), 20));
		
		// reason for payment
		String reference = stripBlanks(expense.getExternalReference());
		line03.append(zeroPad(reference, 27));
		
		// ISR check digit ???
		line03.append(pad(2));
		// reserved
		line03.append(pad(5));
		return line03.toString();
	}
	
	public static String paddedAccountNumber(String accountNumber) {
		String[] parts = accountNumber.split("-");
		return zeroPad(Integer.parseInt(parts[0]), 2) +
			zeroPad(Integer.parseInt(parts[1]), 6) +
			zeroPad(Integer.parseInt(parts[2]), 1);
	}

}
