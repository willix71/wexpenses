package w.wexpense.persistence;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import w.wexpense.model.Expense;
import w.wexpense.model.Payment;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations={"classpath:persistence-test-context.xml"})
public class PersistenceTest {

	@PersistenceContext
	private EntityManager entityManager;

	@Test
	@Transactional
	public void testSetup() {
		Assert.assertNotNull(entityManager);
		
		Query q = entityManager.createQuery("from Currency");		
		Assert.assertEquals(4, q.getResultList().size());
	}
	
	@Test
	@Transactional
	public void testOrderBy() {
		Query q = entityManager.createQuery("from Payment");
		Payment p = (Payment) q.getResultList().get(0);
		System.out.println(p);
		
		System.out.println(p.getExpenses());
		System.out.println(p.getDtaLines());
	}
	
	@Test
	@Transactional
	public void testOrderByEntity() {
		Query q = entityManager.createQuery("from Expense");
		Expense x = (Expense) q.getResultList().get(0);
		System.out.println(x);
		
		System.out.println(x.getTransactions());
	}
}
