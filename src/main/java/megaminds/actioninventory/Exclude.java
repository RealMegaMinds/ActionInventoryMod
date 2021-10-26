package megaminds.actioninventory;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.google.gson.annotations.Expose;

/**
 * This acts the opposite of {@link Expose}
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface Exclude {
	  /**
	   * If {@code true}, the field marked with this annotation is skipped while serializing.
	   * If {@code false}, the field marked with this annotation is serialized.
	   * Defaults to {@code true}.
	   */
	  public boolean serialize() default true;

	  /**
	   * If {@code true}, the field marked with this annotation is skipped during deserialization. 
	   * If {@code false}, the field marked with this annotation is deserialized from the JSON.
	   * Defaults to {@code true}.
	   */
	  public boolean deserialize() default true;
}