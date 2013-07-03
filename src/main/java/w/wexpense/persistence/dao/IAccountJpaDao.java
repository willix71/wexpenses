package w.wexpense.persistence.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import w.wexpense.model.Account;

public interface IAccountJpaDao extends JpaRepository< Account, Long >, JpaSpecificationExecutor< Account >, IDBableJpaDao<Account>  {

	@Query("from Account a where a.parent is null")
	List<Account> findParents();
}
