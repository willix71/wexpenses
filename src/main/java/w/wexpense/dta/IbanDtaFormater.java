package w.wexpense.dta;

import static w.wexpense.dta.DtaHelper.formatLine01;
import static w.wexpense.dta.DtaHelper.getTransactionLine;
import static w.wexpense.dta.DtaHelper.lineSeparator;
import static w.wexpense.dta.DtaHelper.pad;
import static w.wexpense.dta.DtaHelper.stripBlanks;
import static w.wexpense.model.enums.TransactionLineEnum.IN;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import com.google.common.base.Preconditions;

import w.wexpense.model.Expense;
import w.wexpense.model.Payee;
import w.wexpense.model.Payment;

public class IbanDtaFormater extends AbstractDtaFormater {

	public static final String TRANSACTION_TYPE = "836";

	public IbanDtaFormater() {
		super(TRANSACTION_TYPE);
	}
		
	@Override
	public List<String> format(Payment payment, int index, Expense expense) {
		check(expense);
		List<String> lines = new ArrayList<String>();
		lines.add(formatLine01(TRANSACTION_TYPE, payment.getDate(), index, expense, false));
		lines.add(formatLine02(payment, index, expense));
		lines.add(formatLine03(payment, index, expense));
		lines.add(formatLine04(payment, index, expense));
		lines.add(formatLine05(payment, index, expense));
		return lines;
	}

	public void check(Expense expense) {
		// Must have a payee.bankingDetail and an payee.externalReference (IBAN)
		Preconditions.checkNotNull(expense.getPayee().getBankDetails(), "Payee's banking detail is mandatory for Iban payments (836)");
		Preconditions.checkNotNull(expense.getPayee().getExternalReference(), "Payee's external reference is mandatory for Iban payments (836)");
		// TODO check external reference is an Iban
	}
	
	protected String formatLine02(Payment payment, int index, Expense expense) {
		StringBuilder line02 = new StringBuilder();

			line02.append("02");
			
			// conversation rate if agreed
			line02.append(pad(12));
			
			Payee payee = DtaHelper.getUserDetail();
			line02.append(pad(payee.getName(),35));
			line02.append(pad(payee.getAddress1(),35));
			line02.append(pad(payee.getCity().toString(),35));

			line02.append(pad(getTransactionLine(IN, expense).getAccount().getFullName(),9));
			return line02.toString();
	}
	
	protected String formatLine03(Payment payment, int index, Expense expense) {
		StringBuilder line03 = new StringBuilder();
		line03.append("03");
		
		// A or D
		line03.append("D");
		
		// beneficiary
		Payee institution = expense.getPayee().getBankDetails();
		line03.append(pad(institution.getPrefixedName(), 35));
		line03.append(pad(institution.getCity().toString(), 35));

		// IBAN
		line03.append(pad(stripBlanks(expense.getPayee().getExternalReference()),34));

		line03.append(pad(21));
		return line03.toString();
	}
	
	protected String formatLine04(Payment payment, int index, Expense expense) {
		StringBuilder line04 = new StringBuilder();
		line04.append("04");
				
		// Beneficiary
		line04.append(pad(expense.getPayee().getPrefixedName(), 35));
		line04.append(pad(expense.getPayee().getAddress1(), 35));
		line04.append(pad(expense.getPayee().getCity().toString(), 35));
		
		line04.append(pad(21));
		return line04.toString();
	}
	
	protected String formatLine05(Payment payment, int index, Expense expense) {
		StringBuilder line05 = new StringBuilder();
		line05.append("05");
				
		// I or U
		line05.append("U");
		
		// purpose
		String description = expense.getDescription();		
		if (description == null || !description.contains(lineSeparator))  {
			line05.append(pad(description, 105));
		} else {
			String[] separated = description.split(Pattern.quote(lineSeparator));
			for(int i=0;i<3;i++) {
				if (separated.length > i) {
					line05.append(pad(separated[i],35));
				} else {
					line05.append(pad(35));
				}
			}
		}
		// Rules for charges
		// 0=OUR=All charges debited to the ordering party
		// 1=BEN=All charges debited to the beneficiary
		// 2=SHA=charges split
		line05.append("2");
		
		line05.append(pad(19));
		return line05.toString();
	}
}
