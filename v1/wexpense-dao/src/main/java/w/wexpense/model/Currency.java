package w.wexpense.model;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Entity
public class Currency  implements Serializable {

	private static final long serialVersionUID = 2482940442245899869L;
	
    @Id
	@Size(min=3, max=3)
    private String code;
   
    @NotNull
    private String name;
    
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

	@Override
	public int hashCode() {
		return code.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (obj != null && obj instanceof Currency) {
			return code.equals(((Currency) obj).code);
		}
		return false;
	}

	@Override
	public String toString() {
		return code;
	}
}
