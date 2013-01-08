package w.wexpense.model;

import java.text.MessageFormat;
import java.util.Date;

import javax.persistence.Entity;
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
	
	@ManyToOne
	private Expense expense;
	
	@NotNull
	@ManyToOne
	private Account account;
	
	@NotNull
	private TransactionLineEnum factor = TransactionLineEnum.OUT;
	
	private Double amount;
	    
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

	public Double getInAmount() {
		return TransactionLineEnum.IN==factor?amount:null;
	}

	public Double getOutAmount() {
		return TransactionLineEnum.OUT==factor?amount:null;
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
