package w.wexpense.vaadin.view;

import javax.annotation.PostConstruct;
import javax.persistence.EntityManager;

import w.wexpense.model.DBable;

import com.vaadin.addon.jpacontainer.JPAContainer;

public class OneToManyEditor<T extends DBable, C extends DBable> extends GenericEditor<T> {
	
	private static final long serialVersionUID = -8909856497042109472L;
	
	private OneToManySubEditor<C, T> subEditor;

	public OneToManyEditor(Class<T> entityClass) {
		super(entityClass);
	}

	@PostConstruct
	@Override
	public void buildLayout() {
		buildForm();
		
		addComponent(subEditor);
		setExpandRatio(subEditor, 1);
		
		// build the buttons bar
		addComponent(buildButtons());
	}
	
	@Override
	public void setInstance(JPAContainer<T> jpaContainer) {
		super.setInstance(jpaContainer);
	}
	
	@Override
	public void setInstance(T instance) {
		super.setInstance(instance);
		
		subEditor.setInstance(getItem().getBean());
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
