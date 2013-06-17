package w.wexpense.persistence.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import w.wexpense.model.Consolidation;
import w.wexpense.model.Expense;
import w.wexpense.model.TransactionLine;

public interface ITransactionLineJpaDao extends JpaRepository< TransactionLine, Long >, JpaSpecificationExecutor< TransactionLine >, IDBableJpaDao<TransactionLine> {
	List<TransactionLine> findByExpense(Expense expense);
	
	List<TransactionLine> findByConsolidation(Consolidation consolidation);
}
