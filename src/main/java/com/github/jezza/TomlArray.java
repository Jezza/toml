package com.github.jezza;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Jezza
 */
public final class TomlArray {
	private final List<Object> array;

	public TomlArray() {
		this(new ArrayList<>(0));
	}

	public TomlArray(List<Object> array) {
		this.array = array;
	}

	public boolean isEmpty() {
		return array.isEmpty();
	}

	public int size() {
		return array.size();
	}

	public void add(Object value) {
		array.add(value);
	}

	public Object get(int index) {
		return array.get(index);
	}

	public void write(StringBuilder b, int indent, int increment) {
		if (array.isEmpty()) {
			b.append("[]");
			return;
		}
		b.append("[\n");

		for (Object value : array) {
			b.append(" ".repeat(indent * increment));
			if (value instanceof TomlTable) {
				((TomlTable) value).write(b, indent + increment, increment);
			} else if (value instanceof TomlArray) {
				((TomlArray) value).write(b, indent + increment, increment);
			} else if (value instanceof String) {
				b.append('"').append(value).append('"');
			} else {
				b.append(value);
			}
			b.append(',').append('\n');
		}
		int v = (indent - increment) * increment;
		if (v > 0) {
			b.append(" ".repeat(v));
		}
		b.append(']');
	}

	@Override
	public String toString() {
		StringBuilder b = new StringBuilder("TomlArray ");
		write(b, 2, 2);
		return b.toString();
	}
}
