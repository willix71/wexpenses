package w.wexpense.vaadin;

import com.vaadin.addon.jpacontainer.EntityItem;

public interface TwoStepCommit<T> {

	void postCommit(EntityItem<T> item);
}
