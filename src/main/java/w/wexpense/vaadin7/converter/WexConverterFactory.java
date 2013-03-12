package w.wexpense.vaadin7.converter;

import java.math.BigDecimal;
import java.util.Date;

import com.vaadin.data.util.converter.Converter;
import com.vaadin.data.util.converter.DefaultConverterFactory;

public class WexConverterFactory extends DefaultConverterFactory {

	@Override
   protected Converter<String, ?> createStringConverter(Class<?> sourceType) {
      if (BigDecimal.class.isAssignableFrom(sourceType)) {
          return new StringToBigDecimalConverter();
      } else if (Date.class.isAssignableFrom(sourceType)) {
          return new StringToDateConverter();
      } else {
          return super.createStringConverter(sourceType);
      }
  };

}
