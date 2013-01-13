package w.wexpense.persistence.config;

import java.util.Properties;

import javax.annotation.Resource;
import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.dao.annotation.PersistenceExceptionTranslationPostProcessor;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
/** <tx:annotation-driven /> **/
@EnableTransactionManagement
/** <context:component-scan base-package="w.wexpense.persistence" /> */
@ComponentScan( { "w.wexpense.persistence" } )
public class PersistenceJPAConfig{

	@Value( "${jdbc.driverClassName}" ) 
	private String driverClassName;
	
	@Value( "${jdbc.url}" ) 
	private String url;
	
	@Value( "${jdbc.username}" ) 
	private String username;
	
	@Value( "${jdbc.password}" ) 
	private String password;
	
	@Resource(name="hibernateProperties")
	private Properties hibernateProperties;

	public PersistenceJPAConfig(){
		super();
	}

	/**
		<bean id="entityManagerFactory" class="org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean">			
			<property name="dataSource" ref="wxDataSource" />				
			<property name="jpaVendorAdapter">
				<bean class="org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter" />
			</property>	
			<property name="packagesToScan" value="w.wexpense.model" />
			<property name="jpaPropertyMap" >
				<util:properties location="classpath:persistence-test.properties" />
			</property>
		</bean>	
	 */
	@Bean
	public LocalContainerEntityManagerFactoryBean entityManagerFactoryBean(){
		final LocalContainerEntityManagerFactoryBean factoryBean = new LocalContainerEntityManagerFactoryBean();
		factoryBean.setDataSource( wxDataSource() );
		factoryBean.setJpaVendorAdapter( new HibernateJpaVendorAdapter() );
		factoryBean.setPackagesToScan( new String[ ] { "w.wexpense.model" } );
		factoryBean.setJpaProperties( hibernateProperties);

		return factoryBean;
	}
	
	/**
	 	<bean id="wxDataSource" class="org.springframework.jdbc.datasource.DriverManagerDataSource" >
		<property name="driverClassName" value="${jdbc.driverClassName}" />
		<property name="username" value="${jdbc.username}" />
		<property name="password" value="${jdbc.password}" />
		<property name="url" value="${jdbc.url}" />	
	 */
	@Bean
	public DataSource wxDataSource(){
		final DriverManagerDataSource dataSource = new DriverManagerDataSource();
		dataSource.setDriverClassName( driverClassName );
		dataSource.setUsername( username );
		dataSource.setPassword( password );
		dataSource.setUrl( url );
		return dataSource;			
	}

	/**	
		<bean id="transactionManager" class="org.springframework.orm.jpa.JpaTransactionManager">
			<property name="entityManagerFactory" ref="entityManagerFactory" />		
		</bean>
	 */
	@Bean
	public JpaTransactionManager transactionManager(){
		final JpaTransactionManager transactionManager = new JpaTransactionManager();
		transactionManager.setEntityManagerFactory( entityManagerFactoryBean().getObject() );
		return transactionManager;
	}

	/**
	 	<bean class="org.springframework.dao.annotation.PersistenceExceptionTranslationPostProcessor" />
	 */
	@Bean
	public PersistenceExceptionTranslationPostProcessor persistenceExceptionTranslationPostProcessor(){
		return new PersistenceExceptionTranslationPostProcessor();
	}
}
