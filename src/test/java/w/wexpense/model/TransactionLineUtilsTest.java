package w.wexpense.model;

import static w.wexpense.model.enums.TransactionLineEnum.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import junit.framework.Assert;

import org.junit.Test;

import w.utils.DateUtils;
import w.wexpense.model.enums.TransactionLineEnum;
import w.wexpense.utils.ExpenseUtils;
import w.wexpense.utils.TransactionLineUtils;

public class TransactionLineUtilsTest {

	private TransactionLine[] initTransactionLines() {
		Account[] accounts = new Account[]{new Account()};
		
		Expense x = ExpenseUtils.newExpense(DateUtils.getDate(2013), new BigDecimal(100), new Currency("CHF","test",20), null, null, null);
		TransactionLine[] initlines = new TransactionLine[] {
				addTransactionLine(new Account(), OUT, new BigDecimal("10"), DateUtils.getDate(2013)),	// 5			
				addTransactionLine(accounts[0], IN, new BigDecimal("20"), DateUtils.getDate(2012)), 	// 2
				addTransactionLine(new Account(), IN, BigDecimal.ZERO, x), 										// 4
				addTransactionLine(accounts[0], SUM, new BigDecimal("40"), x),									// 3
				addTransactionLine(accounts[0], OUT, new BigDecimal("70"), DateUtils.getDate(2011)), 	// 1
		};
		
		return initlines;
	}
	
	@Test
	public void testBalanceSort() {
		TransactionLine[]  initlines = initTransactionLines();
		List<TransactionLine> lines = TransactionLineUtils.sortForBalance(new ArrayList<TransactionLine>(Arrays.asList(initlines)));
		Assert.assertEquals("2011", initlines[4], lines.get(0));
		Assert.assertEquals("2012", initlines[1], lines.get(1));
		Assert.assertEquals("SUM", initlines[3], lines.get(2));
		Assert.assertEquals("IN", initlines[2], lines.get(3));
		Assert.assertEquals("OUT", initlines[0], lines.get(4));
	}

	@Test
	public void testBalance() {
		TransactionLine[]  initlines = initTransactionLines();
		TransactionLineUtils.balance(new ArrayList<TransactionLine>(Arrays.asList(initlines)));
		BigDecimal balances[] = new BigDecimal[]{
				new BigDecimal("10.0"),
				new BigDecimal("50.0"),
				new BigDecimal("40.0"),
				new BigDecimal("10.0"),
				new BigDecimal("70.0"),				
		};
		for(int i=0;i<balances.length;i++) {	
			Assert.assertEquals("wrong balance for " + i, balances[i],initlines[i].getBalance());
		}
	}
	
	public static TransactionLine addTransactionLine(Account account, TransactionLineEnum factor, Object ...args) {
		TransactionLine tx = new TransactionLine();
		tx.setAccount(account);
		tx.setFactor(factor);
		for(Object o: args) {
			if (o instanceof Expense) {
				Expense x = (Expense) o;
				tx.setExpense(x);
				
				List<TransactionLine> lines = x.getTransactions();
				if (lines == null) {
					lines = new ArrayList<TransactionLine>();
					x.setTransactions(lines);			
				}
				lines.add(tx);

				if (tx.getAmount() == null) {
					tx.setAmount(x.getAmount());
					tx.setValue(x.getAmount());
				}
			}
			if (o instanceof Date) {
				tx.setDate((Date) o);
			}
			if (o instanceof Number) {
				BigDecimal d = BigDecimal.valueOf(((Number) o).doubleValue());
				tx.setAmount(d);
				tx.setValue(d);
			}

		}
		return tx;	
	}
}
