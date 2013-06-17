package w.wexpense.service.model;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import w.wexpense.model.Account;
import w.wexpense.model.City;
import w.wexpense.model.Consolidation;
import w.wexpense.model.Country;
import w.wexpense.model.Currency;
import w.wexpense.model.Discriminator;
import w.wexpense.model.ExchangeRate;
import w.wexpense.model.ExpenseType;
import w.wexpense.model.Payee;
import w.wexpense.model.PayeeType;
import w.wexpense.model.TransactionLine;
import w.wexpense.persistence.dao.ICityJpaDao;
import w.wexpense.persistence.dao.ICountryJpaDao;
import w.wexpense.persistence.dao.ICurrencyJpaDao;
import w.wexpense.persistence.dao.IExchangeRateJpaDao;
import w.wexpense.service.DaoService;
import w.wexpense.service.GenericService;
import w.wexpense.service.StorableService;
import w.wexpense.service.instanciator.NameInitializor;
import w.wexpense.service.instanciator.ParentInitializor;

@Configuration
public class StorableServiceConfiguration {

	@Bean 
	public StorableService<Currency, String> currencyService(ICurrencyJpaDao dao) {
		return new DaoService<Currency, String>(Currency.class, dao);
	}
	
	@Bean 
	public StorableService<Country, String> countryService(ICountryJpaDao dao) {
		return new DaoService<Country, String>(Country.class, dao);
	}

	@Bean 
	public StorableService<City, Long> cityService(ICityJpaDao dao) {
		return new DaoService<City, Long>(City.class, dao, new NameInitializor<City>(City.class));
	}
	
	@Bean 
	public StorableService<PayeeType, Long> payeeTypeService() {
		return new GenericService<PayeeType, Long>(PayeeType.class, new NameInitializor<PayeeType>(PayeeType.class));
	}
	
	@Bean 
	public StorableService<Payee, Long> payeeService() {
		return new GenericService<Payee, Long>(Payee.class, new NameInitializor<Payee>(Payee.class));
	}
		
	@Bean 
	public StorableService<Account, Long> accountService() {
		return new GenericService<Account, Long>(Account.class, new ParentInitializor<Account>(Account.class), new NameInitializor<Account>(Account.class));
	}
	
	@Bean 
	public StorableService<Discriminator, Long> discriminatorService() {
		return new GenericService<Discriminator, Long>(Discriminator.class, new ParentInitializor<Discriminator>(Discriminator.class), new NameInitializor<Discriminator>(Discriminator.class));
	}
	
	@Bean 
	public StorableService<ExchangeRate, Long> exchangeRateService(IExchangeRateJpaDao dao) {
		return new DaoService<ExchangeRate, Long>(ExchangeRate.class, dao);
	}
	
	@Bean 
	public StorableService<ExpenseType, Long> expenseTypeService() {
		return new GenericService<ExpenseType, Long>(ExpenseType.class, new NameInitializor<ExpenseType>(ExpenseType.class));
	}
	
	@Bean 
	public StorableService<TransactionLine, Long> transactionLineService() {
		return new GenericService<TransactionLine, Long>(TransactionLine.class);
	}

	@Bean 
	public StorableService<Consolidation, Long> consolidationService() {
		return new GenericService<Consolidation, Long>(Consolidation.class);
	}	
	
}
