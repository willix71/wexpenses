package w.wexpense.vaadin.filter;

import com.vaadin.addon.jpacontainer.JPAContainer;
import com.vaadin.ui.Component;

public interface WexFilter extends Component {
	void setJPAContainer(JPAContainer<?> jpaContainer);
}
