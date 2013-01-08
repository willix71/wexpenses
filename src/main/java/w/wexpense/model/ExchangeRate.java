package w.wexpense.model;

import java.text.MessageFormat;
import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;

@Entity
public class ExchangeRate extends DBable {

	private static final long serialVersionUID = 2482940442245899869L;
	
    @ManyToOne(fetch = FetchType.LAZY) 
    //@JoinColumn(name="PAYEE_OID")
    private Payee institution;
    
    @Temporal(TemporalType.DATE)
    private Date date;
    
    @NotNull
    private Currency buyCurrency;
    
    @NotNull
    private Currency sellCurrency; 
    
    @NotNull
    private Double rate;
    
    private Double fee;

	public Payee getInstitution() {
		return institution;
	}

	public void setInstitution(Payee institution) {
		this.institution = institution;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public Currency getBuyCurrency() {
		return buyCurrency;
	}

	public void setBuyCurrency(Currency buyCurrency) {
		this.buyCurrency = buyCurrency;
	}

	public Currency getSellCurrency() {
		return sellCurrency;
	}

	public void setSellCurrency(Currency sellCurrency) {
		this.sellCurrency = sellCurrency;
	}

	public Double getRate() {
		return rate;
	}

	public void setRate(Double rate) {
		this.rate = rate;
	}

	public Double getFee() {
		return fee;
	}

	public void setFee(Double fee) {
		this.fee = fee;
	}
	
	@Override
	public String toString() {		
		return MessageFormat.format("{0} x {2,number, 0.00000} {1}", sellCurrency, buyCurrency, rate);
	} 
}
