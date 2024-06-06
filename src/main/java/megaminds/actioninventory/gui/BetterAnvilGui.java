package megaminds.actioninventory.gui;

import java.util.function.Consumer;
import java.util.function.Function;

import eu.pb4.sgui.api.gui.AnvilInputGui;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import megaminds.actioninventory.gui.callback.BetterAnvilCallback;
import megaminds.actioninventory.util.ElementHelper;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;

@Getter
@Setter
public class BetterAnvilGui extends AnvilInputGui implements BetterGuiI {
	private Identifier id;
	/**
	 * Note, this is set on open.
	 */
	@Setter(AccessLevel.NONE) private BetterGuiI previousGui;
	private boolean chained;

	private Function<String, String> filter;
	private Function<String, String> postFilter;
	private Consumer<String> onFinish;

	public BetterAnvilGui(ServerPlayerEntity player, boolean includePlayerInventorySlots) {
		super(player, includePlayerInventorySlots);
		setSlot(1, ElementHelper.getCancel());
		setSlot(2, ElementHelper.getConfirm(CONFIRM));
	}

	private static final BetterAnvilCallback CONFIRM = (i,t,a,g) -> {
		String filtered = g.doFilter(true);
		if (filtered!=null) {
			g.onFinish.accept(filtered);
			return false;
		} else {
			return true;
		}
	};

	@Override
	public void onInput(String input) {
		doFilter(false);
	}

	@Override
	public boolean reOpen() {
		if (isOpen()) return sendGui();
		return false;
	}

	@Override
	public boolean open(BetterGuiI previous) {
		previousGui = previous;
		return open();
	}

	@Override
	public void clearPrevious() {
		previousGui = null;
	}

	private String doFilter(boolean post) {
		String input = getInput();
		String filtered = (post ? postFilter : filter).apply(input).toString();
		if (input.equals(filtered)) {
			return filtered;
		} else {
			setDefaultInputValue(filtered);
			sendGui();
			return null;
		}
	}
}