package com.github.jezza.lang;

import java.io.IOException;

import org.junit.jupiter.api.Test;

/**
 * @author Jezza
 */
final class SimpleTest extends AbstractTest {

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
}
