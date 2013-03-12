package w.wexpense.persistence.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import w.wexpense.model.ExpenseType;
import w.wexpense.model.Payee;

public interface IPayeeJpaDao extends JpaRepository< Payee, Long >, JpaSpecificationExecutor< ExpenseType >{
	Payee findByUid(String uid);
}
