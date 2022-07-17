package megaminds.actioninventory.gui;

import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.UnaryOperator;

import eu.pb4.sgui.api.gui.AnvilInputGui;
import megaminds.actioninventory.gui.callback.BetterClickCallback;
import megaminds.actioninventory.util.ElementHelper;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;

public class BetterAnvilGui extends AnvilInputGui implements BetterGuiI {
	private Identifier id;
	/**
	 * Note, this is set on open.
	 */
	private BetterGuiI previousGui;
	private boolean chained;
	/**
	 * Happens every character.
	 */
	private UnaryOperator<String> filter;
	/**
	 * Happens before finishing.
	 */
	private UnaryOperator<String> postFilter;
	private Consumer<String> onFinish;

	public BetterAnvilGui(ServerPlayerEntity player, boolean includePlayerInventorySlots) {
		super(player, includePlayerInventorySlots);
		setSlot(1, ElementHelper.getCancel(null));
		setSlot(2, ElementHelper.getConfirm(CONFIRM));
	}

	private static final BetterClickCallback CONFIRM = (i,t,a,g)->{
		if (g instanceof BetterAnvilGui v) {
			if (v.doFilter(v.getInput(), v.postFilter)) {
				if (v.onFinish!=null) v.onFinish.accept(v.getInput());
			} else {
				return true;
			}
		}
		return false;
	};

	@Override
	public void onInput(String input) {
		doFilter(input, filter);
	}

	/**
	 * True if input passed the filter.
	 */
	private boolean doFilter(String input, UnaryOperator<String> filter) {
		String filtered;
		if (filter!=null) {
			filtered = filter.apply(input);
			if (!input.equals(filtered)) {
				setDefaultInputValue(filtered);
				return false;
			}
		}
		return true;
	}

	@Override
	public boolean reOpen() {
		if (isOpen()) sendGui();
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

	public Identifier getId() {
		return id;
	}

	public void setId(Identifier id) {
		this.id = id;
	}

	public BetterGuiI getPreviousGui() {
		return previousGui;
	}

	public boolean isChained() {
		return chained;
	}

	public void setChained(boolean chained) {
		this.chained = chained;
	}

	public Function<String, String> getFilter() {
		return filter;
	}

	public void setFilter(UnaryOperator<String> filter) {
		this.filter = filter;
	}

	public Function<String, String> getPostFilter() {
		return postFilter;
	}

	public void setPostFilter(UnaryOperator<String> postFilter) {
		this.postFilter = postFilter;
	}

	public Consumer<String> getOnFinish() {
		return onFinish;
	}

	public void setOnFinish(Consumer<String> onFinish) {
		this.onFinish = onFinish;
	}
}