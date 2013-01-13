package w.wexpense.model;

import java.util.List;

import javax.persistence.FetchType;
import javax.persistence.ManyToOne;
import javax.persistence.MappedSuperclass;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.PreUpdate;

@MappedSuperclass
public class AbstractAccount<T>  extends AbstractType {

	private static final long serialVersionUID = 2482940442245899869L;
	
    @ManyToOne(fetch = FetchType.LAZY) 
    //@JoinColumn(name="PARENT_OID")
    private T parent;

    @OneToMany(fetch = FetchType.LAZY, mappedBy="parent")
    @OrderBy("number")
    private List<T> children;

    private String number;      
    
    private String fullName;
    
    private String fullNumber;
    
    private String display;
    
    @PreUpdate
    public void preupdate() {
    	display = fullNumber + " " + fullName;
    }

	public String getDisplay() {
		return display;
	}
	
	public T getParent() {
		return parent;
	}

	public void setParent(T parent) {
		this.parent = parent;
	}

	public List<T> getChildren() {
		return children;
	}

	public void setChildren(List<T> children) {
		this.children = children;
	}

	public String getNumber() {
		return number;
	}

	public void setNumber(String number) {
		this.number = number;
	}

	public String getFullName() {
		return fullName;
	}

	public void setFullName(String fullName) {
		this.fullName = fullName;
	}

	public String getFullNumber() {
		return fullNumber;
	}

	public void setFullNumber(String fullNumber) {
		this.fullNumber = fullNumber;
	}
	
}
