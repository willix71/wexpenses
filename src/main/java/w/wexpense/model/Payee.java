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

    private String location;

    @ManyToOne(fetch = FetchType.EAGER)
    //@JoinColumn(name="CITY_OID")
    private City city;

    private String externalReference;
    
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

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
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
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		if (prefix != null) {
			sb.append(prefix);
		}
		sb.append(name);	
		if (city != null) {
			sb.append(", ").append(city);	
		}

		return sb.toString();
	}
}
