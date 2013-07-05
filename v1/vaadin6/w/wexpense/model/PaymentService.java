package w.wexpense.service.model;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import w.wexpense.model.Expense;
import w.wexpense.model.Payment;
import w.wexpense.persistence.dao.IExpenseJpaDao;
import w.wexpense.persistence.dao.IPaymentJpaDao;
import w.wexpense.service.DaoService;

@Service
public class PaymentService extends DaoService<Payment, Long> {

	@Autowired
	private IExpenseJpaDao expenseDao;
	
	@Autowired
	public PaymentService(IPaymentJpaDao dao) {
	   super(Payment.class, dao);
   }
	
	@Override
   public Payment save(Payment entity) {
	   LOGGER.debug("Saving payment's expenses {}", entity.getExpenses());
	   
	   List<Expense> newExpenses = entity.getExpenses();
	   
	   Payment newPayment = super.save(entity);
	   
	   List<Expense> oldExpenses = expenseDao.findByPayment(newPayment);
	   
	   LOGGER.debug("olg payment's expenses size{}", oldExpenses.size());
	   for(Expense newExpense: newExpenses) {
	   	newExpense.setPayment(newPayment);
	   	expenseDao.save(newExpense);
	   }
	   for(Expense oldExpense: oldExpenses) {
	   	if (!newExpenses.contains(oldExpense)) {
	   		oldExpense.setPayment(null);
		   	expenseDao.save(oldExpense);
	   	}
	   }
	   return newPayment;
   }

	public Payment getPaymentByUid(String uid) throws Exception {
		return ((IPaymentJpaDao) getDao()).findByUid(uid);
	}
	
	public Payment getPaymentByFilename(String filename) throws Exception {
		return ((IPaymentJpaDao) getDao()).findByUid(filename);
	}
}
