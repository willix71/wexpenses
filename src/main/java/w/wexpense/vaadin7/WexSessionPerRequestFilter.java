package w.wexpense.vaadin7;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import w.wexpense.service.PersistenceService;

public class WexSessionPerRequestFilter extends OncePerRequestFilter {

	private static final Logger LOGGER = LoggerFactory.getLogger(WexSessionPerRequestFilter.class);
	
	private transient PersistenceService persistenceService;

	@Override
	protected void doFilterInternal(HttpServletRequest request,
			HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {

		PersistenceService service = lookupPersistenceService(request);

		LOGGER.debug("Starting request");
		service.getHelper().requestStart();

		try {
			filterChain.doFilter(request, response);
		}

		finally {
			LOGGER.debug("Ending request");
			service.getHelper().requestEnd();
		}
	}

	protected PersistenceService lookupPersistenceService(
			HttpServletRequest request) {
		if (this.persistenceService == null) {
			LOGGER.info("Fetching PersistenceService");
			
			WebApplicationContext wac = WebApplicationContextUtils
					.getRequiredWebApplicationContext(getServletContext());
			this.persistenceService = wac.getBean("persistenceService", PersistenceService.class);
		}
		return this.persistenceService;
	}
}
