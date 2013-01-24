package w.wexpense.vaadin.fieldfactory;

import com.vaadin.addon.jpacontainer.JPAContainer;
import com.vaadin.addon.jpacontainer.fieldfactory.SingleSelectTranslator;
import com.vaadin.ui.AbstractSelect;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.NativeSelect;

public class WexComboBox extends ComboBox {

	public WexComboBox(JPAContainer<?> comboContainer) {
		setMultiSelect(false);
		setContainerDataSource(comboContainer);
		setPropertyDataSource(new SingleSelectTranslator(this));
		setItemCaptionMode(NativeSelect.ITEM_CAPTION_MODE_ITEM);
		setFilteringMode(AbstractSelect.Filtering.FILTERINGMODE_CONTAINS);
		setSizeFull();
	}
}
