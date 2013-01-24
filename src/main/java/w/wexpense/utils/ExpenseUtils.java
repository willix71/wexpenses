package w.wexpense.utils;

import java.text.MessageFormat;

import w.wexpense.model.Expense;
import w.wexpense.model.TransactionLine;

public class ExpenseUtils {

	public static String toString(Expense expense) {
		StringBuilder sb = new StringBuilder();
		sb.append(MessageFormat.format("\n{0,date,dd.MM.yy} {1,number,0.00}{2} {3} {4}",
				expense.getDate(), expense.getAmount(), expense.getCurrency(), expense.getPayee(), expense.getType()));
		for(TransactionLine tr: expense.getTransactions()) {
			sb.append(MessageFormat.format("\n\t{0}.{1} {2,number,0.000} {3,number,0.000}",
					tr.getAccount(), tr.getDiscriminator(), tr.getAmount(), tr.getValue()));
		}
		return sb.toString();
	}

}
