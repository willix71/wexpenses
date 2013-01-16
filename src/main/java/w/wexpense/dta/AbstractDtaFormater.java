package w.wexpense.dta;

import static w.wexpense.dta.DtaHelper.getTransactionLine;
import static w.wexpense.dta.DtaHelper.pad;
import static w.wexpense.dta.DtaHelper.zeroPad;
import static w.wexpense.model.enums.TransactionLineEnum.IN;
import static w.wexpense.model.enums.TransactionLineEnum.OUT;

import java.text.DecimalFormat;

import w.wexpense.model.Expense;
import w.wexpense.model.Payment;

public abstract class AbstractDtaFormater implements DtaFormater {

	private String transactionType;
	
	public AbstractDtaFormater(String transactionType) {
		this.transactionType = transactionType;
	}

	protected String formatLine01(Payment payment, int index, Expense expense) {
		StringBuilder line01 = new StringBuilder();
		line01.append("01");
		line01.append(DtaHelper.getHeader(transactionType, payment, index, expense));
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

	
}
