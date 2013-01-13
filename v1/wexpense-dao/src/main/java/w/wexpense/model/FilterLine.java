package w.wexpense.model;

import java.text.MessageFormat;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotNull;

@Entity
public class FilterLine extends DBable {

	private static final long serialVersionUID = 2482940442245899869L;

	@NotNull
	@ManyToOne
	//@JoinColumn(name="expense_OID")
	private TransactionLine transactionLine;
	
	private Double value;

	@NotNull
	@ManyToOne
	//@JoinColumn(name="account_OID")
	private MoneyAccount account;
	
	private String description;
	
	public TransactionLine getTransactionLine() {
		return transactionLine;
	}

	public void setTransactionLine(TransactionLine transaction) {
		this.transactionLine = transaction;
	}

	public MoneyAccount getAccount() {
		return account;
	}

	public void setAccount(MoneyAccount account) {
		this.account = account;
	}

	public Double getValue() {
		return value;
	}

	public void setValue(Double value) {
		this.value = value;
	}
	
	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}
	
	@Override
	public String toString() {		
		return MessageFormat.format("{0,number, 0.00} {1}", value, account);
	} 
}
