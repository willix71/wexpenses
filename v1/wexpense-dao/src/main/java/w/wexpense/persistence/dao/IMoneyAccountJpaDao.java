package w.wexpense.persistence.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import w.wexpense.model.MoneyAccount;
import w.wexpense.model.enums.AccountEnum;

public interface IMoneyAccountJpaDao extends JpaRepository< MoneyAccount, String >, JpaSpecificationExecutor< MoneyAccount > {

	List<MoneyAccount> findByDisplay( String name );
	
	MoneyAccount findByFullName( final String name );
	
	MoneyAccount findByFullNumber( final String number );
	
	List<MoneyAccount> findByFullNameLike( final String name );
	
	List<MoneyAccount> findByFullNumberLike( final String number );
	
	List<MoneyAccount> findByType( AccountEnum type );
	
	List<MoneyAccount> findByParent( MoneyAccount parent );

	@Query("select a from Account a, IN (a.children) b where b = ? ")
	MoneyAccount findParent( MoneyAccount child);
	
	@Query("select a from Account a where a.parent is null order by number")
	List<MoneyAccount> findTopAccounts();
}