package w.wexpense.persistence.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import w.wexpense.model.Expense;
import w.wexpense.model.ExpenseType;

public interface IExpenseJpaDao extends JpaRepository< Expense, Long >, JpaSpecificationExecutor< Expense > {
	ExpenseType findByUid(String uid);
}
