package w.wexpense.service.model;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import w.wexpense.model.Payee;
import w.wexpense.persistence.dao.IPayeeJpaDao;
import w.wexpense.service.EntityService;

@Service
public class PayeeService extends EntityService<Payee, Long> {

	@Autowired
	public PayeeService(IPayeeJpaDao dao) {
	   super(Payee.class, dao);
   }

}
