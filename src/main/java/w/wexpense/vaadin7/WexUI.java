package w.wexpense.vaadin7;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import ru.xpoft.vaadin.DiscoveryNavigator;
import w.wexpense.vaadin7.converter.WexConverterFactory;

import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinSession;
import com.vaadin.ui.Notification;
import com.vaadin.ui.UI;

@Component
@Scope("prototype")
public class WexUI extends UI {

	protected final Logger LOGGER = LoggerFactory.getLogger(getClass());

   @Autowired
   private transient ApplicationContext applicationContext;

	@Override
	protected void init(VaadinRequest request) {
		LOGGER.debug("{} inited", getClass().getSimpleName() );
		
		setSizeFull();

		VaadinSession.getCurrent().setConverterFactory(new WexConverterFactory());
		
      DiscoveryNavigator navigator = new DiscoveryNavigator(this, this);
      navigator.navigateTo(UI.getCurrent().getPage().getUriFragment());

      Notification.show("Welcome");
	}
	
	public <T> T getBean(Class<T> type) {
		return applicationContext.getBean(type);
	}
	public <T> T getBean(Class<T> type, String name) {
		if (name==null || name.length()==0) {
			return applicationContext.getBean(type);
		} else {
			return applicationContext.getBean(name, type);
		}
	}
}
