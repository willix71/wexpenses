package w.wexpense.model;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Entity
public class Currency implements Codable {

	private static final long serialVersionUID = 2482940442245899869L;

	@Id
	@Size(min = 3, max = 3)
	private String code;

	@NotNull
	private String name;

	private Integer roundingFactor;
	
	public Currency() {
		super();
	}
		
	public Currency(String code, String name, Integer roundingFactor) {
		super();
		this.code = code;
		this.name = name;
		this.roundingFactor = roundingFactor;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Integer getRoundingFactor() {
		return roundingFactor;
	}

	public void setRoundingFactor(Integer roundingFactor) {
		this.roundingFactor = roundingFactor;
	}

	@Override
	public int hashCode() {
		return code==null?0:code.hashCode();
	}
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		
		Currency other = (Currency) obj;
		if (code == null) {
			if (other.code != null) return false;
		} 			
		return (code.equals(other.code));
	}	

	@Override
	public String toString() {
		return code;
	}
}
