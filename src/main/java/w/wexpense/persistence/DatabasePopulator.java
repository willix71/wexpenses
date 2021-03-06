package w.wexpense.persistence;

import static w.wexpense.model.enums.AccountEnum.ASSET;
import static w.wexpense.model.enums.AccountEnum.EXPENSE;

import java.math.BigDecimal;
import java.util.Date;
import java.util.GregorianCalendar;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.transaction.annotation.Transactional;

import w.wexpense.dta.BvoDtaFormater;
import w.wexpense.dta.BvrDtaFormater;
import w.wexpense.model.Account;
import w.wexpense.model.City;
import w.wexpense.model.Country;
import w.wexpense.model.Currency;
import w.wexpense.model.ExchangeRate;
import w.wexpense.model.Expense;
import w.wexpense.model.ExpenseType;
import w.wexpense.model.Payee;
import w.wexpense.model.PayeeType;
import w.wexpense.model.Payment;
import w.wexpense.model.TransactionLine;
import w.wexpense.model.enums.TransactionLineEnum;

public class DatabasePopulator {
	private static final Logger LOGGER = LoggerFactory.getLogger(DatabasePopulator.class);

	@PersistenceContext
	private EntityManager em;
	
	@Transactional
	public void populate() {
		long currencySize = (Long) em.createQuery("SELECT COUNT(p) FROM Currency p").getSingleResult();
		long countrySize = (Long) em.createQuery("SELECT COUNT(p) FROM Country p").getSingleResult();
		
		LOGGER.info("Number of currencies {} and countries {}", currencySize, countrySize);
		
		if (currencySize == 0 && countrySize ==0) {
			// create test data
			LOGGER.warn("Creating test data");

			Currency chf = save(new Currency("CHF", "Swiss Francs", 20));
			Currency euro = save(new Currency("EUR", "Euro", 100));
			Currency usd = save(new Currency("USD", "US Dollar", 100));
			Currency gbp = save(new Currency("GBP", "British Pounds", 100));
			
			Country ch = save(new Country("CH", "Switzerland", chf));
			Country f = save(new Country("FR", "France", euro));
			save(new Country("IT", "Italie", euro));
			save(new Country("DE", "Germany", euro));
			save(new Country("US", "United States of America", usd));
						
			City paris = save(new City(null, "Paris", f));
			City nyon = save(new City("1260", "Nyon", ch));
			save(new City("1197", "Prangins", ch));
			
			Account assetAcc = save(new Account(null, 1, "asset", ASSET, null));			
			Account cashAcc = save(new Account(assetAcc, 1, "cash", ASSET, chf));			
			Account ecAcc = save(new Account(assetAcc, 2, "courant", ASSET, chf));
			ecAcc.setExternalReference("CH650022822851333340B");
			save(new Account(assetAcc, 3, "epargne", ASSET, chf));
			
			Account vehicleAcc = save(new Account(null, 4, "vehicle", EXPENSE, null));
			Account gasAcc = save(new Account(vehicleAcc, 1, "gas", EXPENSE, chf));			
			save(new Account(vehicleAcc, 2, "tax", EXPENSE, chf));
			save(new Account(vehicleAcc, 3, "insurance", EXPENSE, chf));
			
			Account phones = save(new Account(null, 5, "phones", EXPENSE, null));
			Account natel = save(new Account(phones, 1, "natel", EXPENSE, chf));			
			
			PayeeType stationService = save(new PayeeType("Station Service"));			
			PayeeType garage = save(new PayeeType("garage"));			
			
			Payee garageDeLEtraz = new Payee();
			garageDeLEtraz.setType(garage);
			garageDeLEtraz.setPrefix("Garage de l'");
			garageDeLEtraz.setName("Etraz");
			garageDeLEtraz.setCity(nyon);
			garageDeLEtraz = save(garageDeLEtraz);
						
			Payee garageDeParis = new Payee();
			garageDeParis.setType(stationService);
			garageDeParis.setName("BP");
			garageDeParis.setCity(paris);
			garageDeParis = save(garageDeParis);
						
			Payee orange = new Payee();
			orange.setName("Orange");			
			orange.setAddress1("12 rue de la Morache");
			orange.setCity(nyon);
			orange.setExternalReference("10-2020-1");
			orange = save(orange);
			
			ExpenseType recu = save(new ExpenseType("recu"));
			ExpenseType bvr = save(new ExpenseType("BVR"));
			bvr.setPaymentGeneratorClassName(BvrDtaFormater.class.getName());
			ExpenseType bvo = save(new ExpenseType("BVO"));
			bvo.setPaymentGeneratorClassName(BvoDtaFormater.class.getName());
			
			// === Expense 1 ===
			BigDecimal amount = new BigDecimal("22.50");
			Expense expense = new Expense();
			expense.setAmount(amount);
			expense.setCurrency(chf);
			expense.setDate(new GregorianCalendar().getTime());
			expense.setPayee(garageDeLEtraz);
			expense.setType(recu);
			save(expense);
			
			TransactionLine line = new TransactionLine();
			line.setExpense(expense);
			line.setAccount(cashAcc);
			line.setFactor(TransactionLineEnum.OUT);
			line.setAmount(amount);
			line.updateValue();
			save(line);
			
			line = new TransactionLine();
			line.setExpense(expense);
			line.setAccount(gasAcc);
			line.setFactor(TransactionLineEnum.IN);
			line.setAmount(amount);
			line.updateValue();
			save(line);
			
			// === Exchange rates ===
			ExchangeRate rate = new ExchangeRate();
			rate.setDate(new GregorianCalendar(2000,1,1).getTime());
			rate.setBuyCurrency(chf);
			rate.setSellCurrency(euro);
			rate.setRate(1.6);
			rate = save(rate);
			
			rate = new ExchangeRate();
			rate.setDate(new Date());
			rate.setBuyCurrency(chf);
			rate.setSellCurrency(gbp);
			rate.setRate(1.48315);
			rate = save(rate);
			
			Date d = new GregorianCalendar(2012,6,23).getTime();
			
			rate = new ExchangeRate();
			rate.setDate(d);
			rate.setBuyCurrency(chf);
			rate.setSellCurrency(euro);
			rate.setRate(1.2);
			rate = save(rate);
			
			// === Expense 2 ===
			amount = new BigDecimal("40");
			expense = new Expense();
			expense.setAmount(amount);
			expense.setCurrency(euro);
			expense.setDate(d);
			expense.setPayee(garageDeParis);
			expense.setType(recu);
			save(expense);
			
			line = new TransactionLine();
			line.setExpense(expense);
			line.setAccount(ecAcc);
			line.setAmount(amount);
			line.setExchangeRate(rate);
			line.updateValue();
			line.setFactor(TransactionLineEnum.OUT);
			save(line);
			
			line = new TransactionLine();
			line.setExpense(expense);
			line.setAccount(gasAcc);
			line.setAmount(amount);
			line.setExchangeRate(rate);
			line.updateValue();
			line.setFactor(TransactionLineEnum.IN);
			save(line);
			
			// === Expense 3 ===
			Payment payment = new Payment();
			payment.setDate(new Date());
			payment.setFilename("TestDta.dta");
			payment = save(payment);
			
			amount = new BigDecimal("123.5");
			expense = new Expense();
			expense.setAmount(amount);
			expense.setCurrency(chf);
			expense.setDate(new GregorianCalendar(2013,3,14).getTime());
			expense.setPayee(orange);
			expense.setType(bvr);
			expense.setPayment(payment);
			expense.setExternalReference("00 00000 00123 45678 99999");
			expense.setDescription("This is a test;Dee doo doo doo");
			save(expense);
			
			line = new TransactionLine();
			line.setExpense(expense);
			line.setAccount(ecAcc);
			line.setAmount(amount);
			line.setExchangeRate(rate);
			line.updateValue();
			line.setFactor(TransactionLineEnum.OUT);
			save(line);
			
			line = new TransactionLine();
			line.setExpense(expense);
			line.setAccount(natel);
			line.setAmount(amount);
			line.updateValue();
			line.setFactor(TransactionLineEnum.IN);
			save(line);
			
			em.flush();
		}
	}
	
	private <T> T save(T t) {
		em.persist(t);
		return t;
	}
	
	private static final Logger logger = LoggerFactory.getLogger(DatabasePopulator.class);

	public static void main(String[] args) throws Exception {
		logger.info("Application started by {} at {} ",
				System.getProperty("user.name"), new Date());

		if (args.length ==0) {
			args = new String[]{"database-populator-context.xml"};
		}
		
		// Start the Spring container
		((DatabasePopulator) new ClassPathXmlApplicationContext(args).getBean(DatabasePopulator.class)).populate();
	}
}
