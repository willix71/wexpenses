package w.wexpense.persistence.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import w.wexpense.model.Expense;
import w.wexpense.model.TransactionLine;

public interface ITransactionLineJpaDao extends JpaRepository< TransactionLine, String >, JpaSpecificationExecutor< TransactionLine > {

	List<TransactionLine> findByExpenseOrderByFactorAscAmountDesc(final Expense expense);

}
