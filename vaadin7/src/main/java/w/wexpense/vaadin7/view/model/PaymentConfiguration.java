package w.wexpense.vaadin7.view.model;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

import w.wexpense.model.Expense;
import w.wexpense.model.Payment;
import w.wexpense.vaadin7.action.ActionHelper;
import w.wexpense.vaadin7.support.TableColumnConfig;
import w.wexpense.vaadin7.view.ListView;
import w.wexpense.vaadin7.view.MultiSelectorView;

@Configuration
public class PaymentConfiguration {

//	@Autowired
//	private IPaymentService paymentService;
//	
//	@Bean
//	@Scope("prototype")
//	public EditorView<Payment, Long> paymentEditorView() {
//      OneToManyField<Expense> expenses = new OneToManyField<Expense>(Expense.class, getExpenseTableColumnConfig());
//
//      final EditorView<Payment, Long> editorview = new EditorView<Payment, Long>(paymentService) {
//      	@Override
//         public void initFields() {
//      		super.initFields();
//      		fieldGroup.getField("selectable").setReadOnly(true);
//      	}
//      	
//      	@Override
//         public void initFields(Payment payment) {
//	         super.initFields(payment);
//
//	         fieldGroup.getField("date").setReadOnly(!payment.isSelectable());	         
//	         ((OneToManyField<?>) fieldGroup.getField("expenses")).getActionHandler().setEnabled(payment.isSelectable());
//         }
//      	
//      };
//		editorview.setProperties("fullId", "uid", "date", "filename", "selectable", "expenses");
//		editorview.setCustomField("expenses", expenses);
//		
//		AddMultiSelectionAction<Expense> addSelectionAction = new AddMultiSelectionAction<Expense>("paymentExpenseSelectorView") {
//			@Override
//         public Filter getFilter() {
//				Filter filter = new IsNull("payment");
//				Payment p = editorview.getInstance();
//				if (!p.isNew()) {
//					filter = new Or(filter, new Compare.Equal("payment", p));
//				}
//				return filter;
//         }			
//		};
//		
//		ActionHandler expensesActions = new ActionHandler();
//      expensesActions.addListViewAction(addSelectionAction);
//      expensesActions.addListViewAction(new RemoveAction<Expense>());
//      expenses.setActionHandler(expensesActions);
      
//		EnabalebalMenuBar<Payment> menuBar = editorview.getMenuBar();
//		MenuItem mnuDta = menuBar.addItem("DTA", null);
//		menuBar.addItem(mnuDta, "clear", editorview.NEW_DISABLER, new Command() {
//         @SuppressWarnings("unchecked")
//         public void menuSelected(MenuItem selectedItem) { 
//         	try {
//         		BeanItem<Payment> bp = editorview.getBeanItem();
//         		if (!bp.getBean().isSelectable()) {
//         			bp.getItemProperty("filename").setValue(Payment.DEFAULT_FILENAME);
//         			bp.getItemProperty("selectable").setValue(true);
//         			bp.getItemProperty("date").setValue(null);
//         			
//         			editorview.getField("date").setReadOnly(false);         			
//         			(((OneToManyField<?>) editorview.getField("expenses"))).getActionHandler().setEnabled(true);
//         		}         		
//         	} catch(Exception e) {
//         		throw new RuntimeException(e);
//         	}
//         }
//      });
//		menuBar.addItem(mnuDta, "view", editorview.NEW_DISABLER, new Command() {
//         public void menuSelected(MenuItem selectedItem) { 
//         	try {
//         		Payment p = editorview.getBeanItem().getBean();
//         		if (p.isSelectable()) {
//         			p = generatePaymentDtas(editorview, p);
//         		} 
//         		else {
//         			// need to refresh the instance so as to be able to follow the DTA's relationship
//         			p = paymentService.load(p.getId());
//         		}
//         		
//	         	String html =  PaymentDtaUtils.getDtaLines(p.getDtaLines(), false);
//	         	Label label = new Label(html,ContentMode.PREFORMATTED);
//	   			Window window = new Window();
//	   			window.setContent(label);
//	   			window.center();
//	   			UI.getCurrent().addWindow(window);
//	   			
//         	} catch(Exception e) {
//         		throw new RuntimeException(e);
//         	}
//          }
//      });
//		menuBar.addItem(mnuDta, "save", editorview.NEW_DISABLER, new Command() {
//         public void menuSelected(MenuItem selectedItem) { 
//         	try {
//         		Payment p = editorview.getBeanItem().getBean();
//         		if (p.isSelectable()) {
//         			p = generatePaymentDtas(editorview, p);
//         		}         		
//         		Page.getCurrent().open("download?paymentId=" + p.getId(), "_blank");
//         	} catch(Exception e) {
//         		throw new RuntimeException(e);
//         	}
//         }
//      });
//		return editorview;
//	}

//	private Payment generatePaymentDtas(EditorView<Payment, Long> editorview, Payment p) throws Exception {
//		editorview.getField("expenses").commit();
//		
//		p = paymentService.generatePaymentDtas(p);
//		
//		// this invalidates the payment (why?) so we will have to reload it
//		editorview.fireEntityChange(p);
//		
//		p = paymentService.load(p.getId());
//		
//		Notification.show("Payment DTA generated " + p.getExpenses().size() + " expenses", Notification.Type.TRAY_NOTIFICATION);
//
//		// make sure the editor view is updated with the latest version
//		editorview.setInstance(p);
//		
//		// return the latest version
//		return p;
//	}
	


//	private TableColumnConfig[] getExpenseTableColumnConfig() {
//		return new TableColumnConfig[] {
//				new TableColumnConfig("fullId").collapse().rightAlign(),
//            new TableColumnConfig("uid").collapse(),
//            new TableColumnConfig("createdTs").collapse(),
//            new TableColumnConfig("modifiedTs").collapse(),
//           
//            new TableColumnConfig("date").desc(),
//            new TableColumnConfig("type").centerAlign(),
//            new TableColumnConfig("amount").rightAlign(),
//            new TableColumnConfig("currency").centerAlign(),
//            new TableColumnConfig("payee").expand(1.0f),           
//            new TableColumnConfig("externalReference").collapse(),
//            new TableColumnConfig("description").collapse()
//		};
//	}

