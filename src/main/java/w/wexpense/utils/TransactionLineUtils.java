package w.wexpense.utils;

import java.math.BigDecimal;
import java.util.Collection;

import w.wexpense.model.TransactionLine;
import w.wexpense.model.enums.TransactionLineEnum;

public class TransactionLineUtils {

	public static void updateAmount(Collection<TransactionLine> transactions, BigDecimal newAmount, BigDecimal oldAmount) {
		for(TransactionLine transaction: transactions) {
			BigDecimal amnt = transaction.getAmount();
			if (amnt==null || (oldAmount!=null && oldAmount.compareTo(amnt)==0) ) {
				transaction.setAmount(newAmount);
				transaction.updateValue();
			}
		}
	}

	public static BigDecimal[] getDeltaAndTotalOut(Collection<TransactionLine> transactions) {
		BigDecimal[] total = { new BigDecimal(0), new BigDecimal(0)};
		for(TransactionLine transaction: transactions) {
			BigDecimal amount = transaction.getAmount();
			TransactionLineEnum factor = transaction.getFactor();
			if (amount != null && factor != null) {
				if (TransactionLineEnum.OUT==factor) {					
					total[0] = total[0].subtract(amount);
					total[1] = total[1].add(amount);
				} else if (TransactionLineEnum.IN==factor) {
					total[0] = total[0].add( amount );
				}
			}
		}
		return total;
	}
	
	public static BigDecimal[] getInAndOutTotal(Collection<TransactionLine> transactions) {
		BigDecimal[] total = { new BigDecimal(0), new BigDecimal(0)};
		for(TransactionLine transaction: transactions) {
			BigDecimal amount = transaction.getAmount();
			TransactionLineEnum factor = transaction.getFactor();
			if (amount != null && factor != null) {
				if (TransactionLineEnum.IN==factor) {					
					total[0] = total[0].add(amount);
				} else if (TransactionLineEnum.OUT==factor) {
					total[1] = total[1].add( amount );
				}
			}
		}
		return total;
	}
}
