package w.wexpense.persistence.dao;

import java.util.Collection;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import w.wexpense.model.Expense;
import w.wexpense.model.Payee;
import w.wexpense.model.Payment;

public interface IExpenseJpaDao extends JpaRepository< Expense, Long >, JpaSpecificationExecutor< Expense >, IDBableJpaDao<Expense> {
	List<Expense> findByPayee(Payee p);

	List<Expense> findByPayment(Payment p);
	
	@Query(value="from Expense x where x.payment=?1 and x.uid not in (?2)")
	List<Expense> findNotInPayment(Payment p, Collection<String> uids);
}