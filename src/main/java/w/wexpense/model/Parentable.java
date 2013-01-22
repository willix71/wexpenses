package w.wexpense.model;

public interface Parentable<T> {

	T getParent();
	
	void setParent(T t);
}
