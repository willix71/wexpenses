package w.wexpense.persistence;

import static w.wexpense.model.enums.AccountEnum.ASSET;
import static w.wexpense.model.enums.AccountEnum.EXPENSE;

import java.util.Date;
import java.util.GregorianCalendar;

import javax.annotation.PostConstruct;
import javax.persistence.EntityManager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import w.wexpense.model.Account;
import w.wexpense.model.City;
import w.wexpense.model.Country;
import w.wexpense.model.Currency;
import w.wexpense.model.Expense;
import w.wexpense.model.ExpenseType;
import w.wexpense.model.Payee;
import w.wexpense.model.PayeeType;
import w.wexpense.model.TransactionLine;
import w.wexpense.model.enums.TransactionLineEnum;
import w.wexpense.vaadin.WexJPAContainerFactory;

import com.vaadin.addon.jpacontainer.JPAContainerFactory;

public class DatabasePopulator {
	
	EntityManager em;
	
	@PostConstruct
	public void populate() {
		em = JPAContainerFactory.createEntityManagerForPersistenceUnit(WexJPAContainerFactory.PERSISTENCE_UNIT);
		
		long size = (Long) em.createQuery("SELECT COUNT(p) FROM Currency p").getSingleResult();
		if (size == 0) {
			// create test data
	
			em.getTransaction().begin();
	
			Currency chf = save(new Currency("CHF", "Swiss Francs"));
			Currency euro = save(new Currency("EUR", "Euro"));
	
			Country ch = save(new Country("CH", "Switzerland", chf));

			save(new Country("FR", "France", euro));
			save(new Country("IT", "Italie", euro));
			save(new Country("DE", "Germany", euro));
						
			City nyon = save(new City("1260", "Nyon", ch));
			save(new City("1197", "Prangins", ch));
			
			Account assetAcc = save(new Account(null, 1, "asset", ASSET, null));			
			Account cashAcc = save(new Account(assetAcc, 1, "cash", ASSET, chf));			
			save(new Account(assetAcc, 2, "courant", ASSET, chf));
			save(new Account(assetAcc, 3, "epargne", ASSET, chf));
			
			Account vehicleAcc = save(new Account(null, 4, "vehicle", EXPENSE, null));
			Account gasAcc = save(new Account(vehicleAcc, 1, "gas", EXPENSE, chf));			
			save(new Account(vehicleAcc, 2, "tax", EXPENSE, chf));
			save(new Account(vehicleAcc, 3, "insurance", EXPENSE, chf));
			
			PayeeType garage = save(new PayeeType("garage"));			
			
			Payee payee = new Payee();
			payee.setType(garage);
			payee.setPrefix("Garage de l'");
			payee.setName("Etraz");
			payee.setCity(nyon);
			save(payee);
						
			ExpenseType recu = save(new ExpenseType("garage"));
			save(new ExpenseType("BVR"));
			save(new ExpenseType("BVO"));
			
			Expense expense = new Expense();
			expense.setAmount(22.50);
			expense.setCurrency(chf);
			expense.setDate(new GregorianCalendar().getTime());
			expense.setPayee(payee);
			expense.setType(recu);
			save(expense);
			
			TransactionLine line = new TransactionLine();
			line.setExpense(expense);
			line.setAccount(cashAcc);
			line.setAmount(22.50);
			line.setFactor(TransactionLineEnum.OUT);
			line.setPeriod(2012);
			save(line);
			
			line = new TransactionLine();
			line.setExpense(expense);
			line.setAccount(gasAcc);
			line.setAmount(22.50);
			line.setFactor(TransactionLineEnum.IN);
			save(line);
			
			em.getTransaction().commit();
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
		new ClassPathXmlApplicationContext(args);

	}
}
