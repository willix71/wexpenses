package w.wexpense.persistence;

import java.util.Properties;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.orm.jpa.JpaVendorAdapter;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;

@Configuration
public class PersistenceConfiguration {
	
	@Value( "${jdbc.driverClassName}" ) 
	private String driverClassName;
	
	@Value( "${jdbc.url}" ) 
	private String url;
	
	@Value( "${jdbc.username}" ) 
	private String username;
	
	@Value( "${jdbc.password}" ) 
	private String password;
	
	@Value( "${jdbc.jpa.adapter}" ) 
	private String jpaAdapter;
	
	
//	@PersistenceContext(type=PersistenceContextType.EXTENDED)
//	private EntityManager entityManager;

	@Bean
	public LocalContainerEntityManagerFactoryBean getEntityManagerFactoryBean() throws Exception {	
		final LocalContainerEntityManagerFactoryBean factoryBean = new LocalContainerEntityManagerFactoryBean();
		factoryBean.setDataSource( wxDataSource() );
		factoryBean.setPackagesToScan( new String[ ] { "w.wexpense.model" } );
		
		String jpaAdapterClassName = "org.springframework.orm.jpa.vendor." + jpaAdapter + "JpaVendorAdapter";
		@SuppressWarnings("unchecked")
      Class<JpaVendorAdapter> jpaAdapterClass = (Class<JpaVendorAdapter>) Class.forName(jpaAdapterClassName);
		factoryBean.setJpaVendorAdapter( jpaAdapterClass.newInstance() );
		
		Properties jpaProperties = new Properties();
		jpaProperties.load(new ClassPathResource(jpaAdapter + ".properties").getInputStream());
		
		factoryBean.setJpaProperties( jpaProperties);

		return factoryBean;
	}
		
	@Bean
	public DataSource wxDataSource(){
		final DriverManagerDataSource dataSource = new DriverManagerDataSource();
		dataSource.setDriverClassName( driverClassName );
		dataSource.setUsername( username );
		dataSource.setPassword( password );
		dataSource.setUrl( url );
		return dataSource;			
	}
}
