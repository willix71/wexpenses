package w.wexpense.service;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import w.wexpense.dta.DtaFormater;
import w.wexpense.dta.EndDtaFormater;
import w.wexpense.model.Expense;
import w.wexpense.model.Payment;
import w.wexpense.model.PaymentDta;

@Service
public class PaymentDtaService {

	private static final Logger LOGGER = LoggerFactory.getLogger(PaymentDtaService.class);
	
	@PersistenceContext
	private EntityManager entityManager;
	
	private EndDtaFormater endFormater = new EndDtaFormater();
	
	@Transactional
	public void generatePaymentDtas(Payment payment) throws Exception {
		// delete existing dtas
		Query query = entityManager.createQuery("DELETE PaymentDta WHERE payment = :p");
		query.setParameter("p", payment);
		int i = query.executeUpdate();
		LOGGER.info("Deleted {} old payment DTAs", i);
		
		// generate new ones
		List<PaymentDta> dtas = getPaymentDtas(payment);
		
		// save them
		for(PaymentDta dta: dtas) {
			entityManager.persist(dta);
		}
		LOGGER.info("Created {} new payment DTAs", i);
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
		
		String endLine = endFormater.format(payment, ++index);
		dtas.add(new PaymentDta(payment, ++order, null, endLine));

		return dtas;
	}
}
