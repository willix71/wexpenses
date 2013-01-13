package w.wexpense.persistence.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import w.wexpense.model.Expense;

public interface IExpenseJpaDao extends JpaRepository< Expense, String >, JpaSpecificationExecutor< Expense > {

}