	@Bean
	@Scope("prototype")
	public ListView<Payment> paymentListView() {
		ListView<Payment> listview = new ListView<Payment>(Payment.class);
		listview.setColumnConfigs(
				new TableColumnConfig("id").collapse().rightAlign(), 
				new TableColumnConfig("uid").collapse(),
		      new TableColumnConfig("createdTs").collapse(), 
		      new TableColumnConfig("modifiedTs").collapse(),

		      new TableColumnConfig("date").desc(), 
		      new TableColumnConfig("filename"), 
		      new TableColumnConfig("selectable"));

		ActionHelper.setDefaultListViewActions(listview, "paymentEditorView");
		return listview;
	}
	
	@Bean
	@Scope("prototype")
	public MultiSelectorView<Expense> paymentExpenseSelectorView() {
		MultiSelectorView<Expense> selector = new MultiSelectorView<Expense>(Expense.class);
		selector.setColumnConfigs(getExpenseTableColumnConfig());
		return selector;
	}
	
	
	public static TableColumnConfig[] getExpenseTableColumnConfig() {
		return new TableColumnConfig[] {
				new TableColumnConfig("fullId").collapse().rightAlign(),
            new TableColumnConfig("uid").collapse(),
            new TableColumnConfig("createdTs").collapse(),
            new TableColumnConfig("modifiedTs").collapse(),
           
            new TableColumnConfig("date").desc(),
            new TableColumnConfig("type").centerAlign(),
            new TableColumnConfig("amount").rightAlign(),
            new TableColumnConfig("currency").centerAlign(),
            new TableColumnConfig("payee").expand(1.0f),           
            new TableColumnConfig("externalReference").collapse(),
            new TableColumnConfig("description").collapse()
		};
	}
}
