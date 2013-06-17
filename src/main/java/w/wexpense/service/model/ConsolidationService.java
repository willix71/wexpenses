package w.wexpense.service.model;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import w.wexpense.model.TransactionLine;
import w.wexpense.model.Consolidation;
import w.wexpense.persistence.dao.ITransactionLineJpaDao;
import w.wexpense.persistence.dao.IConsolidationJpaDao;
import w.wexpense.service.DaoService;

@Service
public class ConsolidationService extends DaoService<Consolidation, Long> {

	@Autowired
	private ITransactionLineJpaDao expenseDao;
	
	@Autowired
	public ConsolidationService(IConsolidationJpaDao dao) {
	   super(Consolidation.class, dao);
   }
	
	@Override
   public Consolidation save(Consolidation entity) {
	   LOGGER.debug("Saving consolidation's transactions {}", entity.getTransactions());
	   
	   List<TransactionLine> newTransactionLines = entity.getTransactions();
	   
	   Consolidation newConsolidation = super.save(entity);
	   
	   List<TransactionLine> oldTransactionLines = expenseDao.findByConsolidation(newConsolidation);	   
	   LOGGER.debug("old payment's expenses size{}", oldTransactionLines.size());
	   
	   for(TransactionLine newTransactionLine: newTransactionLines) {
	   	newTransactionLine.setConsolidation(newConsolidation);
	   	expenseDao.save(newTransactionLine);
	   }
	   for(TransactionLine oldTransactionLine: oldTransactionLines) {
	   	if (!newTransactionLines.contains(oldTransactionLine)) {
	   		oldTransactionLine.setConsolidation(null);
		   	expenseDao.save(oldTransactionLine);
	   	}
	   }
	   return newConsolidation;
   }

}
