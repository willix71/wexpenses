package w.wexpense.service.model;

import java.util.Date;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import w.wexpense.model.Expense;
import w.wexpense.model.TransactionLine;
import w.wexpense.model.enums.TransactionLineEnum;
import w.wexpense.persistence.dao.IAccountJpaDao;
import w.wexpense.persistence.dao.IExpenseJpaDao;
import w.wexpense.persistence.dao.IExpenseTypeJpaDao;
import w.wexpense.service.EntityService;

//@Service
public class ExpenseService extends EntityService<Expense, Long> {


	@Autowired
	private IExpenseTypeJpaDao expenseTypeDao;

	@Autowired
	private IAccountJpaDao accountDao;
	
	@Resource
	@Qualifier("uids")
	private Map<String, String> uids;

	
	@Autowired
	public ExpenseService(IExpenseJpaDao dao) {
	   super(Expense.class, dao);
   } 
	
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
	}
}
