package w.wexpense.vaadin;

import java.util.List;

import javax.persistence.EntityManagerFactory;

import org.springframework.beans.factory.annotation.Autowired;

import w.wexpense.persistence.DatabasePopulator;

import com.vaadin.Application;
import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.ui.Component;
import com.vaadin.ui.HorizontalSplitPanel;
import com.vaadin.ui.Tree;
import com.vaadin.ui.Window;

public class WexApplication extends Application {

	private static final long serialVersionUID = 4614856229857072876L;

	@Autowired
	private EntityManagerFactory entityManagerFactory;
	
	@Autowired
	private List<GenericViewFactory<?>> views;
	
	@Override
	public void init() {
		WexpenseMainView mainView = new WexpenseMainView();
		
		for(GenericViewFactory<?> c: views) {
			mainView.addView(c);
		}

		setMainWindow(mainView);
	}

	class WexpenseMainView extends Window {
      private static final long serialVersionUID = 6406409460600093055L;

      Tree navTree = new Tree();
		
		public WexpenseMainView() {

			final HorizontalSplitPanel horizontalSplitPanel = new HorizontalSplitPanel();		
			navTree.addListener(new Property.ValueChangeListener() {
				@Override
				public void valueChange(ValueChangeEvent event) {
					GenericViewFactory<?>.GenericView cv = (GenericViewFactory<?>.GenericView) event.getProperty().getValue();
					cv.refreshContainer();
					horizontalSplitPanel.setSecondComponent(cv);
				}
			});
			
			navTree.setSelectable(true);
			navTree.setImmediate(true);
			navTree.setNullSelectionAllowed(false);

			horizontalSplitPanel.setSplitPosition(200, HorizontalSplitPanel.UNITS_PIXELS);
			horizontalSplitPanel.addComponent(navTree);

			setContent(horizontalSplitPanel);
		}

		public Object addView(GenericViewFactory<?> view) {
			Component c = view.newInstance();
			Object componentId = navTree.addItem(c);
			navTree.setItemCaption(c, c.getCaption());
			if (view.getSubViews() == null) {
				navTree.setChildrenAllowed(c, false);
			} else {
				navTree.setChildrenAllowed(c, true);
				for(GenericViewFactory<?> subview: view.getSubViews()) {
					Object subId = addView(subview);
					navTree.setParent(subId, componentId);					
				}
			}
			return componentId;
		}
	}
}