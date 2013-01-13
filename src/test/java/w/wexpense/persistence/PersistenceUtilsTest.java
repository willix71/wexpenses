package w.wexpense.persistence;

import org.junit.Assert;
import org.junit.Test;

import w.wexpense.model.City;
import w.wexpense.model.Currency;
import w.wexpense.model.DBable;
import w.wexpense.persistence.PersistenceUtils;


public class PersistenceUtilsTest {

	@Test
	public void test() {
		Assert.assertEquals("code", PersistenceUtils.getIdName(Currency.class));
		Assert.assertEquals("id", PersistenceUtils.getIdName(DBable.class));
		Assert.assertEquals("id", PersistenceUtils.getIdName(City.class));
	}
}
