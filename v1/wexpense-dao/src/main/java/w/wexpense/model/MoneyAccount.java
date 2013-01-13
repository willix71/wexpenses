package w.wexpense.model;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotNull;

import w.wexpense.model.enums.AccountEnum;

@Entity(name="Account")
public class MoneyAccount extends AbstractAccount<MoneyAccount> {
	
	private static final long serialVersionUID = 2482940442245899869L;
    
	@NotNull
	@Enumerated(EnumType.STRING)
	private AccountEnum type = AccountEnum.FILTER;
	
	@ManyToOne()
	private Currency currency;

	public AccountEnum getType() {
		return type;
	}

	public void setType(AccountEnum type) {
		this.type = type;
	}

	public Currency getCurrency() {
		return currency;
	}

	public void setCurrency(Currency currency) {
		this.currency = currency;
	}	
	
	@Override
	public String toString() {		
		return getFullName();
	} 
}
