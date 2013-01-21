package w.wexpense.vaadin.view;

import w.wexpense.vaadin.PropertyConfiguror;

public class ParentableView<T> extends GenericView<T> {

	private static final long serialVersionUID = 6499289439725418193L;

	public ParentableView(Class<T> entityClass) {
		super(entityClass);
	}
	
	@Override
	protected void initTable() {
		this.jpaContainer = jpaContainerFactory.getJPAContainer(entityClass, propertyConfiguror.getPropertyValues(PropertyConfiguror.nestedProperties));
		this.jpaContainer.setParentProperty("parent");
		
		this.table = new WexTreeTable(jpaContainer, propertyConfiguror);
	}
}
