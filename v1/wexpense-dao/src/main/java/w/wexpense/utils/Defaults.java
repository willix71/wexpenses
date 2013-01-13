package w.wexpense.utils;

import java.util.Date;
import java.util.UUID;

public class Defaults {
	
	public static String newUid()
	{
		return UUID.randomUUID().toString();
	}
	
	public static Date newCreatedTs()
	{
		return new Date();
	}
}
