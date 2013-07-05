package w.wexpense.persistence.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import w.wexpense.model.ExpenseType;

public interface IExpenseTypeJpaDao extends JpaRepository< ExpenseType, Long >, JpaSpecificationExecutor< ExpenseType > {
	ExpenseType findByUid(String uid);
	
	ExpenseType findByName(String name);
}
