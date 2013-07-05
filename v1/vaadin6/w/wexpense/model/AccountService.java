package w.wexpense.service.model;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import w.wexpense.model.Account;
import w.wexpense.persistence.dao.IAccountJpaDao;
import w.wexpense.service.DaoService;

@Service
public class AccountService extends DaoService<Account, Long> {

	@Autowired
	public AccountService(IAccountJpaDao dao) {
	   super(Account.class, dao);
   }

}
