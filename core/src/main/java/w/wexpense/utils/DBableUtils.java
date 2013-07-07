package w.wexpense.utils;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import w.wexpense.model.DBable;

public class DBableUtils {

	public static Collection<String> getUids(List entities) {
		Set<String> uids = new HashSet<String>();
		for(Object o : entities) {
			uids.add(((DBable) o).getUid());
		}
		return uids;
	}

}
