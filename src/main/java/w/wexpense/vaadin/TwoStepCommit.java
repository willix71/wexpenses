package w.wexpense.vaadin;


public interface TwoStepCommit<T> {

	void postCommit(T item);
}
