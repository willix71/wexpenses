package w.wexpense.vaadin.view.model;

import java.math.BigDecimal;
import java.text.DecimalFormat;

import w.wexpense.model.Consolidation;
import w.wexpense.model.TransactionLine;
import w.wexpense.model.enums.TransactionLineEnum;
import w.wexpense.utils.TransactionLineUtils;
import w.wexpense.vaadin.view.OneToManySubEditor;
import w.wexpense.vaadin.view.WexTable;

import com.vaadin.data.Property;

public class ConsolidationTransactionLineEditor extends OneToManySubEditor<TransactionLine, Consolidation> {

	private BigDecimal[] totals;
	
	public ConsolidationTransactionLineEditor() {
	   super(TransactionLine.class, Consolidation.class, "transactions");
   }

	protected void buildTable() {
		table = new WexTable(childContainer, propertyConfiguror) {
			@Override 
			protected Object getPropertyValue(Object rowId, Object colId, Property property) {
				// we only want the consolidatedDate to be editable
				if ("consolidatedDate".equals(colId)) {
					return super.getPropertyValue(rowId, colId, property);
				} else {
					return formatPropertyValue(rowId, colId, property);
				}
			}
		};
		table.setSizeFull();
		table.setSelectable(true);
		table.setImmediate(true);
		table.addListener(this);
		table.addActionHandler(this);

		table.setPageLength(0);	// might want to set a page length
		table.setEditable(true);		
		table.setFooterVisible(true);
		table.setTableFieldFactory(fieldFactory);				
		
		addComponent(table);
		setExpandRatio(table, 1);
		setSizeFull();
	}
	
	private void displayTotal(boolean repaint) {
		DecimalFormat formater = new DecimalFormat("0.00");
		table.setColumnFooter("inValue", formater.format(totals[0]));
		table.setColumnFooter("outValue", formater.format(totals[1]));
		if (repaint) {
			table.requestRepaintAll();
		}
	}
	
	@Override
	public void setInstance(Consolidation conso) {
		super.setInstance(conso);
		
		totals = TransactionLineUtils.getInAndOutTotal(conso.getTransactions());
		
		displayTotal(false);
	}
	
	@Override
	public void addChildEntity(TransactionLine line) {
		super.addChildEntity(line);
		if (line.getConsolidatedDate() == null) {
			line.setConsolidatedDate(line.getExpense().getDate());
		}
		
		if (TransactionLineEnum.IN.equals(line.getFactor())) {
			totals[0] = totals[0].add(line.getValue());
		} else if (TransactionLineEnum.OUT.equals(line.getFactor())) {
			totals[1] = totals[1].add(line.getValue());
		}
		
		displayTotal(true);
	}
	
	@Override
	public void removeEntity(Object target) {
		super.removeEntity(target);
		
		TransactionLine line = (TransactionLine) target;
		
		if (TransactionLineEnum.IN.equals(line.getFactor())) {
			totals[0] = totals[0].subtract(line.getValue());
		} else if (TransactionLineEnum.OUT.equals(line.getFactor())) {
			totals[1] = totals[1].subtract(line.getValue());
		}
		
		displayTotal(true);
	}
	
}
