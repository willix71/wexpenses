package w.wexpense.persistence.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import w.wexpense.model.Currency;
import w.wexpense.model.ExchangeRate;
import w.wexpense.model.Expense;

public interface IExchangeRateJpaDao extends JpaRepository< ExchangeRate, String >, JpaSpecificationExecutor< ExchangeRate > {

	List<ExchangeRate> findBySellCurrencyAndSellCurrency(final Currency sell, final Currency buy);

	@Query("select r from ExchangeRate r where r in (select tl.exchangeRate from TransactionLine tl where tl.expense = ?)")
	List<ExchangeRate> findByExpense(final Expense expense);

}
