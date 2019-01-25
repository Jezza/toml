package com.github.jezza;

import java.util.Map;

import com.github.jezza.util.Strings;

/**
 * @author Jezza
 */
public final class TomlTable {

	private final Map<String, Object> table;

	public TomlTable(Map<String, Object> table) {
		this.table = table;
	}

	@Override
	public String toString() {
		return Strings.format("TomlTable{}",
				table);
	}
}
