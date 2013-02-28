package w.wexpense.vaadin7;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import ru.xpoft.vaadin.DiscoveryNavigator;

import com.vaadin.server.VaadinRequest;
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

      DiscoveryNavigator navigator = new DiscoveryNavigator(this, this);
      navigator.navigateTo(UI.getCurrent().getPage().getUriFragment());

      Notification.show("Welcome");
	}

}
