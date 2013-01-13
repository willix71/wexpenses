package w.wexpense.persistence.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import w.wexpense.model.Consolidation;
import w.wexpense.model.Payee;

public interface IConsolidationJpaDao extends JpaRepository< Consolidation, String >, JpaSpecificationExecutor< Consolidation > {

	List<Consolidation> findByInstitution( final Payee payee );
}
