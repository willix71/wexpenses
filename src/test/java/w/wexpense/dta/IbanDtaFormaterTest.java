package w.wexpense.dta;

import static w.wexpense.model.enums.AccountEnum.ASSET;
import static w.wexpense.model.enums.AccountEnum.EXPENSE;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.GregorianCalendar;
import java.util.List;

import junit.framework.Assert;

import org.junit.Test;

import w.wexpense.model.Account;
import w.wexpense.model.City;
import w.wexpense.model.Country;
import w.wexpense.model.Currency;
import w.wexpense.model.Expense;
import w.wexpense.model.Payee;
import w.wexpense.model.Payment;
import w.wexpense.model.TransactionLine;
import w.wexpense.model.enums.TransactionLineEnum;

public class IbanDtaFormaterTest {

	String[] expected = {
			"01000000            00000121218228    WEX010000583600WEX01ID234567890CH650022822851333340B   121218CHF150,00                    ",
			"02            WILLIAM KEYSER                     11 CH DU GRAND NOYER               1197 PRANGINS (CH)                 SPORTS:FO",
			"03DUBS SA                             1196 GLAND (CH)                    CH2500243243G76735570                                  ",
			"04F.C. BURSINS 'VETERANS'                                               1183 BURSINS (CH)                                       ",
			"05UWILLIAM KEYSER SAISON 2012-2013                                                                          2                   "
	};
	
	@Test
	public void testBvr() {
		Payment payment = getPaymentData();
		List<String> l = new IbanDtaFormater().format(payment, 5, payment.getExpenses().get(0));
		Assert.assertEquals(5, l.size());
		System.out.println(l.get(0) + "]]");
		System.out.println(l.get(1) + "]]");
		System.out.println(l.get(2) + "]]");
		System.out.println(l.get(3) + "]]");
		System.out.println(l.get(4) + "]]");
		Assert.assertEquals("line 1's length is not 128",128, l.get(0).length());
		Assert.assertEquals("line 2's length is not 128",128, l.get(1).length());		
		Assert.assertEquals("line 3's length is not 128",128, l.get(2).length());
		Assert.assertEquals("line 4's length is not 128",128, l.get(3).length());
		Assert.assertEquals("line 5's length is not 128",128, l.get(4).length());
		
		for(int i=0;i<5;i++) {
			Assert.assertEquals("Line "+i+" is wrong", expected[i], l.get(i).toUpperCase());
		}
	}
	
	public Payment getPaymentData() {
		Currency chf = new Currency("CHF", "Swiss Francs", 20);		
		Country ch = new Country("CH", "Switzerland", chf);
	
		Account assetAcc = new Account(null, 1, "asset", ASSET, null);						
		Account ecAcc = new Account(assetAcc, 2, "courant", ASSET, chf);
		ecAcc.setExternalReference("CH650022822851333340B");
		
		Account sportsAcc = new Account(null, 4, "sports", EXPENSE, null);
		Account foot = new Account(sportsAcc, 1, "football", EXPENSE, chf);			
			
		Payee brp = new Payee();
		brp.setName("F.C. Bursins 'Veterans'");
		brp.setCity(new City("1183", "Bursins", ch));
		brp.setExternalReference("CH25 0024 3243 G767 3557 0");
		
		Payee ubs = new Payee();
		ubs.setName("UBS SA");
		ubs.setCity(new City("1196","Gland",ch));
		ubs.setExternalReference("80-2-2");
		brp.setBankDetails(ubs);
		
		// === Payment ===
		Payment payment = new Payment();
		payment.setDate(new GregorianCalendar(2012,11,18).getTime());
		payment.setFilename("test.dta");
		
		// === Expense 1 ===
		BigDecimal amount = new BigDecimal("150");
		Expense expense = new Expense();
		expense.setId(1234567890L);		
		expense.setAmount(amount);
		expense.setCurrency(chf);
		expense.setDate(new GregorianCalendar(2012,11,18).getTime());
		expense.setPayee(brp);
		expense.setPayment(payment);
		expense.setDescription("William Keyser Saison 2012-2013");
		
		TransactionLine line1 = new TransactionLine();
		line1.setExpense(expense);
		line1.setAccount(ecAcc);
		line1.setFactor(TransactionLineEnum.OUT);
		line1.setAmount(amount);
		line1.setValue(amount.doubleValue());
		
		TransactionLine line2 = new TransactionLine();
		line2.setExpense(expense);
		line2.setAccount(foot);
		line2.setFactor(TransactionLineEnum.IN);
		line2.setAmount(amount);
		line2.setValue(amount.doubleValue());;
		
		expense.setTransactions(Arrays.asList(line1, line2));
		payment.setExpenses(Arrays.asList(expense));
		
		return payment;
	}
}
