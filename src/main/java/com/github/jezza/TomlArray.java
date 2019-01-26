package com.github.jezza;

import java.time.temporal.TemporalAccessor;
import java.time.temporal.TemporalQueries;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Objects;

/**
 * The only types that can be stored in this array are:
 * <p>
 * Boolean | Double | Long | String | TomlArray | TomlTable | TemporalAccessor
 * <p>
 * Attempting to store anything else is undefined behaviour... (Although, chances are nothing different will happen...)
 *
 * @author Jezza
 */
public final class TomlArray implements List<Object> {
	private final List<Object> array;

	public TomlArray() {
		this(new ArrayList<>());
	}

	public TomlArray(int initialCapacity) {
		this(new ArrayList<>(initialCapacity));
	}

	public TomlArray(List<Object> array) {
		this.array = Objects.requireNonNull(array);
	}

	@Override
	public boolean isEmpty() {
		return array.isEmpty();
	}

	@Override
	public boolean contains(Object o) {
		return array.contains(o);
	}

	@Override
	public Iterator<Object> iterator() {
		return array.iterator();
	}

	@Override
	public Object[] toArray() {
		return array.toArray();
	}

	@Override
	public <T> T[] toArray(T[] a) {
		return array.toArray(a);
	}

	@Override
	public int size() {
		return array.size();
	}

	@Override
	public boolean add(Object value) {
		return array.add(value);
	}

	@Override
	public boolean remove(Object o) {
		return array.remove(o);
	}

	@Override
	public boolean containsAll(Collection<?> c) {
		return array.containsAll(c);
	}

	@Override
	public boolean addAll(Collection<?> c) {
		return array.addAll(c);
	}

	@Override
	public boolean addAll(int index, Collection<?> c) {
		return array.addAll(index, c);
	}

	@Override
	public boolean removeAll(Collection<?> c) {
		return array.removeAll(c);
	}

	@Override
	public boolean retainAll(Collection<?> c) {
		return array.retainAll(c);
	}

	@Override
	public void clear() {
		array.clear();
	}

	@Override
	public Object get(int index) {
		return array.get(index);
	}

	@Override
	public Object set(int index, Object element) {
		return array.set(index, element);
	}

	@Override
	public void add(int index, Object element) {
		array.add(index, element);
	}

	@Override
	public Object remove(int index) {
		return array.remove(index);
	}

	@Override
	public int indexOf(Object o) {
		return array.indexOf(o);
	}

	@Override
	public int lastIndexOf(Object o) {
		return array.lastIndexOf(o);
	}

	@Override
	public ListIterator<Object> listIterator() {
		return array.listIterator();
	}

	@Override
	public ListIterator<Object> listIterator(int index) {
		return array.listIterator(index);
	}

	@Override
	public List<Object> subList(int fromIndex, int toIndex) {
		return array.subList(fromIndex, toIndex);
	}

	void write(StringBuilder b, int indent, int increment) {
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
			} else if (value instanceof TemporalAccessor) {
				TemporalAccessor accessor = (TemporalAccessor) value;
				b.append(accessor.query(TemporalQueries.chronology()))
						.append(':').append(accessor.query(TemporalQueries.localDate()))
						.append(':').append(accessor.query(TemporalQueries.localTime()))
						.append(':').append(accessor.query(TemporalQueries.precision()))
						.append(':').append(accessor.query(TemporalQueries.zone()));
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

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}

		TomlArray other = (TomlArray) o;
		return array.equals(other.array);
	}

	@Override
	public int hashCode() {
		return array.hashCode();
	}
}
