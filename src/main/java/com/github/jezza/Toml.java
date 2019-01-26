package com.github.jezza;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.function.Supplier;

import com.github.jezza.lang.TomlParser;

/**
 * @author Jezza
 */
public final class Toml {
	private Toml() {
		throw new IllegalStateException();
	}

	public static void main(String[] args) throws Exception {
		TomlTable table = from(Toml.class.getResourceAsStream("/overload.toml"));
		System.out.println(table);
	}

	public static void main0(String[] args) throws Exception {
		TomlTable table = from(List.of(
				() -> Toml.class.getResourceAsStream("/first.toml"),
				() -> Toml.class.getResourceAsStream("/second.toml")
		));

		System.out.println(table);
	}

	public static TomlTable from(InputStream in) throws IOException {
		TomlTable table = new TomlTable();
		TomlParser parser = new TomlParser(new InputStreamReader(in, StandardCharsets.UTF_8));
		parser.parse(table);
		return table;
	}

	public static TomlTable from(Iterable<? extends Supplier<? extends InputStream>> it) throws IOException {
		TomlTable table = new TomlTable();
		for (Supplier<? extends InputStream> supplier : it) {
			try (InputStream in = supplier.get()) {
				TomlParser parser = new TomlParser(new InputStreamReader(in, StandardCharsets.UTF_8));
				parser.parse(table);
			}
		}
		return table;
	}
}
