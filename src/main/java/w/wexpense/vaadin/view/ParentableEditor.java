package w.wexpense.vaadin.view;

import w.wexpense.model.Parentable;

public class ParentableEditor<T extends Parentable<T>> extends GenericEditor<T> {

	private static final long serialVersionUID = 756482738963654180L;

	public ParentableEditor(Class<T> entityClass) {
		super(entityClass);
	}
	
	protected T save() {
		T a = super.save();
		// This a hack to avoid the following bug
		// Create and save a new Account (parentable) the parent of which 
		// has not yet displayed its children in the hierarchical view.
		// open the children -> we get a 
		// 	org.hibernate.LazyInitializationException: could not initialize proxy - no Session
		// and the application fails totally (a server restart is needed)
		// Not quite sure why, full stack trace below.		
		for(T child: a.getParent().getChildren()) {
			child.toString();
		}
		return a;
	}

}

/*
2013-01-25 12:03:54,624 DEBUG | org.springframework.orm.jpa.support.OpenEntityManagerInViewInterceptor | Opening JPA EntityManager in OpenEntityManagerInViewInterceptor 
2013-01-25 12:03:54,624 DEBUG | w.wexpense.vaadin.WexApplication | Request started with EntityManagerPerRequestHelper 
Jan 25, 2013 12:03:54 PM com.vaadin.Application terminalError
SEVERE: Terminal error:
java.lang.IllegalArgumentException: Cannot access the property value
	at com.vaadin.addon.jpacontainer.metadata.ClassMetadata.getPropertyValue(ClassMetadata.java:170)
	at com.vaadin.addon.jpacontainer.metadata.ClassMetadata.getPropertyValue(ClassMetadata.java:340)
	at com.vaadin.addon.jpacontainer.JPAContainer.getParent(JPAContainer.java:1417)
	at com.vaadin.addon.jpacontainer.JPAContainer.isRoot(JPAContainer.java:1430)
	at com.vaadin.ui.TreeTable$AbstractStrategy.getDepth(TreeTable.java:100)
	at com.vaadin.ui.TreeTable.paintRowAttributes(TreeTable.java:365)
	at com.vaadin.ui.Table.paintRowAttributes(Table.java:3434)
	at com.vaadin.ui.Table.paintRow(Table.java:3322)
	at com.vaadin.ui.Table.paintPartialRowAdditions(Table.java:2877)
	at com.vaadin.ui.Table.paintPartialRowUpdate(Table.java:2816)
	at com.vaadin.ui.Table.paintContent(Table.java:2758)
	at com.vaadin.ui.TreeTable.paintContent(TreeTable.java:470)
	at com.vaadin.ui.AbstractComponent.paint(AbstractComponent.java:781)
	at com.vaadin.terminal.gwt.server.AbstractCommunicationManager.writeUidlResponce(AbstractCommunicationManager.java:1044)
	at com.vaadin.terminal.gwt.server.AbstractCommunicationManager.paintAfterVariableChanges(AbstractCommunicationManager.java:925)
	at com.vaadin.terminal.gwt.server.AbstractCommunicationManager.doHandleUidlRequest(AbstractCommunicationManager.java:792)
	at com.vaadin.terminal.gwt.server.CommunicationManager.handleUidlRequest(CommunicationManager.java:323)
	at com.vaadin.terminal.gwt.server.AbstractApplicationServlet.service(AbstractApplicationServlet.java:501)
	at javax.servlet.http.HttpServlet.service(HttpServlet.java:722)
	at org.springframework.web.servlet.mvc.ServletWrappingController.handleRequestInternal(ServletWrappingController.java:159)
	at org.springframework.web.servlet.mvc.AbstractController.handleRequest(AbstractController.java:153)
	at org.springframework.web.servlet.mvc.SimpleControllerHandlerAdapter.handle(SimpleControllerHandlerAdapter.java:48)
	at org.springframework.web.servlet.DispatcherServlet.doDispatch(DispatcherServlet.java:900)
	at org.springframework.web.servlet.DispatcherServlet.doService(DispatcherServlet.java:827)
	at org.springframework.web.servlet.FrameworkServlet.processRequest(FrameworkServlet.java:882)
	at org.springframework.web.servlet.FrameworkServlet.doPost(FrameworkServlet.java:789)
	at javax.servlet.http.HttpServlet.service(HttpServlet.java:641)
	at javax.servlet.http.HttpServlet.service(HttpServlet.java:722)
	at org.apache.catalina.core.ApplicationFilterChain.internalDoFilter(ApplicationFilterChain.java:305)
	at org.apache.catalina.core.ApplicationFilterChain.doFilter(ApplicationFilterChain.java:210)
	at org.apache.catalina.core.StandardWrapperValve.invoke(StandardWrapperValve.java:225)
	at org.apache.catalina.core.StandardContextValve.invoke(StandardContextValve.java:123)
	at org.apache.catalina.authenticator.AuthenticatorBase.invoke(AuthenticatorBase.java:472)
	at org.apache.catalina.core.StandardHostValve.invoke(StandardHostValve.java:168)
	at org.apache.catalina.valves.ErrorReportValve.invoke(ErrorReportValve.java:98)
	at org.apache.catalina.valves.AccessLogValve.invoke(AccessLogValve.java:927)
	at org.apache.catalina.core.StandardEngineValve.invoke(StandardEngineValve.java:118)
	at org.apache.catalina.connector.CoyoteAdapter.service(CoyoteAdapter.java:407)
	at org.apache.coyote.http11.AbstractHttp11Processor.process(AbstractHttp11Processor.java:1001)
	at org.apache.coyote.AbstractProtocol$AbstractConnectionHandler.process(AbstractProtocol.java:585)
	at org.apache.tomcat.util.net.JIoEndpoint$SocketProcessor.run(JIoEndpoint.java:312)
	at java.util.concurrent.ThreadPoolExecutor.runWorker(ThreadPoolExecutor.java:1110)
	at java.util.concurrent.ThreadPoolExecutor$Worker.run(ThreadPoolExecutor.java:603)
	at java.lang.Thread.run(Thread.java:722)
Caused by: java.lang.reflect.InvocationTargetException
	at sun.reflect.GeneratedMethodAccessor31.invoke(Unknown Source)
	at sun.reflect.DelegatingMethodAccessorImpl.invoke(DelegatingMethodAccessorImpl.java:43)
	at java.lang.reflect.Method.invoke(Method.java:601)
	at com.vaadin.addon.jpacontainer.metadata.ClassMetadata.getPropertyValueFromField(ClassMetadata.java:198)
	at com.vaadin.addon.jpacontainer.metadata.ClassMetadata.getPropertyValue(ClassMetadata.java:162)
	... 43 more
Caused by: org.hibernate.LazyInitializationException: could not initialize proxy - no Session
	at org.hibernate.proxy.AbstractLazyInitializer.initialize(AbstractLazyInitializer.java:149)
	at org.hibernate.proxy.AbstractLazyInitializer.getImplementation(AbstractLazyInitializer.java:195)
	at org.hibernate.proxy.pojo.javassist.JavassistLazyInitializer.invoke(JavassistLazyInitializer.java:185)
	at w.wexpense.model.Account_$$_javassist_0.getId(Account_$$_javassist_0.java)
	... 48 more

2013-01-25 12:03:54,858 DEBUG | w.wexpense.vaadin.WexApplication | Request ended with EntityManagerPerRequestHelper 
2013-01-25 12:03:55,108 DEBUG | org.springframework.orm.jpa.support.OpenEntityManagerInViewInterceptor | Closing JPA EntityManager in OpenEntityManagerInViewInterceptor 
*/