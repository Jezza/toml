package com.github.jezza;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.util.Arrays;

import com.github.jezza.lang.TomlParser;

/**
 * @author Jezza
 */
public final class Toml {
	public static final String NONSTANDARD_EXTENSIONS_KEY = "com.github.jezza.toml.nonstandard";

	/**
	 * This feature flag hides all of the nonstandard extensions so anyone
	 * who uses this library in its default state can freely switch to another
	 * library without relying on some feature that we provide.
	 * (Yes, they'll still have to migrate classes, but they won't have to migrate data files.)
	 * <p>
	 * If this is activated, then the developer has acknowledged the use of nonstandard features.
	 * (Technically, it's a VM flag, so it could be activated by another library, and the developer could have no knowledge of it... )
	 * <p>
	 * <strong>Current Extensions:</strong>
	 * <ul>
	 * <li>
	 * <strong>Relative table paths: [.value]</strong>
	 * When specifying a table section, you can use a shorthand to append to previous table.
	 * </li>
	 * </ul>
	 */
	public static final boolean NONSTANDARD_EXTENSIONS = Boolean.getBoolean(NONSTANDARD_EXTENSIONS_KEY);

	private Toml() {
		throw new IllegalStateException();
	}

	public static TomlTable from(InputStream input) throws IOException {
		return new TomlParser(input).parse();
	}

	public static TomlTable from(Reader input) throws IOException {
		return new TomlParser(input).parse();
	}

	public static TomlTable into(InputStream input, TomlTable table) throws IOException {
		return new TomlParser(input).parse(table);
	}

	public static TomlTable into(Reader input, TomlTable table) throws IOException {
		return new TomlParser(input).parse(table);
	}

	public static TomlTable from(Reader... inputs) throws IOException {
		return fromReaders(Arrays.asList(inputs));
	}

	public static TomlTable fromReaders(Iterable<? extends Reader> it) throws IOException {
		TomlTable table = new TomlTable();
		for (Reader reader : it) {
			into(reader, table);
		}
		return table;
	}

	public static TomlTable from(InputStream... inputs) throws IOException {
		return fromInputStreams(Arrays.asList(inputs));
	}

	public static TomlTable fromInputStreams(Iterable<? extends InputStream> it) throws IOException {
		TomlTable table = new TomlTable();
		for (InputStream in : it) {
			into(in, table);
		}
		return table;
	}
}
