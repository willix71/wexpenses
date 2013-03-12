package w.wexpense.service.model;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import w.wexpense.model.PayeeType;
import w.wexpense.persistence.dao.IPayeeTypeJpaDao;
import w.wexpense.service.EntityService;

@Service
public class PayeeTypeService extends EntityService<PayeeType, Long> {

	@Autowired
	public PayeeTypeService(IPayeeTypeJpaDao dao) {
	   super(PayeeType.class, dao);
   }

}
