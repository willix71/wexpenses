package w.wexpense.web;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import w.wexpense.model.Payment;
import w.wexpense.model.PaymentDta;

@Controller
public class WexpensesController {

	private static final Logger LOGGER = LoggerFactory.getLogger(WexpensesController.class);
	
	@PersistenceContext
	private EntityManager entityManager;
	
	@RequestMapping("/wexpenses")
	public void helloWord(Model model) {
		
		LOGGER.info("WexpensesController called");
		
		String message = "Hello Wexpenses";
		model.addAttribute("message", message);
	}

	@RequestMapping("/dta")
	public void dta(@RequestParam(value="id") Long id, Model model) {
		
		LOGGER.info("Loading dta");
		
		Payment p = entityManager.find(Payment.class, id);
		
		StringBuilder sb = new StringBuilder();
		for(PaymentDta dta: p.getDtaLines()) {
			sb.append(dta.getData());
		}
		model.addAttribute("payments", sb.toString());
	}
}
