package com.github.jezza.util;

import java.util.LinkedList;
import java.util.List;
import java.util.function.Function;

/**
 * @author Jezza
 */
public final class Strings {
	//	private static final char[] OBJ_REP_CHARS = "{}".toCharArray();
	private static final String[] EMPTY = new String[0];

	private static final String OBJECT_REP = "{}";
	private static final char[] OBJECT_REP_CHAR = OBJECT_REP.toCharArray();
	// Don't just replace this field with OBJECT_REP_CHAR.length(), as we're trying to get javac to fold the constant into the places we use it.
	private static final int OBJECT_REP_LENGTH = 2;

	static {
		if (OBJECT_REP_LENGTH != OBJECT_REP_CHAR.length) {
			throw new IllegalStateException("Update the OBJECT_REP_LENGTH field to reflect the char array inside of OBJECT_REP_CHAR");
		}
	}

	private Strings() {
		throw new IllegalStateException();
	}

	public static boolean useable(CharSequence charSequence) {
		if (charSequence == null || charSequence.length() == 0)
			return false;
		for (int i = 0; i < charSequence.length(); i++)
			if (charSequence.charAt(i) > ' ')
				return true;
		return false;
	}

	public static String lastPart(String input) {
		int index = input.lastIndexOf('.');
		return index >= 0 ? input.substring(index + 1) : input;
	}

