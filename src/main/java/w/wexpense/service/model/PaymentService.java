package w.wexpense.service.model;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import w.wexpense.model.Payment;
import w.wexpense.persistence.dao.IPaymentJpaDao;
import w.wexpense.service.EntityService;

@Service
public class PaymentService extends EntityService<Payment, Long> {

	@Autowired
	public PaymentService(IPaymentJpaDao dao) {
	   super(Payment.class, dao);
   }
	
	public Payment getPaymentByUid(String uid) throws Exception {
		return ((IPaymentJpaDao) getDao()).findByUid(uid);
	}
	
	public Payment getPaymentByFilename(String filename) throws Exception {
		return ((IPaymentJpaDao) getDao()).findByUid(filename);
	}
}
