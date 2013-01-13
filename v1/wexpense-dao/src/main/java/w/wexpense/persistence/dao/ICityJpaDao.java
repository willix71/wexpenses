package w.wexpense.persistence.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import w.wexpense.model.City;

public interface ICityJpaDao extends JpaRepository< City, String >, JpaSpecificationExecutor< City> {

	@Query("select c from City c where c.display like LOWER(?1)")
	List<City> findByDisplay( String name );

}
