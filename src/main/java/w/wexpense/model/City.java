package w.wexpense.model;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.ManyToOne;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.validation.constraints.NotNull;

@Entity
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public class City extends DBable {

	private static final long serialVersionUID = 2482940442245899869L;

	private String zip;

	@NotNull
	private String name;

	@ManyToOne(fetch = FetchType.EAGER)
	private Country country;

	private String display;

	public City() {
		super();
	}

	public City(String zip, String name, Country country) {
		super();
		this.zip = zip;
		this.name = name;
		this.country = country;
	}

	@PrePersist
	@PreUpdate
	public void preupdate() {
		display = toString().toLowerCase();
	}

	public String getDisplay() {
		return display;
	}

	public String getZip() {
		return zip;
	}

	public void setZip(String zip) {
		this.zip = zip;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Country getCountry() {
		return country;
	}

	public void setCountry(Country country) {
		this.country = country;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		if (zip != null) {
			sb.append(zip).append(" ");
		}
		sb.append(name);	
		if (country != null) {
			sb.append(" (").append(country).append(")");	
		}
		return sb.toString();
	}
}
