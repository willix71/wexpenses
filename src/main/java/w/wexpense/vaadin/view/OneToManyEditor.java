package w.wexpense.vaadin.view;

import javax.annotation.PostConstruct;
import javax.persistence.EntityManager;

import com.vaadin.addon.jpacontainer.JPAContainer;

import w.wexpense.model.DBable;
import w.wexpense.model.Expense;
import w.wexpense.model.TransactionLine;

public class OneToManyEditor<T extends DBable, C extends DBable> extends GenericEditor<T> {
	
	private OneToManySubEditor<C, T> subEditor;

	public OneToManyEditor(Class<T> entityClass) {
		super(entityClass);
	}

	@PostConstruct
	@Override
	public void buildLayout() {
		buildForm();
		addComponent(subEditor);
		buildButtons();
	}
	
	@Override
	public void setInstance(T instance, JPAContainer<T> jpaContainer) {
		super.setInstance(instance, jpaContainer);
		
		subEditor.setInstance(getItem().getBean(), getJpaContainer());
	}
		
	@Override
	protected T insert(EntityManager em) {
		T t = super.insert(em);
		subEditor.save(t, em);
		return t;
	}

	@Override
	protected T update(EntityManager em) {
		T t = super.update(em);
		subEditor.save(t, em);
		return t;
	}

	public OneToManySubEditor<C, T> getSubEditor() {
		return subEditor;
	}

	public void setSubEditor(OneToManySubEditor<C, T> subEditor) {
		this.subEditor = subEditor;
	}
}
