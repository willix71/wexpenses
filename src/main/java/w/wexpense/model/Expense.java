package w.wexpense.model;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;

import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

@Entity
public class Expense extends DBable {

	private static final long serialVersionUID = 2482940442245899869L;
	
	@Temporal(TemporalType.TIMESTAMP)
	private Date date;
	
	@NotNull
	private Double amount;
	
	@NotNull
	@ManyToOne(fetch = FetchType.EAGER)
	private Currency currency;
	
	@ManyToOne(fetch = FetchType.EAGER)
	private ExchangeRate defaultExchangeRate;
	
	@ManyToOne(fetch = FetchType.EAGER)
	//@JoinColumn(name="PAYEE_OID")
	private Payee payee;
	
	@ManyToOne(fetch = FetchType.EAGER)
	//@JoinColumn(name="TYPE_OID")
	private ExpenseType type;
	
	private String externalReference;
	
	private String description;
	
	@ManyToOne
	private Payment payment;
	
   @OneToMany(mappedBy="expense")
   @OnDelete(action=OnDeleteAction.CASCADE)
   private List<TransactionLine> transactions = new ArrayList<>();
   
	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public Double getAmount() {
		return amount;
	}

	public void setAmount(Double amount) {
		this.amount = amount;
	}

	public Currency getCurrency() {
		return currency;
	}

	public void setCurrency(Currency currency) {
		this.currency = currency;
	}

	public ExchangeRate getDefaultExchangeRate() {
		return defaultExchangeRate;
	}

	public void setDefaultExchangeRate(ExchangeRate defaultExchangeRate) {
		this.defaultExchangeRate = defaultExchangeRate;
	}

	public Payee getPayee() {
		return payee;
	}

	public void setPayee(Payee payee) {
		this.payee = payee;
	}

	public ExpenseType getType() {
		return type;
	}

	public void setType(ExpenseType type) {
		this.type = type;
	}

	public String getExternalReference() {
		return externalReference;
	}

	public void setExternalReference(String externalReference) {
		this.externalReference = externalReference;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}
	
	public List<TransactionLine> getTransactions() {
		return transactions;
	}

	public void setTransactions(List<TransactionLine> transactions) {
		this.transactions = transactions;
	}

	public Payment getPayment() {
		return payment;
	}

	public void setPayment(Payment payment) {
		this.payment = payment;
	}

	@Override
	public String toString() {		
		return MessageFormat.format("{0,date,dd/MM/yyyy} {1} {2,number, 0.00}{3}", date, payee, amount, currency);
	} 
}
