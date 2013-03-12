package w.wexpense.service.model;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import w.wexpense.dta.DtaFormater;
import w.wexpense.dta.EndDtaFormater;
import w.wexpense.model.Expense;
import w.wexpense.model.Payment;
import w.wexpense.model.PaymentDta;
import w.wexpense.persistence.dao.IPaymentDtaJpaDao;
import w.wexpense.service.EntityService;

//@Service
public class PaymentDtaService extends EntityService<PaymentDta, Long> {

	@PersistenceContext
	private EntityManager entityManager;
	
	@Autowired
	public PaymentDtaService(IPaymentDtaJpaDao dao) {
	   super(PaymentDta.class, dao);
   }
	
	@Transactional
	public void generatePaymentDtas(Payment payment) throws Exception {
		// delete existing dtas		
		Query query = entityManager.createQuery("DELETE PaymentDta WHERE payment = :p");
		query.setParameter("p", payment);
		int i = query.executeUpdate();
		LOGGER.info("Deleted {} old payment DTAs", i);
		
		/**
		List<PaymentDta> l = payment.getDtaLines();
		getDao().deleteInBatch(l);
		LOGGER.info("Deleted {} old payment DTAs", l.size());
		*/
		
		// generate new ones
		List<PaymentDta> dtas = getPaymentDtas(payment);
		
		// save them
		for(PaymentDta dta: dtas) {
			getDao().save(dta);
		}
		LOGGER.info("Created {} new payment DTAs", dtas.size());
	}
	
	public List<PaymentDta> getPaymentDtas(Payment payment) throws Exception {
		List<PaymentDta> dtas = new ArrayList<PaymentDta>();
		int index=0;
		int order=0;
		for(Expense expense:payment.getExpenses()) {
			String formaterName = expense.getType().getPaymentGeneratorClassName();
			DtaFormater formater = (DtaFormater) Class.forName(formaterName).newInstance();
			for(String line: formater.format(payment, ++index, expense)) {
				dtas.add(new PaymentDta(payment, ++order, expense, line));
			}
		}
		
		String endLine = new EndDtaFormater().format(payment, ++index);
		dtas.add(new PaymentDta(payment, ++order, null, endLine));

		return dtas;
	}
}
