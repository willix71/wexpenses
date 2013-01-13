package w.wexpense.persistence;

import static w.wexpense.model.enums.AccountEnum.ASSET;
import static w.wexpense.model.enums.AccountEnum.EXPENSE;

import java.util.Date;
import java.util.GregorianCalendar;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.transaction.annotation.Transactional;

import w.wexpense.model.Account;
import w.wexpense.model.City;
import w.wexpense.model.Country;
import w.wexpense.model.Currency;
import w.wexpense.model.ExchangeRate;
import w.wexpense.model.Expense;
import w.wexpense.model.ExpenseType;
import w.wexpense.model.Payee;
import w.wexpense.model.PayeeType;
import w.wexpense.model.TransactionLine;
import w.wexpense.model.enums.TransactionLineEnum;

public class DatabasePopulator {
	private static final Logger LOGGER = LoggerFactory.getLogger(DatabasePopulator.class);
	
//	@Autowired
//	private WexJPAContainerFactory jpaContainerFactory;
	
	@PersistenceContext
	private EntityManager em;
	
	@Transactional
	public void populate() {
		//em = jpaContainerFactory.getEntityManager();
		
		long size = (Long) em.createQuery("SELECT COUNT(p) FROM Currency p").getSingleResult();
		
		LOGGER.info("Number of currencies {}", size);
		
		if (size == 0) {
			// create test data
	
			//em.getTransaction().begin();
	
			Currency chf = save(new Currency("CHF", "Swiss Francs", 20));
			Currency euro = save(new Currency("EUR", "Euro", 100));
			Currency usd = save(new Currency("USD", "US Dollar", 100));
			Currency gbp = save(new Currency("GBP", "British Pounds", 100));
			
			Country ch = save(new Country("CH", "Switzerland", chf));

			Country f = save(new Country("FR", "France", euro));
			save(new Country("IT", "Italie", euro));
			save(new Country("DE", "Germany", euro));
						
			City paris = save(new City(null, "Paris", f));
			City nyon = save(new City("1260", "Nyon", ch));
			save(new City("1197", "Prangins", ch));
			
			Account assetAcc = save(new Account(null, 1, "asset", ASSET, null));			
			Account cashAcc = save(new Account(assetAcc, 1, "cash", ASSET, chf));			
			Account ecAcc = save(new Account(assetAcc, 2, "courant", ASSET, chf));
			save(new Account(assetAcc, 3, "epargne", ASSET, chf));
			
			Account vehicleAcc = save(new Account(null, 4, "vehicle", EXPENSE, null));
			Account gasAcc = save(new Account(vehicleAcc, 1, "gas", EXPENSE, chf));			
			save(new Account(vehicleAcc, 2, "tax", EXPENSE, chf));
			save(new Account(vehicleAcc, 3, "insurance", EXPENSE, chf));
			
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
						
			ExpenseType recu = save(new ExpenseType("recu"));
			save(new ExpenseType("BVR"));
			save(new ExpenseType("BVO"));
			
			// === Expense 1 ===
			Expense expense = new Expense();
			expense.setAmount(22.50);
			expense.setCurrency(chf);
			expense.setDate(new GregorianCalendar().getTime());
			expense.setPayee(garageDeLEtraz);
			expense.setType(recu);
			save(expense);
			
			TransactionLine line = new TransactionLine();
			line.setExpense(expense);
			line.setAccount(cashAcc);
			line.setFactor(TransactionLineEnum.OUT);
			line.setAmount(22.50);
			line.setValue(22.50);
			line.setPeriod(2012);
			save(line);
			
			line = new TransactionLine();
			line.setExpense(expense);
			line.setAccount(gasAcc);
			line.setFactor(TransactionLineEnum.IN);
			line.setAmount(22.50);
			line.setValue(22.50);
			save(line);
			
			// === Expense 2 ===
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
			
			expense = new Expense();
			expense.setAmount(40.0);
			expense.setCurrency(euro);
			expense.setDefaultExchangeRate(rate);
			expense.setDate(d);
			expense.setPayee(garageDeParis);
			expense.setType(recu);
			save(expense);
			
			line = new TransactionLine();
			line.setExpense(expense);
			line.setAccount(ecAcc);
			line.setAmount(40.0);
			line.setExchangeRate(rate);
			line.setValue(40 * 1.6);
			line.setValue(40 * 1.6 * rate.getRate());
			line.setFactor(TransactionLineEnum.OUT);
			line.setPeriod(2012);
			save(line);
			
			line = new TransactionLine();
			line.setExpense(expense);
			line.setAccount(gasAcc);
			line.setAmount(40.0);
			line.setExchangeRate(rate);
			line.setValue(40 * 1.6);
			line.setValue(40 * 1.6 * rate.getRate());
			line.setFactor(TransactionLineEnum.IN);
			save(line);
			
			em.flush();
			//em.getTransaction().commit();
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
