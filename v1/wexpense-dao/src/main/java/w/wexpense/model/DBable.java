package w.wexpense.model;

import java.io.Serializable;
import java.text.MessageFormat;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.MappedSuperclass;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Version;

import org.springframework.format.annotation.DateTimeFormat;

import w.wexpense.utils.Defaults;

@MappedSuperclass
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public abstract class DBable implements Serializable {

	private static final long serialVersionUID = 2482940442245899869L;

	@Id	
	@Column(name = "uid")
	//@GeneratedValue( strategy = GenerationType.AUTO )
	private String uid;

	@Temporal(TemporalType.TIMESTAMP)
	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss,SSS")
	private Date createdTs;

	@Version
	@Temporal(TemporalType.TIMESTAMP)
	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss,SSS")
	private Date modifiedTs;

	public DBable() {
		this.setUid(Defaults.newUid());
		this.setCreatedTs(Defaults.newCreatedTs());
	}

	public String getUid() {
		return uid;
	}

	public void setUid(String uid) {
		this.uid = uid;
	}

	public Date getCreatedTs() {
		return createdTs;
	}

	public void setCreatedTs(Date createdTs) {
		this.createdTs = createdTs;
	}

	public Date getModifiedTs() {
		return modifiedTs;
	}

	public void setModifiedTs(Date modifiedTs) {
		this.modifiedTs = modifiedTs;
	}

	public boolean isNew() {
		return modifiedTs == null;
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((uid == null) ? 0 : uid.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		
		DBable other = (DBable) obj;
		if (uid == null) {
			if (other.uid != null) return false;
		} 			
		return (uid.equals(other.uid));
	}
	
	@Override
	public String toString() {		
		return MessageFormat.format("{0}{{1}}", this.getClass().getSimpleName(), uid);
	}
}
