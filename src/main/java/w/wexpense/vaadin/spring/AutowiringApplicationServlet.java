package w.wexpense.vaadin.spring;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import com.vaadin.Application;
import com.vaadin.terminal.gwt.server.ApplicationServlet;

/**
 * {@link ApplicationServlet} that autowires and configures the
 * {@link Application} objects it creates using the containing Spring
 * {@link WebApplicationContext}.
 * 
 * <p>
 * When using this servlet, annotations such as
 * <code>@{@link org.springframework.beans.factory.annotation.Autowired Autowired}</code>
 * and
 * <code>@{@link org.springframework.beans.factory.annotation.Required Required}</code>
 * and interfaces such as
 * {@link org.springframework.beans.factory.BeanFactoryAware BeanFactoryAware},
 * etc. will work on your {@link Application} instances.
 * </p>
 * 
 * <p>
 * An example: <blockquote>
 * 
 * <pre>
 *  &lt;bean id="applicationServlet" class="org.springframework.web.servlet.mvc.ServletWrappingController"
 *     p:servletClass="com.example.AutowiringApplicationServlet"&gt;
 *      &lt;property name="initParameters"&gt;
 *          &lt;props&gt;
 *              &lt;prop key="application"&gt;some.spring.configured.Application&lt;/prop&gt;
 *              &lt;prop key="productionMode"&gt;true&lt;/prop&gt;
 *          &lt;/props&gt;
 *      &lt;/property&gt;
 *  &lt;/bean&gt;
 * </pre>
 * 
 * </blockquote>
 * 
 * @see org.springframework.web.servlet.mvc.ServletWrappingController
 * @see AutowireCapableBeanFactory
 */
public class AutowiringApplicationServlet extends ApplicationServlet {

   private static final long serialVersionUID = -2925875079218143751L;

   protected final Logger log = LoggerFactory.getLogger(getClass());

   private WebApplicationContext webApplicationContext;

   /**
    * Initialize this servlet.
    * 
    * @throws ServletException
    *            if there is no {@link WebApplicationContext} associated with
    *            this servlet's context
    */
   @Override
   public void init(ServletConfig config) throws ServletException {
      super.init(config);
      log.debug("finding containing WebApplicationContext");
      try {
         this.webApplicationContext = WebApplicationContextUtils.getRequiredWebApplicationContext(config
               .getServletContext());
      } catch (IllegalStateException e) {
         throw new ServletException("could not locate containing WebApplicationContext");
      }
   }

   /**
    * Get the containing Spring {@link WebApplicationContext}. This only works
    * after the servlet has been initialized (via {@link #init init()}).
    * 
    * @throws ServletException
    *            if the operation fails
    */
   protected final WebApplicationContext getWebApplicationContext() throws ServletException {
      if (this.webApplicationContext == null)
         throw new ServletException("can't retrieve WebApplicationContext before init() is invoked");
      return this.webApplicationContext;
   }

   /**
    * Get the {@link AutowireCapableBeanFactory} associated with the containing
    * Spring {@link WebApplicationContext}. This only works after the servlet
    * has been initialized (via {@link #init init()}).
    * 
    * @throws ServletException
    *            if the operation fails
    */
   protected final AutowireCapableBeanFactory getAutowireCapableBeanFactory() throws ServletException {
      try {
         return getWebApplicationContext().getAutowireCapableBeanFactory();
      } catch (IllegalStateException e) {
         throw new ServletException("containing context " + getWebApplicationContext() + " is not autowire-capable", e);
      }
   }

   /**
    * Create and configure a new instance of the configured application class.
    * 
    * <p>
    * The implementation in {@link AutowiringApplicationServlet} delegates to
    * {@link #getAutowireCapableBeanFactory getAutowireCapableBeanFactory()},
    * then invokes {@link AutowireCapableBeanFactory#createBean
    * AutowireCapableBeanFactory.createBean()} using the configured
    * {@link Application} class.
    * </p>
    * 
    * @param request
    *           the triggering {@link HttpServletRequest}
    * @throws ServletException
    *            if creation or autowiring fails
    */
   @Override
   protected Application getNewApplication(HttpServletRequest request) throws ServletException {

      AutowireCapableBeanFactory beanFactory = getAutowireCapableBeanFactory();
      try {
         Class<? extends Application> cl = getApplicationClass();
         log.debug("creating new instance of " + cl);
         Application app = beanFactory.createBean(cl); //, AutowireCapableBeanFactory.AUTOWIRE_BY_TYPE, true);
         beanFactory.autowireBean(app);
         return app;
      } catch (ClassNotFoundException e) {
         throw new ServletException("failed to create new instance of application", e);
      } catch (BeansException e) {
         throw new ServletException("failed to create new instance of application", e);
      }
   }
}
