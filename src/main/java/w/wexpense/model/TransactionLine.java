package w.wexpense.model;

import java.text.MessageFormat;
import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;

import w.wexpense.model.enums.TransactionLineEnum;

@Entity
//@TypeDefs({
//	@TypeDef(name = "transactionLineEnumType", typeClass = w.wexpense.persistence.dao.type.TransactionLineEnumType.class),
//	@TypeDef(name = "amountValueType", typeClass = w.wexpense.persistence.dao.type.AmountValueType.class)
//	})
public class TransactionLine extends DBable {

	private static final long serialVersionUID = 2482940442245899869L;
		
	private Integer period;
	
	@ManyToOne(fetch = FetchType.EAGER)
	private Expense expense;
	
	@ManyToOne(fetch = FetchType.EAGER)
	private Discriminator discriminator;
	
	@NotNull
	@ManyToOne(fetch = FetchType.EAGER)
	private Account account;
	
	@NotNull
	private TransactionLineEnum factor = TransactionLineEnum.OUT;
	
	@NotNull
	private Double amount;
	    
	@ManyToOne(fetch = FetchType.EAGER)
	private ExchangeRate exchangeRate;
	
	@NotNull
	private Double value;
	
	@Temporal(TemporalType.TIMESTAMP)
	private Date consolidatedDate;
	
	@ManyToOne
	private Consolidation consolidation;
	   
	private String description;
	
	public Expense getExpense() {
		return expense;
	}

	public void setExpense(Expense expense) {
		this.expense = expense;
	}

	public Discriminator getDiscriminator() {
		return discriminator;
	}

	public void setDiscriminator(Discriminator discriminator) {
		this.discriminator = discriminator;
	}

	public Account getAccount() {
		return account;
	}

	public void setAccount(Account account) {
		this.account = account;
	}

	public Double getAmount() {
		return amount;
	}

	public void setAmount(Double amount) {
		this.amount = amount;
	}

	public ExchangeRate getExchangeRate() {
		return exchangeRate;
	}

	public void setExchangeRate(ExchangeRate exchangeRate) {
		this.exchangeRate = exchangeRate;
	}

	public Double getValue() {
		return value;
	}

	public void setValue(Double value) {
		this.value = value;
	}

	public Double getInValue() {
		return TransactionLineEnum.IN==factor?value:null;
	}

	public Double getOutValue() {
		return TransactionLineEnum.OUT==factor?value:null;
	}
	
	public TransactionLineEnum getFactor() {
		return factor;
	}

	public void setFactor(TransactionLineEnum factor) {
		this.factor = factor;
	}

	public Date getConsolidatedDate() {
		return consolidatedDate;
	}

	public void setConsolidatedDate(Date consolidatedDate) {
		this.consolidatedDate = consolidatedDate;
	}

	public Consolidation getConsolidation() {
		return consolidation;
	}

	public void setConsolidation(Consolidation consolidation) {
		this.consolidation = consolidation;
	}

	public Integer getPeriod() {
		return period;
	}

	public void setPeriod(Integer period) {
		this.period = period;
	}
	
	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}
	
	@Override
	public String toString() {		
		return MessageFormat.format("{0} {1,number, 0.00} [{2}]", factor, amount, account);
	} 
}