	/**
	 * Replaces occurrences of "{}" in the original string with
	 * String-representations of the given objects.<br>
	 * <br>
	 * <i>NOTE: This method is very cheap to call and is highly optimized.
	 */
	public static String format2(String input, Object... objects) {
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

	/**
	 * Replaces occurrences of "{}" in the original string with String-representations of the given objects.<br>
	 * <br>
	 * <i>NOTE: This method is very cheap to call and is highly optimized.</i>
	 */
	public static String format(String original, Object... objects) {
		if (original == null)
			return null;
		int length = original.length();
		if (length == 0)
			return "";
		if (objects == null || objects.length == 0)
			return original;

		int objectLength = objects.length;
		int calculatedLength = length;
		String[] params = new String[objectLength];
		while (--objectLength >= 0) {
			Object obj = objects[objectLength];
			if (obj != null) {
				String param = obj.toString();
				calculatedLength += param.length();
				params[objectLength] = param;
			} else {
				calculatedLength += 4;
				params[objectLength] = "null";
			}
		}
		objectLength = objects.length;

		char[] rep = OBJECT_REP_CHAR;
		char[] result = new char[calculatedLength];
		char[] param;
		original.getChars(0, length, result, 0);
		for (int i = 0, index = 0, end, paramLength; i < objectLength; i++) {
			index = indexOf(result, 0, length, rep, 0, OBJECT_REP_LENGTH, index);
			if (index < 0) {
				return new String(result, 0, length);
			}
			end = index + OBJECT_REP_LENGTH;
			if (end > length)
				end = length;
			param = params[i].toCharArray();
			paramLength = param.length;
			// Shifts the entire result array down to fit the parameter.
			System.arraycopy(result, end, result, index + paramLength, length - end);
			// Copys the parameter into the result array.
			System.arraycopy(param, 0, result, index, paramLength);
			// The new length of the used characters.
			length = length + paramLength - (end - index);
			// Moves the index to AFTER the parameter we just inserted.
			index += paramLength;
		}
		return new String(result, 0, length);
	}

	/**
	 * Performs an indexOf search on a char array, with another char array.
	 * Think of it as lining up the two arrays, and returning the index that it matches.
	 * Or just think of it as an indexOf...
	 *
	 * @param source    - the characters being searched.
	 * @param target    - the characters being searched for.
	 * @param fromIndex - the index to begin searching from.
	 * @return - the index that the target array was found at within the source array
	 */
	public static int indexOf(char[] source, char[] target, int fromIndex) {
		return indexOf(source, 0, source.length, target, 0, target.length, fromIndex);
	}

	/**
	 * Performs an indexOf search on a char array, with another char array.
	 * Think of it as lining up the two arrays, and returning the index that it matches.
	 *
	 * @param source       - the characters being searched.
	 * @param sourceOffset - offset of the source string.
	 * @param sourceCount  - count of the source string.
	 * @param target       - the characters being searched for.
	 * @param targetOffset - offset of the target string.
	 * @param targetCount  - count of the target string.
	 * @param fromIndex    - the index to begin searching from.
	 * @return - the index that the target array was found at within the source array
	 */
	public static int indexOf(char[] source, int sourceOffset, int sourceCount, char[] target, int targetOffset, int targetCount, int fromIndex) {
		if (fromIndex >= sourceCount)
			return targetCount == 0 ? sourceCount : -1;
		if (fromIndex < 0)
			fromIndex = 0;
		if (targetCount == 0)
			return fromIndex;

		char first = target[targetOffset];
		int max = sourceOffset + sourceCount - targetCount;

		for (int i = sourceOffset + fromIndex; i <= max; i++) {
			// Look for first character.
			if (source[i] != first)
				while (++i <= max && source[i] != first) ;

			// Found first character, now look at the rest of v2
			if (i <= max) {
				int j = i + 1;
				int end = j + targetCount - 1;
				for (int k = targetOffset + 1; j < end && source[j] == target[k]; j++, k++) ;

				if (j == end)
					return i - sourceOffset; // Found whole string.
			}
		}
		return -1;
	}

	/**
	 * Formats a chunk of text, replacing defined tokens by the start and end, and passes the value off to the given function.
	 * <p>
	 * eg:
	 * <pre>
	 *  String result = formatKey("This is a {hello}", "{", "}", k -> Integer.toString(k.length()));
	 * </pre>
	 * result = "This is a 5"
	 * <p>
	 * <pre>
	 *  String[] values = {"First", "Second"};
	 *  String result = formatKey("This is a [0][1]", "[", "]", k -> values[Integer.valueOf(k)]);
	 * </pre>
	 * result = "This is a FirstSecond"
	 *
	 * @param input     - The text to scan and alter
	 * @param startKey  - The series of characters that start the token
	 * @param endKey    - The series of characters that end the token
	 * @param transform - The function to apply to the value found between the startKey and the endKey
	 * @return - The formatted result.
	 */
	public static String formatToken(String input, String startKey, String endKey, Function<String, String> transform) {
		int startKeyLength = startKey.length();
		int endKeyLength = endKey.length();

		StringBuilder output = new StringBuilder(input.length());
		int start;
		int end = -endKeyLength;
		int diff;

		while (true) {
			end += endKeyLength;
			start = input.indexOf(startKey, end);
			if (start < 0)
				break;
			diff = start - end;
			if (diff > 0) {
				if (diff == 1) {
					output.append(input.charAt(end));
				} else {
					output.append(input.substring(end, start));
				}
			}
			if (start + 1 >= input.length())
				throw new IllegalArgumentException("Unmatched token @ position: " + start);
			end = input.indexOf(endKey, start + startKeyLength);
			if (end < 0)
				throw new IllegalArgumentException("Unmatched token @ position: " + start);
			diff = end - start - startKeyLength;
			if (diff > 0) {
				if (diff > 1) {
					output.append(transform.apply(input.substring(start + startKeyLength, end)));
				} else {
					output.append(transform.apply(String.valueOf(input.charAt(start + startKeyLength))));
				}
			} else {
				output.append(transform.apply(""));
			}
		}

		if (-1 < end && end < input.length())
			output.append(input.substring(end));
		return output.toString();
	}

	public static String[] split(String target, String on) {
		if (target == null || target.isEmpty())
			return EMPTY;
		int start = target.indexOf(on);
		if (start < 0)
			return new String[]{target};
		List<String> results = new LinkedList<>();
		if (start > 0)
			results.add(trimSubstring(target, 0, start));
		int onLength = on.length();
		int last = start + onLength;
		while ((start = target.indexOf(on, last)) >= 0) {
			int diff = start - last;
			if (diff > 0) {
				if (diff == 1) {
					char c = target.charAt(last);
					if (!Character.isWhitespace(c))
						results.add(String.valueOf(c));
				} else {
					results.add(trimSubstring(target, last, start));
				}
			}
			last = start + onLength;
		}
		results.add(trimSubstring(target, last, target.length()));
		return results.toArray(EMPTY);
	}

	public static String trimSubstring(String target, int start) {
		return trimSubstring(target, start, target.length());
	}

	public static String trimSubstring(String target, int start, int end) {
		if (start < 0)
			throw new StringIndexOutOfBoundsException(start);

		int diff = end - start;
		if (diff < 0)
			throw new StringIndexOutOfBoundsException(diff);
		if (diff == 1) {
			char c = target.charAt(start);
			return Character.isWhitespace(c) ? "" : String.valueOf(c);
		}

		char[] val = target.toCharArray();
		int length = val.length;
		if (end > length)
			throw new StringIndexOutOfBoundsException(end);

		while (start < end && val[start] <= ' ')
			start++;
		while (start < end && val[end - 1] <= ' ')
			end--;

		diff = end - start;
		if (diff < 0)
			throw new StringIndexOutOfBoundsException(diff);
		return (start > 0 || end < length) ? new String(val, start, diff) : target;
	}
}