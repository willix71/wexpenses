package w.wexpense.model;

import java.text.MessageFormat;
import java.util.Date;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.OneToMany;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;

@Entity
public class Payment extends DBable {

	private static final long serialVersionUID = 2482940442245899869L;

	@NotNull
	@Temporal(TemporalType.DATE)
	private Date date;
	
	@NotNull
	private String filename;
	
    @OneToMany(mappedBy="payment")
    private List<Expense> expenses;

    @OneToMany(mappedBy="payment")
    private List<PaymentDta> dtaLines;
    
	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public String getFilename() {
		return filename;
	}

	public void setFilename(String filename) {
		this.filename = filename;
	}

	public List<Expense> getExpenses() {
		return expenses;
	}

	public void setExpenses(List<Expense> expenses) {
		this.expenses = expenses;
	}

	public List<PaymentDta> getDtaLines() {
		return dtaLines;
	}

	public void setDtaLines(List<PaymentDta> dtaLines) {
		this.dtaLines = dtaLines;
	}

	@Override
	public String toString() {		
		return MessageFormat.format("{0,date,dd/MM/yyyy} {1}", date, filename);
	} 
}
