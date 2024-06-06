package megaminds.actioninventory.gui.input;

import java.util.Objects;
import java.util.function.Function;

public abstract class FilteredInput<E> extends InputCallback<E> {
	protected Function<E, E> filter;
	protected Function<E, E> postFilter;

	public FilteredInput() {
		filter = s->s;
		postFilter = s->s;
	}

	public FilteredInput<E> filter(Function<E, E> filter) {
		this.filter = Objects.requireNonNullElse(filter, s->s);
		return this;
	}

	public FilteredInput<E> postFilter(Function<E, E> postFilter) {
		this.postFilter = Objects.requireNonNullElse(postFilter, s->s);
		return this;
	}
}