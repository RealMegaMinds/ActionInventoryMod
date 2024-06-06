package megaminds.actioninventory.gui.input;

import java.util.Objects;
import java.util.function.Consumer;

import eu.pb4.sgui.api.ClickType;
import megaminds.actioninventory.gui.BetterGuiI;
import megaminds.actioninventory.gui.callback.BetterClickCallback;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;

public abstract class InputCallback<E> implements BetterClickCallback {
	protected Text question;
	protected InputFinisher<E> onFinish;
	protected E defaultAnswer;

	public InputCallback() {
		question(null);
		onFinish((InputFinisher<E>)null);
		defaultAnswer(null);
	}

	/**
	 * Default answer should remain unchanged.
	 */
	public abstract InputCallback<E> defaultAnswer(E defaultAnswer);
	public abstract InputCallback<E> copy();

	public InputCallback<E> question(Text question) {
		this.question = Objects.requireNonNullElse(question, LiteralText.EMPTY);
		return this;
	}

	/**
	 * Input to onFinish is not null.
	 */
	public InputCallback<E> onFinish(Consumer<E> onFinish) {
		this.onFinish = onFinish==null ? InputFinisher.empty() : (i,t,a,g,e)->onFinish.accept(e);
		return this;
	}

	/**
	 * Input to onFinish is not null.
	 */
	public InputCallback<E> onFinish(InputFinisher<E> onFinish) {
		this.onFinish = Objects.requireNonNullElse(onFinish, InputFinisher.empty());
		return this;
	}

	public interface InputFinisher<E> {
		public static <E> InputFinisher<E> empty() {
			return (i,t,a,g,e)->{};
		}

		/**
		 * index, type, action, and gui should all be the same as the ones passed into cancellingClick
		 */
		void onFinish(int index, ClickType type, SlotActionType action, BetterGuiI gui, E e);
	}
}