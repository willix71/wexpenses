package w.wexpense.model;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;

@Entity
public class Payment extends DBable<Payment> implements Selectable {

	private static final long serialVersionUID = 2482940442245899869L;

	@NotNull
	@Temporal(TemporalType.DATE)
	private Date date;
	
	@NotNull
	private String filename;
	
	private boolean selectable = true;
	
	@OneToMany(mappedBy = "payment")
	@OrderBy("date, amount")
	private List<Expense> expenses;

	@OneToMany(mappedBy = "payment")
	@OrderBy("orderBy")
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

	public boolean isSelectable() {
		return selectable;
	}
	
	@Override
	public boolean getSelectable() {
		return selectable;
	}

	public void setSelectable(boolean selectable) {
		this.selectable = selectable;
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
	
	@Override
   public Payment duplicate() {
		Payment klone = super.duplicate();
		klone.getExpenses().clear();
		klone.getDtaLines().clear();
		return klone;
   }

	@Override
   public Payment klone() {
		Payment klone = super.klone();
		if (klone.getExpenses()!=null) {
			klone.setExpenses(new ArrayList<Expense>(klone.getExpenses()));
		}
		if (klone.getDtaLines()!=null) {
			klone.setDtaLines(new ArrayList<PaymentDta>(klone.getDtaLines()));
		}
		return klone;
   }
}
