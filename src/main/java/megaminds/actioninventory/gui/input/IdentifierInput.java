package megaminds.actioninventory.gui.input;

import java.util.function.Function;
import java.util.regex.Pattern;

import eu.pb4.sgui.api.ClickType;
import megaminds.actioninventory.gui.BetterAnvilGui;
import megaminds.actioninventory.gui.BetterGuiI;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.util.Identifier;

public class IdentifierInput extends FilteredInput<Identifier>  {
	private static final Pattern INVALID_NAMESPACE = Pattern.compile("[^a-z0-9_\\.\\-]");
	private static final Pattern INVALID_PATH = Pattern.compile("[^a-z0-9_\\.\\-/]");
	private static final Function<String, String> ID_FILTER = s->{
		var i = s.indexOf(Identifier.NAMESPACE_SEPARATOR);
		if (i==-1) {
			//ex: "path" -> "path"
			//ex: ""     -> ""
			return INVALID_PATH.matcher(s).replaceAll("");
		} else if (i==0) {
			//ex: ":path" -> "path"
			return INVALID_PATH.matcher(s.substring(1)).replaceAll("");
		} else if (i<s.length()-1) {
			//ex: "name:path" -> "name:path"
			return INVALID_NAMESPACE.matcher(s.substring(0, i)).replaceAll("")
					+ Identifier.NAMESPACE_SEPARATOR
					+ INVALID_PATH.matcher(s.substring(i+1)).replaceAll("");				
		} else {
			//ex: "name:" -> "name:"
			return INVALID_NAMESPACE.matcher(s.substring(0, i)).replaceAll("")
					+ Identifier.NAMESPACE_SEPARATOR;
		}
	};

	private static final Function<String, Identifier> ID_POST_FILTER = s->{
		var i = s.indexOf(Identifier.NAMESPACE_SEPARATOR);
		if (s.isEmpty() || i==s.length()-1) s += "path";
		return new Identifier(s);
	};

	@Deprecated
	@Override
	public FilteredInput<Identifier> filter(Function<Identifier, Identifier> filter) {
		if (filter==null) return this;
		throw new UnsupportedOperationException("filter() is unsupported for IdentifierInput");
	}

	@Override
	public InputCallback<Identifier> copy() {
		return new IdentifierInput()
				.filter(filter)
				.postFilter(postFilter)
				.question(question)
				.defaultAnswer(defaultAnswer)
				.onFinish(onFinish);
	}

	@Override
	public boolean cancellingClick(int index, ClickType type, SlotActionType action, BetterGuiI gui) {
		BetterAnvilGui input = new BetterAnvilGui(gui.getPlayer(), false);
		input.setDefaultInputValue(defaultAnswer==null?"":defaultAnswer.toString());
		input.setTitle(question);
		input.setChained(true);
		input.setFilter(ID_FILTER);
		input.setPostFilter(s->postFilter.apply(ID_POST_FILTER.apply(s)).toString());
		input.setOnFinish(s->onFinish.onFinish(index, type, action, gui, ID_POST_FILTER.apply(s)));

		input.open(gui);
		return false;
	}

	@Override
	public InputCallback<Identifier> defaultAnswer(Identifier defaultAnswer) {
		this.defaultAnswer = defaultAnswer;
		return this;
	}
}