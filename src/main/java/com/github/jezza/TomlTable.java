package com.github.jezza;

import java.time.temporal.TemporalAccessor;
import java.time.temporal.TemporalQueries;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Function;

import com.github.jezza.util.Strings;

/**
 * The only types that can be stored in this table are:
 * <p>
 * Boolean | Double | Long | String | TomlArray | TomlTable | TemporalAccessor
 * <p>
 * Attempting to store anything else is undefined behaviour... (Although, chances are nothing different will happen...)
 *
 * @author Jezza
 */
public final class TomlTable {
	private final Map<String, Object> table;

	public TomlTable() {
		this(new LinkedHashMap<>());
	}

	public TomlTable(Map<String, Object> table) {
		this.table = Objects.requireNonNull(table);
	}

	public Map<String, Object> asMap() {
		return table;
	}
	
	public boolean isEmpty() {
		return table.isEmpty();
	}

	public int size() {
		return table.size();
	}

	public boolean containsKey(Iterable<String> key) {
		Boolean result = locate(false, key, (t, k) -> t.table.containsKey(k));
		return result != null && result;
	}

	public Object get(String key) {
		return get(Strings.split(key));
	}

	public Object get(Iterable<String> key) {
		return locate(false, key, (t, k) -> t.table.get(k));
	}

	public Object put(String key, Object value) {
		return put(Strings.split(key), value);
	}

	public Object put(Iterable<String> key, Object value) {
		return locate(true, key, (table, k) -> table.table.put(k, value));
	}

	public Object remove(String key) {
		return remove(Strings.split(key));
	}

	public Object remove(Iterable<String> key) {
		return locate(false, key, (t, k) -> t.table.remove(k));
	}

	public Object getOrDefault(String key, Object defaultValue) {
		return getOrDefault(Strings.split(key), defaultValue);
	}

	public Object getOrDefault(Iterable<String> key, Object defaultValue) {
		Object value = get(key);
		return value != null
				? value
				: defaultValue;
	}

	public void forEach(BiConsumer<? super String, ? super Object> action) {
		table.forEach(action);
	}

	public void replaceAll(BiFunction<? super String, ? super Object, ?> function) {
		table.replaceAll(function);
	}

	public Object putIfAbsent(String key, Object value) {
		return putIfAbsent(Strings.split(key), value);
	}

	public Object putIfAbsent(Iterable<String> key, Object value) {
		return locate(true, key, (t, k) -> t.table.putIfAbsent(k, value));
	}

	public Object replace(String key, Object value) {
		return replace(Strings.split(key), value);
	}

	public Object replace(Iterable<String> key, Object value) {
		return locate(false, key, (t, k) -> t.table.replace(k, value));
	}

	public boolean replace(String key, Object oldValue, Object newValue) {
		return replace(Strings.split(key), oldValue, newValue);
	}

	public boolean replace(Iterable<String> key, Object oldValue, Object newValue) {
		Boolean locate = locate(false, key, (t, k) -> t.table.replace(k, oldValue, newValue));
		return locate != null && locate;
	}

	public Object computeIfAbsent(String key, Function<? super String, ?> mappingFunction) {
		return computeIfAbsent(Strings.split(key), mappingFunction);
	}

	public Object computeIfAbsent(Iterable<String> key, Function<? super String, ?> mappingFunction) {
		return locate(true, key, (t, k) -> t.table.computeIfAbsent(k, mappingFunction));
	}

	public Object computeIfPresent(String key, BiFunction<? super String, ? super Object, ?> remappingFunction) {
		return computeIfPresent(Strings.split(key), remappingFunction);
	}

	public Object computeIfPresent(Iterable<String> key, BiFunction<? super String, ? super Object, ?> remappingFunction) {
		return locate(true, key, (t, k) -> t.table.computeIfPresent(k, remappingFunction));
	}

	public Object compute(String key, BiFunction<? super String, ? super Object, ?> remappingFunction) {
		return compute(Strings.split(key), remappingFunction);
	}

	public Object compute(Iterable<String> key, BiFunction<? super String, ? super Object, ?> remappingFunction) {
		return locate(true, key, (t, k) -> t.table.compute(k, remappingFunction));
	}

	public Object merge(String key, Object value, BiFunction<? super Object, ? super Object, ?> remappingFunction) {
		return merge(Strings.split(key), value, remappingFunction);
	}

	public Object merge(Iterable<String> key, Object value, BiFunction<? super Object, ? super Object, ?> remappingFunction) {
		return locate(true, key, (t, k) -> t.table.merge(k, value, remappingFunction));
	}

	public void putAll(TomlTable table) {
		throw new UnsupportedOperationException();
	}

	public void clear() {
		table.clear();
	}

	public Set<String> keySet() {
		return table.keySet();
	}

	public Collection<Object> values() {
		return table.values();
	}

	public Set<Entry<String, Object>> entrySet() {
		return table.entrySet();
	}

	private <T> T locate(boolean create, Iterable<String> key, BiFunction<? super TomlTable, ? super String, T> action) {
		Iterator<String> it = key.iterator();
		if (!it.hasNext()) {
			throw new IllegalStateException("No key given...");
		}
		TomlTable table = this;
		do {
			String s = it.next();
			if (!it.hasNext()) {
				return action.apply(table, s);
			}
			Object current;
			if (create) {
				current = table.table.computeIfAbsent(s, k -> new TomlTable());
			} else {
				current = table.table.get(s);
				if (current == null) {
					return null;
				}
			}
			if (current instanceof TomlArray) {
				current = ((TomlArray) current).get(((TomlArray) current).size() - 1);
			}
			if (!(current instanceof TomlTable)) {
				throw new IllegalStateException("Cannot insert into a non-table value: " + key + ":[" + s + "] => " + current.getClass());
			}
			table = (TomlTable) current;
		} while (true); // This is basically `it.hasNext()`, but no point checking it again...
	}

	void write(StringBuilder b, int indent, int increment) {
		b.append("{\n");

		for (Entry<String, Object> entry : table.entrySet()) {
			for (int i = 0, l = indent * increment; i < l; i++) {
				b.append(' ');
			}
			b.append(entry.getKey()).append(" = ");
			Object value = entry.getValue();
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
			for (int i = 0; i < v; i++) {
				b.append(' ');
			}
		}
		b.append('}');
	}

	@Override
	public String toString() {
		StringBuilder b = new StringBuilder("TomlTable ");
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

		TomlTable other = (TomlTable) o;
		return table.equals(other.table);
	}

	@Override
	public int hashCode() {
		return table.hashCode();
	}
}
