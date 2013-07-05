package w.wexpense.service.model;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import w.wexpense.model.ExpenseType;
import w.wexpense.persistence.dao.IExpenseTypeJpaDao;
import w.wexpense.service.DaoService;

@Service
public class ExpenseTypeService extends DaoService<ExpenseType, Long> {

	@Autowired
	public ExpenseTypeService(IExpenseTypeJpaDao dao) {
	   super(ExpenseType.class, dao);
   }

}
