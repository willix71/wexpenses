package w.wexpense.service.model;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import w.wexpense.model.Expense;
import w.wexpense.model.TransactionLine;
import w.wexpense.persistence.dao.IExpenseJpaDao;
import w.wexpense.persistence.dao.ITransactionLineJpaDao;
import w.wexpense.service.DaoService;

@Service
public class ExpenseService extends DaoService<Expense, Long> {
	

	@Autowired
	private ITransactionLineJpaDao transactionLineDao;
	
	@Autowired
	public ExpenseService(IExpenseJpaDao dao) {
	   super(Expense.class, dao);
   } 
	
	@Override
   public Expense save(Expense entity) {
	   LOGGER.debug("Saving expense {}", entity);
	   
	   List<TransactionLine> newTransactionLines = entity.getTransactions();
	   
	   Expense newExpense = super.save(entity);
	   
	   List<TransactionLine> oldTransactionLines = transactionLineDao.findByExpense(newExpense);	   
	   LOGGER.debug("old expense's transaction line size{}", oldTransactionLines.size());
	   
	   for(TransactionLine newTransactionLine: newTransactionLines) {
	   	newTransactionLine.setExpense(newExpense);
	   	transactionLineDao.save(newTransactionLine);
	   }
	   for(TransactionLine oldTransactionLine: oldTransactionLines) {
	   	if (!newTransactionLines.contains(oldTransactionLine)) {
	   		transactionLineDao.delete(oldTransactionLine);	   		
	   	}
	   }
	   return newExpense;
   }
	
/*
	@Autowired
	private IExpenseTypeJpaDao expenseTypeDao;

	@Autowired
	private IAccountJpaDao accountDao;
	
	//@Resource
	//@Qualifier("uids")
	private Map<String, String> uids;
	
	public Expense newBvrExpense() {
		return newExpense("BVR", uids.get("account:ec"));
	}
	
	public Expense newBvoExpense() {
		return newExpense("BVO", uids.get("account:ec"));
	}
	
	public Expense newBviExpense() {
		return newExpense("BVI", uids.get("account:ec"));
	}
	
	public Expense newCashExpense() {
		return newExpense("recu", uids.get("account:cash"));
	}
	
	public Expense newDebitExpense() {
		return newExpense("recu", uids.get("account:ec"));
	}
	
	public Expense newCreditExpense() {
		return newExpense("recu", uids.get("account:mastercard"));
	}
	
	public Expense newExpense(String expenseType, String accountUid) {
		Expense expense = new Expense();
		expense.setDate(new Date());
		expense.setType( expenseTypeDao.findByName(expenseType) );
		
		TransactionLine line = new TransactionLine();
		line.setAccount( accountDao.findByUid(accountUid) );
		line.setFactor(TransactionLineEnum.OUT);
		expense.addTransaction(line);
		
		line = new TransactionLine();
		line.setFactor(TransactionLineEnum.IN);
		expense.addTransaction(line);
		
		return expense;
	}*/
}
