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
import w.wexpense.model.PayeeType;
import w.wexpense.model.Payment;
import w.wexpense.model.TransactionLine;
import w.wexpense.model.enums.TransactionLineEnum;

public class BvrDtaFormaterTest {

	@Test
	public void testBvr() {
		Payment payment = getPaymentData();
		List<String> l = new BvrDtaFormater().format(payment, 1, payment.getExpenses().get(0));
		Assert.assertEquals(4, l.size());
		System.out.println(l.get(0) + "]]");
		System.out.println(l.get(1) + "]]");
		System.out.println(l.get(2) + "]]");
		System.out.println(l.get(3) + "]]");
		Assert.assertEquals(128, l.get(0).length());
		Assert.assertEquals(128, l.get(1).length());		
		Assert.assertEquals(128, l.get(2).length());
		Assert.assertEquals(128, l.get(3).length());
	}
	
	public Payment getPaymentData() {
		Currency chf = new Currency("CHF", "Swiss Francs", 20);		
		Country ch = new Country("CH", "Switzerland", chf);
		City nyon = new City("1260", "Nyon", ch);

		
		Account assetAcc = new Account(null, 1, "asset", ASSET, null);						
		Account ecAcc = new Account(assetAcc, 2, "courant", ASSET, chf);
		ecAcc.setExternalReference("CH650022822851333340B");
		
		Account vehicleAcc = new Account(null, 4, "vehicle", EXPENSE, null);
		Account gasAcc = new Account(vehicleAcc, 1, "gas", EXPENSE, chf);			
		
		PayeeType garage = new PayeeType("garage");					
		Payee garageDeLEtraz = new Payee();
		garageDeLEtraz.setType(garage);
		garageDeLEtraz.setPrefix("Garage de l'");
		garageDeLEtraz.setName("Etraz");
		garageDeLEtraz.setCity(nyon);
		garageDeLEtraz.setExternalReference("17-128583-4");
		
		// === Payment ===
		Payment payment = new Payment();
		payment.setDate(new GregorianCalendar(2013,01,15).getTime());
		payment.setFilename("test.dta");
		
		// === Expense 1 ===
		BigDecimal amount = new BigDecimal("22.50");
		Expense expense = new Expense();
		expense.setId(1234567890L);		
		expense.setAmount(amount);
		expense.setCurrency(chf);
		expense.setDate(new GregorianCalendar(2013,02,01).getTime());
		expense.setPayee(garageDeLEtraz);
		expense.setPayment(payment);
		expense.setDescription("This is a payment;with a new line");
		
		TransactionLine line1 = new TransactionLine();
		line1.setExpense(expense);
		line1.setAccount(ecAcc);
		line1.setFactor(TransactionLineEnum.OUT);
		line1.setAmount(amount);
		line1.setValue(amount.doubleValue());
		
		TransactionLine line2 = new TransactionLine();
		line2.setExpense(expense);
		line2.setAccount(gasAcc);
		line2.setFactor(TransactionLineEnum.IN);
		line2.setAmount(amount);
		line2.setValue(amount.doubleValue());;
		
		expense.setTransactions(Arrays.asList(line1, line2));
		payment.setExpenses(Arrays.asList(expense));
		
		return payment;
	}
}
