package megaminds.actioninventory.util.annotations;

import static java.lang.annotation.ElementType.LOCAL_VARIABLE;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * Types annotated with this should have a static field {@code INSTANCE} of the type the class, which is the single instance of the class.<br>
 * When deserialized by Gson, {@code INSTANCE} is returned.
 */
@Documented
@Retention(RUNTIME)
@Target({ TYPE, METHOD, LOCAL_VARIABLE })
public @interface Instanced {
}