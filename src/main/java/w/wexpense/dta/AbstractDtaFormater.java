package w.wexpense.dta;


public abstract class AbstractDtaFormater implements DtaFormater {

	protected String transactionType;
	
	public AbstractDtaFormater(String transactionType) {
		this.transactionType = transactionType;
	}
}
