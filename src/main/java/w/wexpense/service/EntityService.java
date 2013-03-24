package w.wexpense.service;

import java.io.Serializable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.jpa.repository.JpaRepository;

import com.vaadin.data.Property;
import com.vaadin.data.util.BeanItem;

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
	@SuppressWarnings({"rawtypes", "unchecked"})
   public T newInstance(Object ... args) {
		try {
			T t = entityClass.newInstance();
			
			if (args!=null && args.length>0) {
				// set the first argument as text in the name property if one exists
            Property p = new BeanItem<T>(t).getItemProperty("name");
				if (p!=null && String.class.equals(p.getType())) {
					p.setValue(args[0]);
				}
			}
			
			return t;
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
