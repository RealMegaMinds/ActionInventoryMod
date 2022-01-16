package megaminds.actioninventory.util.annotations;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import megaminds.actioninventory.serialization.PolyAdapterFactory;

@Documented
@Retention(RUNTIME)
@Target(TYPE)
public @interface PolyName {
	/**
	 * Used to set custom name for types using {@link PolyAdapterFactory}.
	 * Default is Class.getSimpleName().
	 */
	String value();
}