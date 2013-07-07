package w.wexpense.vaadin7.converter;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import com.vaadin.data.util.converter.Converter;

public class StringToDateConverter implements Converter<String, Date> {

	private DateFormat df = new SimpleDateFormat("dd.MM.yyyy");
	@Override
   public Date convertToModel(String value, Locale locale) throws com.vaadin.data.util.converter.Converter.ConversionException {
		if (value == null) return null;
	   
		try {
	   	return df.parse(value);
	   } catch (ParseException e) {
	   	throw new ConversionException(e);
	   }
   }

	@Override
   public String convertToPresentation(Date value, Locale locale) throws com.vaadin.data.util.converter.Converter.ConversionException {
	   if (value == null) return null;
	   else return df.format(value);
   }

	@Override
   public Class<Date> getModelType() {
	   return Date.class;
   }

	@Override
   public Class<String> getPresentationType() {
	   return String.class;
   }
}
