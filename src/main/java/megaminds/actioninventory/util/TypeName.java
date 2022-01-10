package megaminds.actioninventory.util;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import megaminds.actioninventory.serialization.PolymorphicTypeAdapterFactory;

@Documented
@Retention(RUNTIME)
@Target(TYPE)
/**
 * Uses to set custom name for types using {@link PolymorphicTypeAdapterFactory}
 */
public @interface TypeName {
	String value();
}