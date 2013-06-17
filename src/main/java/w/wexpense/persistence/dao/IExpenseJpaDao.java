package w.wexpense.persistence.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import w.wexpense.model.Expense;
import w.wexpense.model.Payment;

public interface IExpenseJpaDao extends JpaRepository< Expense, Long >, JpaSpecificationExecutor< Expense >, IDBableJpaDao<Expense> {
	List<Expense> findByPayment(Payment p);
}
