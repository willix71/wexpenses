package w.wexpense.persistence.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import w.wexpense.model.Account;
import w.wexpense.model.ExpenseType;

public interface IAccountJpaDao extends JpaRepository< Account, Long >, JpaSpecificationExecutor< ExpenseType >{

	Account findByUid(String uid);
	
	Account findByName(String name);
}
