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
