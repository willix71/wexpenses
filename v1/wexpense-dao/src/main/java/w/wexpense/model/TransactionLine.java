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
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;
import org.hibernate.annotations.TypeDefs;
import org.springframework.format.annotation.DateTimeFormat;

import w.wexpense.model.enums.TransactionLineEnum;

@Entity
@TypeDefs({
	@TypeDef(name = "transactionLineEnumType", typeClass = w.wexpense.persistence.dao.type.TransactionLineEnumType.class),
	@TypeDef(name = "amountValueType", typeClass = w.wexpense.persistence.dao.type.AmountValueType.class)
	})
public class TransactionLine extends DBable {

	private static final long serialVersionUID = 2482940442245899869L;
		
	private Integer period;
	
	@ManyToOne
	//@JoinColumn(name="expense_OID")
	private Expense expense;
	
	//@NotNull
	@ManyToOne
	//@JoinColumn(name="account_OID")
	private MoneyAccount account;
	
	@NotNull
	@Type(type="transactionLineEnumType")
	private TransactionLineEnum factor = TransactionLineEnum.OUT;
	
	private Double amount;
	
	@ManyToOne
	//@JoinColumn(name="exchange_rate_OID")
	private ExchangeRate exchangeRate;

   //	@NotNull
   //	private Double value;
	
	@NotNull
	@Type(type="amountValueType")
	private AmountValue amountValue = AmountValue.fromValue(0);
	
    @OneToMany(fetch = FetchType.LAZY, mappedBy="transactionLine")
    @OnDelete(action=OnDeleteAction.CASCADE)
    private List<FilterLine> filters = new ArrayList<>();
    
	@Temporal(TemporalType.TIMESTAMP)
	@DateTimeFormat(pattern = "dd/MM/yyyy")
	private Date consolidatedDate;
	
	@ManyToOne
	//@JoinColumn(name="consolidation_OID")
	private Consolidation consolidation;
    
	private String description;
	
	public Expense getExpense() {
		return expense;
	}

	public void setExpense(Expense expense) {
		this.expense = expense;
	}

	public MoneyAccount getAccount() {
		return account;
	}

	public void setAccount(MoneyAccount account) {
		this.account = account;
	}

	public Double getAmount() {
		return amount;
	}

	public void setAmount(Double amount) {
		this.amount = amount;
	}

	public AmountValue getAmountValue() {
		return amountValue;
	}

	public void setAmountValue(AmountValue value) {
		this.amountValue = value;
	}
	
	public Double getValue() {
		return getAmountValue().getRealValue();
	}

	public void setValue(Double value) {
		setAmountValue(AmountValue.fromRealValue(amount));
	}

	public TransactionLineEnum getFactor() {
		return factor;
	}

	public void setFactor(TransactionLineEnum factor) {
		this.factor = factor;
	}

	public ExchangeRate getExchangeRate() {
		return exchangeRate;
	}

	public void setExchangeRate(ExchangeRate exchangeRate) {
		this.exchangeRate = exchangeRate;
	}

	public List<FilterLine> getFilters() {
		return filters;
	}

	public void setFilters(List<FilterLine> filters) {
		this.filters = filters;
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
		return MessageFormat.format("{0} {1,number, 0.00} [{2}]", factor, amountValue.getRealValue(), account);
	} 
}
