package megaminds.actioninventory.gui.input;

import java.util.Objects;
import java.util.regex.Pattern;

public class IntInput extends NumberInputCallback<Integer> {
	private static final Pattern NON_NUM = Pattern.compile("\\D");
	private static final String INT_MAX = Integer.MAX_VALUE+"";

	@Override
	public InputCallback<Integer> copy() {
		return new IntInput()
				.filter(filter)
				.postFilter(postFilter)
				.question(question)
				.defaultAnswer(defaultAnswer)
				.onFinish(onFinish);
	}

	@Override
	protected Integer strToNum(String s) {
		if (s==null || s.isBlank()) return 0;
		s = NON_NUM.matcher(s).replaceAll("");
		if (s.isBlank()) return 0;

		if (s.length() >= 10) {
			StringBuilder b = new StringBuilder(s);
			b.setLength(10);
			for (int i = 0, l = b.length(); i < l; i++) {
				char c = INT_MAX.charAt(i);
				if (b.charAt(i)<c) break;
				b.setCharAt(i, c);
			}
			s = b.toString();
		}
		return Integer.parseInt(s);
	}

	@Override
	public NumberInputCallback<Integer> boundedPostFilter(Integer min, Integer max) {
		postFilter(i -> i<min ? min : i>max ? max : i);
		return this;
	}

	@Override
	public InputCallback<Integer> defaultAnswer(Integer defaultAnswer) {
		this.defaultAnswer = Objects.requireNonNullElse(defaultAnswer, 0);
		return this;
	}
}