package w.wexpense.persistence.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import w.wexpense.model.Payee;

public interface IPayeeJpaDao extends JpaRepository< Payee, String >, JpaSpecificationExecutor< Payee > {

	Payee findByName( final String name );
	
	@Query("select p from Payee p where p.name like :text order by name")
	List<Payee> findByText(@Param("text") String lastname);
}
