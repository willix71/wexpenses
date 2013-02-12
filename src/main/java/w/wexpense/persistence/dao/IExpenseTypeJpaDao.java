package w.wexpense.persistence.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import w.wexpense.model.ExpenseType;

public interface IExpenseTypeJpaDao extends JpaRepository< ExpenseType, Long >, JpaSpecificationExecutor< ExpenseType > {

	ExpenseType findByName(String name);
}
