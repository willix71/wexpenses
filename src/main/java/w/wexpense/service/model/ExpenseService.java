package w.wexpense.service.model;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import w.wexpense.model.Expense;
import w.wexpense.persistence.dao.IExpenseJpaDao;
import w.wexpense.service.DaoService;

@Service
public class ExpenseService extends DaoService<Expense, Long> {
	
//	@Autowired
//	private ITransactionLineJpaDao transactionLineDao;
	
	@Autowired
	public ExpenseService(IExpenseJpaDao dao) {
	   super(Expense.class, dao);
   } 
	
	@Override
   public Expense save(Expense entity) {
	   LOGGER.debug("Saving expense {}", entity);
	   
	   Expense newExpense = super.save(entity);

	   return newExpense;
   }
	
//	@Override
//   public Expense save(Expense entity) {
//	   LOGGER.debug("Saving expense {}", entity);
//	   
//	   List<TransactionLine> newTransactionLines = entity.getTransactions();
//	   
//	   Expense newExpense = super.save(entity);
//	   
//	   List<TransactionLine> oldTransactionLines = transactionLineDao.findByExpense(newExpense);	   
//	   LOGGER.debug("old expense's transaction line size{}", oldTransactionLines.size());
//	   
//	   for(TransactionLine newTransactionLine: newTransactionLines) {
//	   	newTransactionLine.setExpense(newExpense);
//	   	transactionLineDao.save(newTransactionLine);
//	   }
//	   for(TransactionLine oldTransactionLine: oldTransactionLines) {
//	   	if (!newTransactionLines.contains(oldTransactionLine)) {
//	   		transactionLineDao.delete(oldTransactionLine);	   		
//	   	}
//	   }
//	   return newExpense;
//	}
}
