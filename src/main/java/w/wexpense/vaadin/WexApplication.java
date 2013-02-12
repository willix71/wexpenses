package w.wexpense.vaadin;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import w.wexpense.model.Expense;
import w.wexpense.service.ExpenseService;
import w.wexpense.vaadin.view.GenericEditor;
import w.wexpense.vaadin.view.GenericView;

import com.vaadin.Application;
import com.vaadin.addon.jpacontainer.util.EntityManagerPerRequestHelper;
import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.terminal.gwt.server.HttpServletRequestListener;
import com.vaadin.ui.HorizontalSplitPanel;
import com.vaadin.ui.MenuBar;
import com.vaadin.ui.MenuBar.Command;
import com.vaadin.ui.MenuBar.MenuItem;
import com.vaadin.ui.Tree;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;

public class WexApplication extends Application implements HttpServletRequestListener {

	private static final long serialVersionUID = 4614856229857072876L;
		
	private static final Logger LOGGER = LoggerFactory.getLogger(WexApplication.class);
	
	@Resource
	@Qualifier("views")
	private List<GenericView<?>> views;

	@Resource
	@Qualifier("todaysExpenseView")
	private GenericView<Expense> todaysExpenseView;
	
	@Resource
	private ExpenseService expenseService;
	
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
      HorizontalSplitPanel horizontalSplitPanel = new HorizontalSplitPanel();
      
		public WexpenseMainView() { 		
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

			MenuBar menuBar = new MenuBar();
			menuBar.setWidth("100%");
			buildMenu(menuBar);
			
			VerticalLayout vl = new VerticalLayout();
			vl.setSizeFull();
			vl.addComponent(menuBar);
			vl.addComponent(horizontalSplitPanel);
			horizontalSplitPanel.setSizeFull();
			vl.setExpandRatio(horizontalSplitPanel, 100);
			setContent(vl);
		}

		public Object addView(GenericView<?> view) {
			Object componentId = navTree.addItem(view);
			navTree.setItemCaption(view, view.getTitle());
			navTree.setChildrenAllowed(view, false);
			return componentId;
		}
	
	
		public void buildMenu(MenuBar menubar) {
	      
	      menubar.addItem("BVR", new Command() {
	         public void menuSelected(MenuItem selectedItem) { 
	         	Expense expense = expenseService.newBvrExpense();
	         	todaysExpenseView.addEntity(expense, WexApplication.this);         	
	         }
	      });
	
	      menubar.addItem("BVO", new Command() {
	         public void menuSelected(MenuItem selectedItem) { 
	         	Expense expense = expenseService.newBvoExpense();
	         	todaysExpenseView.addEntity(expense, WexApplication.this);         	
	         }
	      });
	     
	      menubar.addItem("BVI", new Command() {
	         public void menuSelected(MenuItem selectedItem) { 
	         	Expense expense = expenseService.newBviExpense();
	         	todaysExpenseView.addEntity(expense, WexApplication.this);         	
	         }
	      });
	      
	      menubar.addItem("cash", new Command() {
	         public void menuSelected(MenuItem selectedItem) { 
	         	Expense expense = expenseService.newCashExpense();
	         	todaysExpenseView.addEntity(expense, WexApplication.this);         	
	         }
	      });
	
	      menubar.addItem("debit", new Command() {
	         public void menuSelected(MenuItem selectedItem) { 
	         	Expense expense = expenseService.newDebitExpense();
	         	todaysExpenseView.addEntity(expense, WexApplication.this);         	
	         }
	      });
	      
	      menubar.addItem("credit", new Command() {
	         public void menuSelected(MenuItem selectedItem) { 
	         	Expense expense = expenseService.newCreditExpense();
	         	todaysExpenseView.addEntity(expense, WexApplication.this);         	
	         }
	      }); 
	      
	      menubar.addItem("today", new Command() {
	         public void menuSelected(MenuItem selectedItem) { 
	         	todaysExpenseView.refreshContainer(false);	         	
	         	horizontalSplitPanel.setSecondComponent(todaysExpenseView);        	
	         }
	      });    
	  }
	}

}