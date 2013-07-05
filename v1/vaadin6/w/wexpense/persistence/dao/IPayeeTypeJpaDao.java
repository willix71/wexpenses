package w.wexpense.persistence.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import w.wexpense.model.PayeeType;

public interface IPayeeTypeJpaDao extends JpaRepository< PayeeType, Long >, JpaSpecificationExecutor< PayeeType > {
	PayeeType findByUid(String uid);
	PayeeType findByName(String name);
}
