package com.github.jezza.lang;

import java.io.IOException;

import org.junit.jupiter.api.Test;

/**
 * @author Jezza
 */
final class ExamplesTest extends AbstractTest {
	@Test
	void examples() throws IOException {
		compareOutput("/examples/examples.txt", "/examples/examples.toml");
	}

	@Test
	void fruit() throws IOException {
		compareOutput("/examples/fruit.txt", "/examples/fruit.toml");
	}

	@Test
	void hardExample() throws IOException {
		compareOutput("/examples/hard_example.txt", "/examples/hard_example.toml");
	}

	@Test
	void hardExampleUnicode() throws IOException {
		compareOutput("/examples/hard_example_unicode.txt", "/examples/hard_example_unicode.toml");
	}
}
