package w.wexpense.model;

import java.io.Serializable;
import java.text.MessageFormat;
import java.util.Date;
import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.MappedSuperclass;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Version;
//import javax.persistence.Version;

@MappedSuperclass
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public abstract class DBable implements Serializable {

	private static final long serialVersionUID = 2482940442245899869L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
	
	@Version
	private Long version;

	@Temporal(TemporalType.TIMESTAMP)
	private Date modifiedTs;
	
	@Temporal(TemporalType.TIMESTAMP)
	private Date createdTs;

	@Column(name = "uid", unique=true, nullable=false, updatable=false)
	private String uid;


	public DBable() {
		uid = UUID.randomUUID().toString();
		createdTs = new Date();
	}

	@PrePersist
	@PreUpdate
	public void preupdate() {
		modifiedTs = new Date();
	}
	
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getVersion() {
		return version;
	}

	public void setVersion(Long version) {
		this.version = version;
	}

	public String getFullId() {
		return (id == null?0:id)+"."+(version == null?0:version);
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
