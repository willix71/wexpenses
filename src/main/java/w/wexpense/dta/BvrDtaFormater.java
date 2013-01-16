package w.wexpense.dta;

import static w.wexpense.dta.DtaHelper.*;
import static w.wexpense.model.enums.TransactionLineEnum.IN;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

import w.wexpense.model.Expense;
import w.wexpense.model.Payment;

public class BvrDtaFormater extends AbstractDtaFormater {

	public static final String TRANSACTION_TYPE = "827";

	public BvrDtaFormater() {
		super(TRANSACTION_TYPE);
	}
		
	@Override
	public List<String> format(Payment payment, int index, Expense expense) {
		List<String> lines = new ArrayList<String>();
		lines.add(formatLine01(payment, index, expense));
		lines.add(formatLine02(payment, index, expense));
		lines.add(formatLine03(payment, index, expense));
		lines.add(formatLine04(payment, index, expense));
		return lines;
	}
	
	protected String formatLine02(Payment payment, int index, Expense expense) {
		StringBuilder line02 = new StringBuilder();
		line02.append("02");
		line02.append(pad("William Keyser",24));
		line02.append(pad("11 ch du Grand Noyer",24));
		line02.append(pad(24));
		line02.append(pad("1197 Prangins",24));
		line02.append(pad(getTransactionLine(IN, expense).getAccount().getFullName(),30));
		return line02.toString();
	}
	
	protected String formatLine03(Payment payment, int index, Expense expense) {
		StringBuilder line03 = new StringBuilder();
		line03.append("03");
		
		// beneficiary
		line03.append("/C/");
		
		// ISR party number
		line03.append(pad(expense.getPayee().getExternalReference(),27));
		
		line03.append(pad(expense.getPayee().getPrefix() + expense.getPayee().getName(), 24));
		line03.append(pad(expense.getPayee().getPostalBox(), 24));
		line03.append(pad(expense.getPayee().getLocation(), 24));
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
