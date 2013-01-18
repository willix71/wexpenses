package w.wexpense.dta;

import java.util.ArrayList;
import java.util.List;

import w.wexpense.model.Expense;
import w.wexpense.model.Payment;
import w.wexpense.model.PaymentDta;

public class DtaService {

	private EndDtaFormater endFormater = new EndDtaFormater();
	
	public List<String> getPaymentDtaLines(Payment payment) throws Exception {
		List<String> lines = new ArrayList<String>();
		int index=0;
		for(Expense expense:payment.getExpenses()) {
			String formaterName = expense.getType().getPaymentGeneratorClassName();
			DtaFormater formater = (DtaFormater) Class.forName(formaterName).newInstance();
			lines.addAll(formater.format(payment, ++index, expense));
		}
		lines.add(new EndDtaFormater().formatLine01(payment, ++index));
		return lines;
	}

	public List<PaymentDta> getPaymentDtas(Payment payment) throws Exception {
		List<PaymentDta> dtas = new ArrayList<PaymentDta>();
		int index=0;
		for(Expense expense:payment.getExpenses()) {
			String formaterName = expense.getType().getPaymentGeneratorClassName();
			DtaFormater formater = (DtaFormater) Class.forName(formaterName).newInstance();
			for(String line: formater.format(payment, ++index, expense)) {
				dtas.add(new PaymentDta(payment, index, expense, line));
			}
		}
		String line = endFormater.formatLine01(payment, ++index);
		dtas.add(new PaymentDta(payment, index, null, line));
		return dtas;
	}
}
