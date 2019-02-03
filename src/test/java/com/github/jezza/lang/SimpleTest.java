package com.github.jezza.lang;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.time.temporal.TemporalAccessor;

import org.junit.jupiter.api.Test;

import com.github.jezza.Toml;
import com.github.jezza.TomlArray;
import com.github.jezza.TomlTable;

/**
 * @author Jezza
 */
final class SimpleTest extends AbstractTest {

	@Test
	void dates() throws IOException {
		TomlTable table = parseFile("/dates.toml");

		OffsetDateTime.from((TemporalAccessor) table.get("odt1"));
		OffsetDateTime.from((TemporalAccessor) table.get("odt2"));
		OffsetDateTime.from((TemporalAccessor) table.get("odt3"));
		OffsetDateTime.from((TemporalAccessor) table.get("odt4"));

		LocalDateTime.from((TemporalAccessor) table.get("ldt1"));
		LocalDateTime.from((TemporalAccessor) table.get("ldt2"));

		LocalDate.from((TemporalAccessor) table.get("ld1"));

		LocalTime.from((TemporalAccessor) table.get("lt1"));
		LocalTime.from((TemporalAccessor) table.get("lt2"));
	}

	@Test
	void floats() throws IOException {
		compareOutput("/floats.txt", "/floats.toml");
	}

	@Test
	void integers() throws IOException {
		compareOutput("/integers.txt", "/integers.toml");
	}

	@Test
	void keys() throws IOException {
		compareOutput("/keys.txt", "/keys.toml");
	}

	@Test
	void strings() throws IOException {
		compareOutput("/strings.txt", "/strings.toml");
	}

	@Test
	void basic() throws IOException {
		TomlTable table = Toml.from(
				locate("/first.toml")
		);

		assertEquals("first", table.get("origin"));
		assertEquals("first", table.get("untouched"));

		TomlTable metadata = (TomlTable) table.get("metadata");
		{
			TomlTable entry = (TomlTable) metadata.get("a");
			assertEquals("first-a", entry.get("name"));
			assertEquals("first", entry.get("untouched"));
			TomlArray values = new TomlArray();
			values.add("first-0");
			values.add("first-1");
			values.add("first-2");
			assertEquals(values, entry.get("values"));
		}
		{
			TomlTable entry = (TomlTable) metadata.get("b");
			assertEquals("first-b", entry.get("name"));
			assertEquals("first", entry.get("untouched"));
			TomlArray values = new TomlArray();
			values.add("first-0");
			values.add("first-1");
			assertEquals(values, entry.get("values"));
		}
	}

	@Test
	void inheritance() throws IOException {
		TomlTable table = Toml.from(
				locate("/first.toml"),
				locate("/second.toml")
		);

		assertEquals("second", table.get("origin"));
		assertEquals("first", table.get("untouched"));

		TomlTable metadata = (TomlTable) table.get("metadata");
		{
			TomlTable entry = (TomlTable) metadata.get("a");
			assertEquals("second-a", entry.get("name"));
			assertEquals("first", entry.get("untouched"));
			TomlArray values = new TomlArray();
			values.add("second-4");
			assertEquals(values, entry.get("values"));
		}
		{
			TomlTable entry = (TomlTable) metadata.get("b");
			assertEquals("first-b", entry.get("name"));
			assertEquals("first", entry.get("untouched"));
			TomlArray values = new TomlArray();
			values.add("first-0");
			values.add("first-1");
			assertEquals(values, entry.get("values"));
		}
	}

	@Test
	void numberedKeys() throws IOException {
		compareOutput("/number-keys.txt", "/number-keys.toml");
	}
}
