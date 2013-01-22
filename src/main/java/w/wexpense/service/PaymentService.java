package w.wexpense.service;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.springframework.stereotype.Service;

import w.wexpense.model.Payment;

@Service
public class PaymentService {
	
	@PersistenceContext
	private EntityManager entityManager;
	
	public Payment getPaymentById(Long id) throws Exception {
		return entityManager.find(Payment.class, id);
	}
	
	public Payment getPaymentByUid(String uid) throws Exception {
		Query q = entityManager.createQuery("select p from Payment p where p.uid = :uid", Payment.class);
		q.setParameter("uid", uid);
		Payment payment = (Payment) q.getSingleResult();
		return payment;
	}
	
	public Payment getPaymentByFilename(String filename) throws Exception {
		Query q = entityManager.createQuery("select p from Payment p where p.filename = :filename", Payment.class);
		q.setParameter("filename", filename);
		Payment payment = (Payment) q.getSingleResult();
		return payment;
	}
}
