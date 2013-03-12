package w.wexpense.service;

import java.io.Serializable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.jpa.repository.JpaRepository;


public class EntityService<T, ID extends Serializable> implements StorableService<T, ID> {

	protected final Logger LOGGER = LoggerFactory.getLogger(getClass());
	
	private Class<T> entityClass;
	
	private JpaRepository<T, ID> dao;
	
	public EntityService(Class<T> entityClass, JpaRepository<T, ID> dao) {
		this.entityClass = entityClass;
		this.dao = dao;
	}
		
	public Class<T> getEntityClass() {
		return entityClass;
	}

	public JpaRepository<T, ID> getDao() {
		return dao;
	}

	@Override
   public T newInstance() {
		try {
			return entityClass.newInstance();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	@Override
   public T load(ID id) {
		return dao.findOne(id);
	}
	
	@Override
   public T save(T entity) {
		return dao.save(entity);
	}
	

	@Override
   public void delete(T entity) {
		dao.delete(entity);
	}

	@Override
   public void delete(ID id) {
		dao.delete(id);
	}
}
