package megaminds.actioninventory.util.annotations;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import megaminds.actioninventory.serialization.PolyAdapterFactory;

/**
 * Signals that a type uses {@link PolyAdapterFactory}
 */
@Documented
@Retention(RUNTIME)
@Target(TYPE)
@Inherited
public @interface Poly {
}