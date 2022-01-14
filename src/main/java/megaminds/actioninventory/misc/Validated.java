package megaminds.actioninventory.misc;

import java.util.function.Supplier;

import com.google.gson.annotations.JsonAdapter;

import megaminds.actioninventory.serialization.ValidatedAdapterFactory;

@JsonAdapter(ValidatedAdapterFactory.class)
public interface Validated {
	/**
	 * b==true else throws an error
	 */
	public static void validate(boolean b, String message) {
		if (!b) throw new IllegalArgumentException(message);
	}
	
	/**
	 * b==true else throws an error
	 */
	public static void validate(boolean b, Supplier<String> message) {
		if (!b) throw new IllegalArgumentException(message.get());
	}
	
	/**
	 * Called after an object of this type is loaded by Gson. <br>
	 * Implementors should correct any fields needing it and call other initializing methods.<br>
	 * Implementors may throw IllegalArgumentException but should try to correct mistakes/use defaults if they can.
	 */
	void validate();	
}