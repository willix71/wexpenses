package w.wexpense.vaadin;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.ui.ModelMap;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.context.request.WebRequestInterceptor;

import com.vaadin.addon.jpacontainer.util.EntityManagerPerRequestHelper;

@Component
public class WexSessionPerRequestInterceptor implements WebRequestInterceptor {
	
	private final Logger LOGGER = LoggerFactory.getLogger(getClass());
	
	@Autowired
	private WexJPAContainerFactory jpaContainerFactory;
	
	@Override
	public void preHandle(WebRequest request) throws Exception {
		EntityManagerPerRequestHelper helper = jpaContainerFactory.getHelper();		
		if (helper != null) {
			LOGGER.debug("Request started with EntityManagerPerRequestHelper");
			helper.requestStart();
		} else {
			LOGGER.debug("Request started");
		}

	}

	@Override
	public void postHandle(WebRequest request, ModelMap model) throws Exception {
		// TODO Auto-generated method stub

	}

	@Override
	public void afterCompletion(WebRequest request, Exception ex) throws Exception {
		EntityManagerPerRequestHelper helper = jpaContainerFactory.getHelper();
		if (helper != null) {
			LOGGER.debug("Request ended with EntityManagerPerRequestHelper");
			helper.requestEnd();
		}  else {
			LOGGER.debug("Request ended");
		}
	}

}
