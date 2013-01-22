package w.wexpense.model;

import java.math.BigDecimal;
import java.text.MessageFormat;
import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.ValidationException;
import javax.validation.constraints.NotNull;

import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;
import org.hibernate.annotations.TypeDefs;

import w.wexpense.model.enums.TransactionLineEnum;

@Entity
@TypeDefs({
	@TypeDef(name = "transactionLineEnumType", typeClass = w.wexpense.persistence.type.TransactionLineEnumType.class),
	@TypeDef(name = "amountValueType", typeClass = w.wexpense.persistence.type.AmountValueType.class)
	})
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
	@Type(type="transactionLineEnumType")
	private TransactionLineEnum factor = TransactionLineEnum.OUT;
	
	@NotNull
	private BigDecimal amount;
	    
	@ManyToOne(fetch = FetchType.EAGER)
	private ExchangeRate exchangeRate;
	
	@NotNull
	@Type(type="amountValueType")
	private AmountValue amountValue;
	
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

	// TODO Remove once figured out a way to get Nested properties out of a BeanItemContainer
	public Payee getPayee() {
		return expense==null?null:expense.getPayee();
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

	public BigDecimal getAmount() {
		return amount;
	}

	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}

	public ExchangeRate getExchangeRate() {
		return exchangeRate;
	}

	public void setExchangeRate(ExchangeRate exchangeRate) {
		this.exchangeRate = exchangeRate;
	}

	/**
	 * automatically calculates the amountValue based on the amount, 
	 * the exchange rate and the currency rounding factor
	 */
	public void updateValue() {
		// using double because it will be rounded by the AmountValue object anyway
		// so we don't need the precision of a BigDecimal
		double v = amount == null?0.0:amount.doubleValue();
			
		Currency currency = null;
		if (exchangeRate != null) {
			v *= exchangeRate.getRate();

			// get the currency of the exchange rate
			currency = exchangeRate.getBuyCurrency();
		}
		if (currency == null && account != null) {
			// fall back on the currency of the account
			currency = account.getCurrency();
		}
		if (currency != null && currency.getRoundingFactor() != null) {
			// perform rounding
			v = Math.rint(v * currency.getRoundingFactor()) / currency.getRoundingFactor();
		}
		amountValue = AmountValue.fromRealValue(v);
	}

	public BigDecimal getValue() {
		return amountValue==null?null:amountValue.getBigValue();
	}

	public void setValue(BigDecimal value) {
		this.amountValue = value==null?null:AmountValue.fromBigValue(value);
	}

	public BigDecimal getInValue() {
		return TransactionLineEnum.IN==factor?getValue():null;
	}

	public BigDecimal getOutValue() {
		return TransactionLineEnum.OUT==factor?getValue():null;
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
		String s = account == null ? " " : account.toString();
		if (discriminator != null) {
			s += "/" + discriminator.toString();
		}
		return MessageFormat.format("{0} {1,number, 0.00} [{2}]", factor, s );
	}
	
	public boolean validate() {		
		if (exchangeRate != null && account.getCurrency() != null) {
			if (!account.getCurrency().equals(exchangeRate.getBuyCurrency())) {
				throw new ValidationException("transaction line's account and exchange rate currencies don't match");
			}
		}
		return true;
	}
}
