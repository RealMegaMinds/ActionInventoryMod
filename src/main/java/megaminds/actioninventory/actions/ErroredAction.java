package megaminds.actioninventory.actions;

import java.util.Collections;
import java.util.List;
import lombok.Getter;
import megaminds.actioninventory.ActionInventoryMod;
import net.minecraft.util.Identifier;

@Getter
public class ErroredAction extends EmptyAction {
	private final List<String> errors;

	public ErroredAction(List<String> errors) {
		this.errors = Collections.unmodifiableList(errors);
	}

	public ErroredAction(String error) {
		this.errors = List.of(error);
	}

	@Override
	public Identifier getType() {
		return new Identifier(ActionInventoryMod.MOD_ID, "Errored");
	}
}