package w.wexpense.persistence.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import w.wexpense.model.PayeeType;

public interface IPayeeTypeJpaDao extends JpaRepository< PayeeType, String >, JpaSpecificationExecutor< PayeeType >{

	PayeeType findByName(String name);
	
	List<PayeeType> findByNameLike(String name);
}
