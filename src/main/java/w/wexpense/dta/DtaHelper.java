package w.wexpense.dta;

import java.text.DecimalFormat;
import java.text.MessageFormat;
import java.util.Date;

import w.wexpense.model.Expense;
import w.wexpense.model.Payment;
import w.wexpense.model.TransactionLine;
import w.wexpense.model.enums.TransactionLineEnum;
import static w.wexpense.model.enums.TransactionLineEnum.OUT;

public class DtaHelper {

	public static final String APPLICATION_ID = "WEX01";
	
	private static final String[] BLANK = new String[128];
	static {
		String b = "";
		for(int i=0;i<BLANK.length;i++) {
			BLANK[i]=b;
			b += " ";
		}
	}
	
	public static String pad(int size) {
		return BLANK[size];
	}

		
	public static String pad(String text, int size) {
		if (text==null) {
			return BLANK[size];		
		} 
		int l = text.length();
		if (l == size) {
			return text;
		} else if (l > size) {
			return text.substring(0, size);
		} else {
			return text + BLANK[size-text.length()];
		}
	}

	public static String pad(Date date) {
		return MessageFormat.format("{0, date,yyMMdd}",date);
	}
	
	// size is maximum 9
	public static String zeroPad(int i, int size) {
		int v = i % 1000000000;
		String s = new DecimalFormat("000000000").format(i);		
		return s.length()>size?s.substring(s.length()-size,s.length()):s;
	}
	
	public static TransactionLine getTransactionLine(TransactionLineEnum factor, Expense expense) {
		for(TransactionLine l: expense.getTransactions()) {
			if (l.getFactor()==factor) return l;
		}
		return null;
	}
	
	public static String getHeader(String type, Payment payment, int index, Expense expense) {
		StringBuilder sb = new StringBuilder();
		
		// processing date
		sb.append(pad(expense.getDate()));
		
		// clearing of the beneficiary's bank
		sb.append(pad(12));
		
		// Bank reserved sequence
		sb.append("00000");
		
		// creation date
		sb.append(pad(payment.getDate()));
		
		// clearing of the beneficiary's bank
		String iban = getTransactionLine(OUT, expense).getAccount().getExternalReference();
		String clearing = iban.substring(6, 9);
		sb.append(pad(clearing,7));
		
		// sender id
		sb.append(pad(APPLICATION_ID,5));
		
		// sequence number
		sb.append(zeroPad(index, 5));
		
		// transaction type
		sb.append(pad(type, 3));
		
		// is salary and processing flag
		sb.append("00");
		
		return sb.toString();
	}
}
