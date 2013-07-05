package w.wexpense.vaadin.view;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;

import w.wexpense.vaadin.SelectionChangeEvent;

import com.vaadin.addon.jpacontainer.EntityItem;
import com.vaadin.event.ItemClickEvent;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.HorizontalLayout;

public class GenericSelector<T> extends GenericView<T> {

	private static final long serialVersionUID = -6576677905097142401L;
	
	public GenericSelector(Class<T> entityClass) {
		super(entityClass);
	}

	public void buildButtonBar() {
		HorizontalLayout buttonLayout = new HorizontalLayout();
		buttonLayout.addComponent(new Button("Select", new Button.ClickListener() {
			private static final long serialVersionUID = -1;			
			@Override
			public void buttonClick(ClickEvent event) {
				LOGGER.debug("Selection is {}", table.getValue());
				fireEvent(new SelectionChangeEvent(GenericSelector.this, table.getValue()));
				close();
			}
		}));
		buttonLayout.addComponent(getCloseButton());
		addComponent(buttonLayout);
	}
	
	@PostConstruct
	public void build() {
		super.build();
		buildButtonBar();
	}
	
	@Override
	public void entitySelected(ItemClickEvent event) {
		LOGGER.debug("Selection is {}", event.getItemId());
		fireEvent(new SelectionChangeEvent(GenericSelector.this, event.getItemId()));
		close();
	}
}
