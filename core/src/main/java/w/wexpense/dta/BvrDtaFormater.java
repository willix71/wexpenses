package w.wexpense.dta;

import static w.wexpense.dta.DtaHelper.formatLine01;
import static w.wexpense.dta.DtaHelper.lineSeparator;
import static w.wexpense.dta.DtaHelper.pad;
import static w.wexpense.model.enums.TransactionLineEnum.OUT;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import w.wexpense.model.Expense;
import w.wexpense.model.Payee;
import w.wexpense.model.Payment;

import com.google.common.base.Preconditions;

public class BvrDtaFormater implements DtaFormater {

	public static final String TRANSACTION_TYPE = "827";
		
	@Override
	public List<String> format(Payment payment, int index, Expense expense) {
		check(expense);
		List<String> lines = new ArrayList<String>();
		lines.add(formatLine01(TRANSACTION_TYPE, payment.getDate(), index, expense, true));
		lines.add(formatLine02(payment, index, expense));
		lines.add(formatLine03(payment, index, expense));
		lines.add(formatLine04(payment, index, expense));
		return lines;
	}
	
	public void check(Expense expense) {
		// Must have a payee.externalReference (account number)
		Preconditions.checkNotNull(DtaHelper.getTransactionLine(OUT, expense).getAccount().getOwner().getIban(), "Out account must have a bank details");		
		Preconditions.checkNotNull(DtaHelper.getTransactionLine(OUT, expense).getAccount().getOwner().getIban(), "Out account's bank details must have an Iban");

		Preconditions.checkNotNull(expense.getPayee().getBankDetails(), "Payee's bank details is mandatory for BVR payments (827)");
		Preconditions.checkNotNull(expense.getPayee().getBankDetails().getPostalAccount(), "Payee's bank details postal account is mandatory for BVR payments (827)");
	}
	
	protected String getProcessingDate(Expense expense) {
		return pad(expense.getDate());
	}
	
	protected String formatLine02(Payment payment, int index, Expense expense) {
		StringBuilder line02 = new StringBuilder();
		line02.append("02");
		
		Payee payee = DtaHelper.getTransactionLine(OUT, expense).getAccount().getOwner();
		line02.append(pad(payee.getName(),24));
		line02.append(pad(payee.getAddress1(),24));
		line02.append(pad(payee.getAddress2(),24));
		line02.append(pad(payee.getCity().toString(),24));

		line02.append(pad(DtaHelper.getExpenseHint(expense),30));
		return line02.toString();
	}
	
	protected String formatLine03(Payment payment, int index, Expense expense) {
		StringBuilder line03 = new StringBuilder();
		line03.append("03");
		
		// beneficiary
		line03.append("/C/");
		
		// ISR party number
		line03.append(pad(expense.getPayee().getBankDetails().getPostalAccount(),27));
		
		line03.append(pad(expense.getPayee().getPrefixedName(), 24));
		line03.append(pad(expense.getPayee().getAddress1(), 24));
		line03.append(pad(expense.getPayee().getAddress2(), 24));
		line03.append(pad(expense.getPayee().getCity().toString(), 24));
		
		return line03.toString();
	}
	
	protected String formatLine04(Payment payment, int index, Expense expense) {
		StringBuilder line04 = new StringBuilder();
		line04.append("04");
		
		// description
		String description = expense.getDescription();		
		if (description == null || !description.contains(lineSeparator))  {
			line04.append(pad(description, 112));
		} else {
			String[] separated = description.split(Pattern.quote(lineSeparator));
			for(int i=0;i<4;i++) {
				if (separated.length > i) {
					line04.append(pad(separated[i],28));
				} else {
					line04.append(pad(28));
				}
			}
		}
		
		line04.append(pad(14));
		return line04.toString();
	}
	
}
