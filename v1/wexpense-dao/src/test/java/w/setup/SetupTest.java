package w.setup;

import java.util.List;

import javax.annotation.Resource;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.Order;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;

import w.junit.extras.OrderedSpringJUnit4ClassRunner;
import w.wexpense.model.City;
import w.wexpense.model.Country;
import w.wexpense.model.Currency;
import w.wexpense.model.MoneyAccount;
import w.wexpense.model.TransactionLine;
import w.wexpense.model.enums.AccountEnum;
import w.wexpense.persistence.dao.ICityJpaDao;
import w.wexpense.persistence.dao.ICountryJpaDao;
import w.wexpense.persistence.dao.ICurrencyJpaDao;
import w.wexpense.persistence.dao.IMoneyAccountJpaDao;
import w.wexpense.persistence.dao.ITransactionLineJpaDao;

@RunWith( OrderedSpringJUnit4ClassRunner.class )
@ContextConfiguration( locations ={"classpath:persistence-test-context.xml"})

//Use the following to configure without XML files
//@ContextConfiguration( classes = { PersistenceJPAConfig.class },loader = AnnotationConfigContextLoader.class )

@TransactionConfiguration( defaultRollback = false )
@Transactional
public class SetupTest {

	protected final Logger logger = LoggerFactory.getLogger( this.getClass() );

	@Resource
	private ICurrencyJpaDao currencyDao;

	@Resource
	private ICountryJpaDao countryDao;
	
	@Resource
	private ICityJpaDao cityDao;
	
	@Resource
	private IMoneyAccountJpaDao accountDao;
	
	@Resource
	private ITransactionLineJpaDao transactionLineDao;
	
	@Before
	public void before() {
		logger.info("\n\t========================================");
	}
	
	@Test
	@Order(1)
	public void testSetup() {
		logger.info("slf4j");
		
		Assert.assertTrue(true);
	}
	
	@Test
	@Order(2)
	public void testCurrencySetup() {
		
		Assert.assertNotNull(currencyDao);
		Assert.assertEquals(4,currencyDao.count());
		
		Currency c = currencyDao.findOne("CHF");
		Assert.assertNotNull(c);
		Assert.assertEquals("Swiss Francs", c.getName());
	}
	
	@Test
	@Order(3)
	public void testCountrySetup() {
		
		Assert.assertNotNull(countryDao);
		Assert.assertEquals(6,countryDao.count());
		
		Country c = countryDao.findOne("UK");
		Assert.assertNotNull(c);
		Assert.assertEquals("United Kingdom", c.getName());
		
		Assert.assertEquals("GBP", c.getCurrency().getCode());
		
		Country newc = new Country();
		newc.setCode("tt");
		newc.setName("test");
		newc.setCurrency(currencyDao.findOne("EUR"));
		
		countryDao.save(newc);
	}
	
	@Test
	@Order(4)
	public void testNewCountry() {
		
		Assert.assertEquals(7,countryDao.count());
	}
	
	@Test
	@Order(5)
	public void testCity() {
		
		Assert.assertNotNull(cityDao);
		Assert.assertEquals(5,cityDao.count());
		
		List<City> cities = cityDao.findByDisplay("Prang%");
		Assert.assertNotNull(cities);
		Assert.assertEquals(1,cities.size());
		
		City prangins = cities.get(0);
		Assert.assertEquals("1197", prangins.getZip());
		Assert.assertNotNull(prangins.getCountry());
		Assert.assertNotNull("CH", prangins.getCountry().getCode());
		
      City newYork = new City();
      newYork.setName("NewYork");
      newYork.setZip("909");
      newYork.setCountry(countryDao.findOne("US"));     
      Assert.assertNull(newYork.getDisplay());
      
      City newYork2 = cityDao.save(newYork);
      Assert.assertEquals("909 newyork (us)", newYork2.getDisplay());
      
      // change the name
      newYork2.setName("New York");
	}
	
	@Test
	@Order(6)
	public void testNewCity() {
		List<City> cities = cityDao.findByDisplay("%New York%");
		Assert.assertNotNull(cities);
		Assert.assertEquals(1,cities.size());
		
		City newYork = cities.get(0);
		Assert.assertEquals("909 new york (us)", newYork.getDisplay());
	}
	
	@Test
	@Order(7)
	public void testAccounts() {
		
		Assert.assertNotNull(accountDao);
		Assert.assertEquals(4,accountDao.count());
		
		MoneyAccount cash = accountDao.findByFullNumber("39");
		Assert.assertEquals("unknown", cash.getName());
		Assert.assertEquals(AccountEnum.EXPENSE, cash.getType());

	}
	
	@Test
	@Order(8)
	public void testTransactionLine() {
		
		Assert.assertNotNull(transactionLineDao);
		Assert.assertEquals(2,transactionLineDao.count());
		
		double balance = 0;
		for(TransactionLine tl: transactionLineDao.findAll()) {
			Assert.assertEquals(20.0, tl.getValue(),0.0);
			balance += tl.getValue() * tl.getFactor().getFactor();
		}
		Assert.assertEquals(0.0, balance, 0.0);
	}
}
