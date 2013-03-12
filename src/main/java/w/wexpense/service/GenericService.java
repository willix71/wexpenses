package w.wexpense.service;

import java.io.Serializable;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import w.wexpense.persistence.PersistenceUtils;


public class GenericService<T, ID extends Serializable> implements StorableService<T, ID> {

	protected final Logger LOGGER = LoggerFactory.getLogger(getClass());
	
	@PersistenceContext
	private EntityManager entityManager;
	
	private Class<T> entityClass;
	
	public GenericService(Class<T> entityClass) {
		this.entityClass = entityClass;
	}
		
	public Class<T> getEntityClass() {
		return entityClass;
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
		return entityManager.find(entityClass, id);
	}
	
	@Override
	public T save(T entity) {
		entityManager.persist(entity);
		
		return entity;
	}
	
	@Override
	public void delete(T entity) {
		entityManager.remove(entity);
	}
	
	@Override
	public void delete(ID id) {
		String entityName = entityClass.getSimpleName();
		String idName = PersistenceUtils.getIdName(entityClass);
		Query query = entityManager.createQuery("DELETE " + entityName + " WHERE " + idName + " = :id");
		query.setParameter("id", id);
		query.executeUpdate();
	}
}
