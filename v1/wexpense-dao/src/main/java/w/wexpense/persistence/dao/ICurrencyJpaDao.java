package w.wexpense.persistence.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import w.wexpense.model.Currency;

public interface ICurrencyJpaDao extends JpaRepository< Currency, String >, JpaSpecificationExecutor< Currency> {

	List<Currency> findByCodeLike(String code);

}
