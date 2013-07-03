package w.wexpense.vaadin7.view;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Scope;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Component;

import ru.xpoft.vaadin.VaadinView;
import w.wexpense.model.Expense;
import w.wexpense.model.Template;
import w.wexpense.persistence.dao.ITemplateJpaDao;
import w.wexpense.vaadin7.UIHelper;
import w.wexpense.vaadin7.WexUI;

import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.ui.HorizontalSplitPanel;
import com.vaadin.ui.MenuBar;
import com.vaadin.ui.MenuBar.MenuItem;
import com.vaadin.ui.Panel;
import com.vaadin.ui.Tree;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;

@Component
@Scope("prototype")
@VaadinView(value = AllInOneView.NAME, cached = true)
public class AllInOneView extends Panel implements View {

	public static final String NAME = "allInOne";

	private static final long serialVersionUID = 6406409460600093055L;

	private HorizontalSplitPanel horizontalSplitPanel = new HorizontalSplitPanel();
	private VerticalLayout layout = new VerticalLayout();
	private MenuBar menuBar = new MenuBar();
	private Tree navTree = new Tree();
	
   @Autowired
   private transient ApplicationContext applicationContext;

	@PostConstruct
	public void PostConstruct() {
		addView("currencyListView");
		addView("countryListView");
		addView("cityListView");
		addView("payeeTypeListView");
		addView("payeeListView");
		addView("discriminatorListView");
		addView("accountListView");
		addView("exchangeRateListView");
		addView("expenseTypeListView");
		addView("expenseListView");
		addView("transactionLineListView");
		addView("paymentListView");
		addView("consolidationListView");
		addView("templateListView");
		
		navTree.setSelectable(true);
		navTree.setImmediate(true);
		navTree.setNullSelectionAllowed(false);
		navTree.addValueChangeListener(new Property.ValueChangeListener() {
			private static final long serialVersionUID = 1L;

			@Override
			public void valueChange(ValueChangeEvent event) {
				ListView<?> cv = (ListView<?>) event.getProperty().getValue();
				cv.setSizeFull();
				horizontalSplitPanel.setSecondComponent(cv);
				horizontalSplitPanel.markAsDirty();
			}
		});
		
		horizontalSplitPanel.setSplitPosition(200, HorizontalSplitPanel.Unit.PIXELS);
		horizontalSplitPanel.addComponent(navTree);
		horizontalSplitPanel.setSizeFull();

		menuBar = new MenuBar();
		buildMenu(menuBar);
		menuBar.setWidth("100%");

		layout = new VerticalLayout();
		layout.setSizeFull();
		layout.addComponent(menuBar);
		layout.addComponent(horizontalSplitPanel);
		layout.setExpandRatio(horizontalSplitPanel, 100);

		setContent(layout);
	}

	public Object addView(String name) {
		return addView(applicationContext.getBean(name, ListView.class));
	}
	
	public Object addView(ListView<?> view) {
		Object componentId = navTree.addItem(view);
		navTree.setItemCaption(view, view.getEntityClass().getSimpleName());
		navTree.setChildrenAllowed(view, false);
		return componentId;
	}

	private class TemplateMenu implements MenuBar.Command {
		private Template template;
		public TemplateMenu(Template template) {
			this.template = template;
		}
		@Override
      public void menuSelected(MenuItem selectedItem) {
	      Expense x = template.toExpense();
   		EditorView xeditor = ((WexUI) UI.getCurrent()).getBean(EditorView.class, "expenseEditorView");
   		xeditor.setInstance(x);
   		UIHelper.displayWindow(xeditor);
      }
		
	}
	
	public void buildMenu(MenuBar menuBar) {
		Map<String, MenuItem> menus = new HashMap<String, MenuItem>();
		
		// bypass service
		ITemplateJpaDao dao = applicationContext.getBean(ITemplateJpaDao.class);
		for(Template template : dao.findAll(new Sort(new Sort.Order("templateMenu"), new Sort.Order("templateOrder")))) {
			if (template.getTemplateOrder() == null) continue;
			
			if (template.getTemplateMenu()==null) {
				menuBar.addItem(template.getTemplateName(), new TemplateMenu(template));
			} else {
				MenuItem mnu = menus.get(template.getTemplateMenu());
				if (mnu == null) {					
					// build the menu					
					for(String s: template.getTemplateMenu().split("\\.")) {
						if (mnu == null) {
							mnu = menuBar.addItem(s,null);
						}
						else {
							mnu = mnu.addItem(s, null);
						}
					}
				}
				mnu.addItem(template.getTemplateName(), new TemplateMenu(template));
			}
		}
	}
	
	@Override
	public void enter(ViewChangeEvent event) {
		setSizeFull();
	}
}
