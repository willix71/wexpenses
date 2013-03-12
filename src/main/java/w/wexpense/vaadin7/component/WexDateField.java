package w.wexpense.vaadin7.component;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vaadin.data.util.converter.Converter;
import com.vaadin.shared.ui.datefield.Resolution;
import com.vaadin.ui.DateField;

public class WexDateField extends DateField {

	private static final long serialVersionUID = 8561115907318437297L;
	
	private static final Logger LOGGER = LoggerFactory.getLogger(WexDateField.class);	

	public WexDateField() {
		setResolution(Resolution.DAY);
		setDateFormat("dd.MM.yyyy");
	}
	
	@Override
	protected Date handleUnparsableDateString(String dateString)
			throws Converter.ConversionException {

		return getEasyDate(dateString);
	}
	
	public static Date getEasyDate(String dateString) {
		if (dateString == null || dateString.length()==0) {
			LOGGER.debug("Can not parse empty date");
			throw new Converter.ConversionException("invalid date (empty)");
		}
		
		Calendar now = Calendar.getInstance();
		int hour = 0;
		int minute = 0;
		int second = 0;
		String[] parts = dateString.split(" ");
		if (parts.length>1) {
			try {
				String fields[] = parts[1].split("[:.]");
				if (fields.length >0) {
					hour = Integer.parseInt(fields[0]);
					if (fields.length >1) {
						minute = Integer.parseInt(fields[1]);
						if (fields.length >2) {
							second = Integer.parseInt(fields[2]);
						}
					}
				}
			} catch (NumberFormatException e) {
				LOGGER.debug("Can not parse number for time", e);
				throw new Converter.ConversionException("invalid time " + parts[1]);
			}
		}

		String fields[] = parts.length==0 ? new String[]{}: parts[0].split("[/.-]");
		try {
			int d = fields.length > 0 && fields[0].length()>0 ? Integer.parseInt(fields[0]) : now.get(Calendar.DAY_OF_MONTH);
			int m = fields.length > 1 && fields[1].length()>0 ? Integer.parseInt(fields[1]) - 1 : now.get(Calendar.MONTH);
			int y = fields.length > 2 && fields[2].length()>0 ? Integer.parseInt(fields[2]) : now.get(Calendar.YEAR);
			GregorianCalendar c = new GregorianCalendar(y, m, d, hour, minute, second);
			c.set(Calendar.MILLISECOND,0);
			return c.getTime();
		} catch (NumberFormatException e) {
			LOGGER.debug("Can not parse number for date", e);
			throw new Converter.ConversionException("invalid date " + parts[0]);
		}
	}
}
