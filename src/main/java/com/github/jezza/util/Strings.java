package com.github.jezza.util;

import java.util.Arrays;
import java.util.List;

/**
 * @author Jezza
 */
public final class Strings {
	private static final String OBJECT_REP = "{}";
	// Don't just replace this field with OBJECT_REP_CHAR.length(), as we're trying to get javac to fold the constant into the places we use it.
	private static final int OBJECT_REP_LENGTH = 2;

	private Strings() {
		throw new IllegalStateException();
	}

	public static boolean useable(CharSequence input) {
		return input != null
				&& input.length() > 0
				&& input.chars().anyMatch(i -> i > ' ');
	}

	/**
	 * Replaces occurrences of "{}" in the original string with
	 * String-representations of the given objects.
	 * <br>
	 * <i>NOTE: This method is very cheap to call and is highly optimized.</i>
	 *
	 * @param input   - The format string.
	 * @param objects - The arguments that the format string refers to.
	 * @return A formatted string.
	 */
	public static String format(String input, Object... objects) {
		if (input == null) {
			return null;
		}
		int length = input.length();
		if (length == 0) {
			return "";
		}
		if (objects == null || objects.length == 0) {
			return input;
		}
		int start = input.indexOf(OBJECT_REP);
		if (start == -1) {
			return input;
		}
		StringBuilder b = new StringBuilder(input.length());
		int index = 0;
		int end = 0;
		do {
			if (end < start) {
				b.append(input, end, start);
			}
			end = start + OBJECT_REP_LENGTH;
			b.append(objects[index++]);
		} while (index < objects.length && (start = input.indexOf(OBJECT_REP, end)) != -1);
		if (end < length) {
			b.append(input, end, length);
		}
		return b.toString();
	}

	public static List<String> split(String input) {
		if (input.isEmpty()) {
			return List.of();
		}
		int index = input.indexOf('.');
		if (index == -1) {
			return List.of(input);
		}
		String[] segments = {
				input.substring(0, index)
		};
		int mark;
		while ((index = input.indexOf('.', mark = index + 1)) != -1) {
			String segment = input.substring(mark, index);
			int length = segments.length;
			segments = Arrays.copyOf(segments, length + 1);
			segments[length] = segment;
		}
		if (mark != input.length()) {
			int length = segments.length;
			segments = Arrays.copyOf(segments, length + 1);
			segments[length] = input.substring(mark);
		}
		return List.of(segments);
	}
}