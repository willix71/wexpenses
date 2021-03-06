package w.wexpense.model;

import java.math.BigDecimal;
import java.text.MessageFormat;
import java.util.Date;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;

@Entity
public class Consolidation extends DBable {

	private static final long serialVersionUID = 2482940442245899869L;
	
	@NotNull
	@ManyToOne
	private Payee institution;

	@NotNull
	@Temporal(TemporalType.DATE)
	private Date date;
	
	private BigDecimal openingBalance;
	
	private BigDecimal closingBalance;
	
    @OneToMany(mappedBy="consolidation")
    @OrderBy("consolidatedDate")
    private List<TransactionLine> transactions;

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

	public BigDecimal getOpeningBalance() {
		return openingBalance;
	}

	public void setOpeningBalance(BigDecimal openingBalance) {
		this.openingBalance = openingBalance;
	}

	public BigDecimal getClosingBalance() {
		return closingBalance;
	}

	public void setClosingBalance(BigDecimal closingBalance) {
		this.closingBalance = closingBalance;
	}

	public List<TransactionLine> getTransactions() {
		return transactions;
	}

	public void setTransactions(List<TransactionLine> transactions) {
		this.transactions = transactions;
	}
    
	@Override
	public String toString() {		
		return MessageFormat.format("{0,date,dd/MM/yyyy} {1}", date, institution);
	} 
}
