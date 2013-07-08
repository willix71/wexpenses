package w.wexpense.validation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import javax.validation.Constraint;
import javax.validation.Payload;

@Target({ElementType.METHOD,ElementType.FIELD,ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy=TransactionValidator.class)
public @interface Transactionized {

	/**
	 * Can be overriden with a custom message by using curly brackers
	 * i.e.: "{w.wexpense.validation.transaction.total}"
	 */
	String message() default "Invalid transaction lines";
	
	Class<?>[] groups() default {};
	
	Class<? extends Payload>[] payload() default {};
}
