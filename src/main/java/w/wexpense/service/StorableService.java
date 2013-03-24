package w.wexpense.service;

import java.io.Serializable;

public interface StorableService<T, ID extends Serializable> {

	T newInstance(Object ... args);

	T load(ID id);

	T save(T entity);

	void delete(T entity);

	void delete(ID id);

}