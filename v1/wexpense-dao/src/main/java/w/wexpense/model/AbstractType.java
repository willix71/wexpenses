package w.wexpense.model;

import javax.persistence.MappedSuperclass;
import javax.validation.constraints.NotNull;

@MappedSuperclass
public abstract class AbstractType extends DBable {

	private static final long serialVersionUID = 2482940442245899869L;

	@NotNull
	private String name;

	private boolean selectable = true;
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	public boolean isSelectable() {
		return selectable;
	}

	public void setSelectable(boolean selectable) {
		this.selectable = selectable;
	}

	@Override
	public String toString() {
		return name;
	}
}
