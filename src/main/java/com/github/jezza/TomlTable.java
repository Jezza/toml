package com.github.jezza;

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Function;

/**
 * @author Jezza
 */
public final class TomlTable {
	private final Map<String, Object> table;

	public TomlTable() {
		this(new LinkedHashMap<>());
	}

	public TomlTable(Map<String, Object> table) {
		this.table = table;
	}

	public boolean isEmpty() {
		return table.isEmpty();
	}

	public int size() {
		return table.size();
	}

	public boolean containsKey(List<String> key) {
		return locate(false, key, (t, k) -> t.table.containsKey(k));
	}

//	@Override
//	public boolean containsValue(Object value) {
//		// @TODO Jezza - 26 Jan. 2019: I'm too fucking lazy to implement this right now...
//		throw new UnsupportedOperationException();
//	}

	public Object get(List<String> key) {
		return locate(false, key, (t, k) -> t.table.get(k));
	}

	public Object put(List<String> key, Object value) {
		return locate(true, key, (table, k) -> table.table.put(k, value));
	}

	public Object remove(List<String> key) {
		return locate(false, key, (t, k) -> t.table.remove(k));
	}

	public Object getOrDefault(Object key, Object defaultValue) {
		return null;
	}

	public void forEach(BiConsumer<? super List<String>, ? super Object> action) {

	}

	public void replaceAll(BiFunction<? super List<String>, ? super Object, ?> function) {

	}

	public Object putIfAbsent(List<String> key, Object value) {
		return null;
	}

	public boolean remove(Object key, Object value) {
		return false;
	}

	public boolean replace(List<String> key, Object oldValue, Object newValue) {
		return false;
	}

	public Object replace(List<String> key, Object value) {
		return null;
	}

	public Object computeIfAbsent(List<String> key, Function<? super String, ?> mappingFunction) {
		return locate(true, key, (t, k) -> t.table.computeIfAbsent(k, mappingFunction));
	}

	public Object computeIfPresent(List<String> key, BiFunction<? super List<String>, ? super Object, ?> remappingFunction) {
		return null;
	}

	public Object compute(List<String> key, BiFunction<? super List<String>, ? super Object, ?> remappingFunction) {
		return null;
	}

	public Object merge(List<String> key, Object value, BiFunction<? super Object, ? super Object, ?> remappingFunction) {
		return null;
	}

	public void putAll(Map<? extends List<String>, ?> m) {
		throw new UnsupportedOperationException();
	}

	public void clear() {
		table.clear();
	}

	public Set<List<String>> keySet() {
		throw new UnsupportedOperationException();
	}

	public Collection<Object> values() {
		throw new UnsupportedOperationException();
	}

//	public Set<Entry<List<String>, Object>> entrySet() {
//		throw new UnsupportedOperationException();
//	}

	private <T> T locate(boolean create, List<String> key, BiFunction<? super TomlTable, ? super String, T> action) {
		if (key.isEmpty()) {
			throw new IllegalStateException("No key given...");
		}
		TomlTable table = this;
		Iterator<String> it = key.iterator();
		while (it.hasNext()) {
			String s = it.next();
			if (!it.hasNext()) {
				return action.apply(table, s);
			}
			if (!create) {
				return null;
			}
			Object current = table.table.computeIfAbsent(s, k -> new TomlTable());
			if (current instanceof TomlArray) {
				current = ((TomlArray) current).get(((TomlArray) current).size() - 1);
			}
			if (!(current instanceof TomlTable)) {
				throw new IllegalStateException("Cannot insert into a non-table value: " + key + ":[" + s + "] => " + current.getClass());
			}
			table = (TomlTable) current;
		}
		throw new IllegalStateException("Internal Table State violated. [This shouldn't be possible. If this is thrown, contact author.]");
	}

	public void write(StringBuilder b, int indent, int increment) {
		b.append("{\n");

		for (Entry<String, Object> entry : table.entrySet()) {
			b.append(" ".repeat(indent * increment)).append(entry.getKey()).append(" = ");
			Object value = entry.getValue();
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
		b.append('}');
	}

	@Override
	public String toString() {
		StringBuilder b = new StringBuilder("TomlTable ");
		write(b, 2, 2);
		return b.toString();
	}
}
