package w.wexpense.model;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.validation.constraints.NotNull;

@Entity
public class Payee extends DBable {

	private static final long serialVersionUID = 2482940442245899869L;
	
    @ManyToOne(fetch = FetchType.EAGER)
    //@JoinColumn(name="TYPE_OID")
    private PayeeType type;

    private String prefix;
    
    @NotNull
    private String name;

    private String address1;

    private String address2;
    
    @ManyToOne(fetch = FetchType.EAGER)
    //@JoinColumn(name="CITY_OID")
    private City city;

	// ISR
    private String externalReference;
    
    @ManyToOne(fetch = FetchType.EAGER)
    private Payee bankDetails;
   
    private String display;
    
    @PrePersist
    @PreUpdate
    public void preupdate() {
    	display = toString().toLowerCase();
    }

	public String getDisplay() {
		return display;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getAddress1() {
		return address1;
	}

	public void setAddress1(String address1) {
		this.address1 = address1;
	}
	
	public String getAddress2() {
		return address2;
	}

	public void setAddress2(String address2) {
		this.address2 = address2;
	}
	
	public City getCity() {
		return city;
	}

	public void setCity(City city) {
		this.city = city;
	}

	public PayeeType getType() {
		return type;
	}

	public void setType(PayeeType type) {
		this.type = type;
	}

	public String getPrefixedName() {
		if (prefix == null) {
			return name;
		} else {
			return prefix + name;
		}
	}
	
	public String getPrefix() {
		return prefix;
	}

	public void setPrefix(String typeOf) {
		this.prefix = typeOf;
	}

	public String getExternalReference() {
		return externalReference;
	}

	public void setExternalReference(String externalReference) {
		this.externalReference = externalReference;
	}
	
	public Payee getBankDetails() {
		return bankDetails;
	}

	public void setBankDetails(Payee bankDetails) {
		this.bankDetails = bankDetails;
	}

	@Override
	public String toString() {
		if (city == null) {
			return getPrefixedName();
		} else {
			return getPrefixedName() + ", " + city.toString();	
		}
	}
}
