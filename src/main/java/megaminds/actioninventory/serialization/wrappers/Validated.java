package megaminds.actioninventory.serialization.wrappers;

import java.util.function.Consumer;

import org.jetbrains.annotations.MustBeInvokedByOverriders;
import org.jetbrains.annotations.NotNull;

public interface Validated {
	/**
	 * Validates this object. May default unset fields.
	 * <br>
	 * Errors must be reported to {@code errorReporter}. If errorReporter receives no messages, it is assumed there was no error in validation.
	 * @implNote
	 * The maximum amount of errors should be given. For example, if several fields hold incorrect values, print a message for each one, not just the first.
	 */
	@MustBeInvokedByOverriders
	void validate(@NotNull Consumer<String> errorReporter);
}