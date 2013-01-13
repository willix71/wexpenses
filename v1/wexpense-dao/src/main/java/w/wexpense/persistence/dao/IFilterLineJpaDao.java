package w.wexpense.persistence.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import w.wexpense.model.FilterLine;
import w.wexpense.model.TransactionLine;

public interface IFilterLineJpaDao extends JpaRepository< FilterLine, String >, JpaSpecificationExecutor< FilterLine > {

	List<FilterLine> findByTransactionLine(final TransactionLine transactionLine);
}
