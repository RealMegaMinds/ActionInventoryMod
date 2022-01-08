package megaminds.actioninventory.util;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.RetentionPolicy.SOURCE;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 *  The default values for methods to return if some check fails.<br>
 *  For parameters, there is an overloaded method with out the parameter that calls the original method with the default value.<br>
 *  The default value for fields to hold.
 */
public class Defaults {
	/**
	 * "" by default
	 */
	@Documented
	@Retention(SOURCE)
	@Target({FIELD, METHOD, PARAMETER})
	@interface StringDefault {
		String value() default "";
	}
	
	/**
	 * null by default
	 */
	@Documented
	@Retention(SOURCE)
	@Target({FIELD, METHOD, PARAMETER})
	@interface NullDefault {
	}
	
	/**
	 * new &lt;Type&gt;() or equivalent by default
	 */
	@Documented
	@Retention(SOURCE)
	@Target({FIELD, METHOD, PARAMETER})
	@interface NewDefault {
	}
	
	/**
	 * 0 by default
	 */
	@Documented
	@Retention(SOURCE)
	@Target({FIELD, METHOD, PARAMETER})
	@interface NumberDefault {
		double value() default 0;
	}
	
	/**
	 * false by default
	 */
	@Documented
	@Retention(SOURCE)
	@Target({FIELD, METHOD, PARAMETER})
	@interface BooleanDefault {
		boolean value() default false;
	}
	
	@interface ClassDefault {
		Class<?> value() default Object.class;
	}
}