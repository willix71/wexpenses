package w.wexpense.vaadin;

import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import w.wexpense.vaadin.view.GenericEditor;
import w.wexpense.vaadin.view.GenericView;

import com.vaadin.Application;
import com.vaadin.addon.jpacontainer.util.EntityManagerPerRequestHelper;
import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.terminal.gwt.server.HttpServletRequestListener;
import com.vaadin.ui.HorizontalSplitPanel;
import com.vaadin.ui.Tree;
import com.vaadin.ui.Window;

public class WexApplication extends Application implements HttpServletRequestListener {

	private static final long serialVersionUID = 4614856229857072876L;
		
	private static final Logger LOGGER = LoggerFactory.getLogger(WexApplication.class);
	
	@Resource()
	@Qualifier("views")
	private List<GenericView<?>> views;
	
	@Autowired
	private WexJPAContainerFactory jpaContainerFactory;
	
	@Override
	public void init() {
		LOGGER.debug(">>>>>>>>>> init");
		
		WexpenseMainView mainView = new WexpenseMainView();
		
		for(GenericView<?> c: views) {
			mainView.addView(c);
		}

		setMainWindow(mainView);
		
		LOGGER.debug("<<<<<<<<<< inited");
	}
	
	

	@Override
	public void onRequestStart(HttpServletRequest request, HttpServletResponse response) {
		EntityManagerPerRequestHelper helper = jpaContainerFactory.getHelper();		
		if (helper != null) {
			LOGGER.debug("Request started with EntityManagerPerRequestHelper");
			helper.requestStart();
		} else {
			LOGGER.debug("Request started");
		}
	}

	@Override
	public void onRequestEnd(HttpServletRequest request, HttpServletResponse response) {
		EntityManagerPerRequestHelper helper = jpaContainerFactory.getHelper();
		if (helper != null) {
			LOGGER.debug("Request ended with EntityManagerPerRequestHelper");
			helper.requestEnd();
		}  else {
			LOGGER.debug("Request ended");
		}
	}

	public GenericEditor<?> getEditorFor(Class<?> entityClass) {
		for(GenericView<?> c: views) {
			if (c.getEntityClass().equals(entityClass)) {
				return c.newEditor();
			}
		}
		return null;
	}
	
	class WexpenseMainView extends Window {
      private static final long serialVersionUID = 6406409460600093055L;

      Tree navTree = new Tree();
		
		public WexpenseMainView() {

			final HorizontalSplitPanel horizontalSplitPanel = new HorizontalSplitPanel();		
			navTree.addListener(new Property.ValueChangeListener() {
				private static final long serialVersionUID = 1L;

				@Override
				public void valueChange(ValueChangeEvent event) {
					GenericView<?> cv = (GenericView<?>) event.getProperty().getValue();
					cv.refreshContainer(false);
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

		public Object addView(GenericView<?> view) {
			Object componentId = navTree.addItem(view);
			navTree.setItemCaption(view, view.getTitle());
			navTree.setChildrenAllowed(view, false);
			return componentId;
		}
	}
}