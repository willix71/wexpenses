package w.wexpense.dta;

import java.util.List;

import w.wexpense.model.Expense;
import w.wexpense.model.Payment;

public interface DtaFormater {

	List<String> format(Payment payment, int index, Expense expense);
}
