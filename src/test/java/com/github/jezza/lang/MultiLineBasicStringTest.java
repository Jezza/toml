package com.github.jezza.lang;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.IOException;

import org.junit.jupiter.api.Test;

/**
 * @author Jezza
 */
final class MultiLineBasicStringTest extends AbstractTest {
	@Test
	void simple() throws IOException {
		Token token = slurpFile("/basic-strings/multiline/simple.toml");
		assertEquals(Tokens.STRING, token.type);
		assertEquals("Roses are red\nViolets are blue", token.value);
	}

	@Test
	void empty() throws IOException {
		test("", "\"\"\"\"\"\"");
	}

	@Test
	void cut() throws IOException {
		Token token = slurpFile("/basic-strings/multiline/cut.toml");
		assertEquals(Tokens.STRING, token.type);
		assertEquals("The quick brown fox jumps over the lazy dog.", token.value);
	}

	@Test
	void indent() throws IOException {
		Token token = slurpFile("/basic-strings/multiline/indent.toml");
		assertEquals(Tokens.STRING, token.type);
		assertEquals("The quick brown fox jumps over the lazy dog.", token.value);
	}

	@Test
	void escape() throws IOException {
		test("", "\"\"\"" +
				"" +
				"" +
				"" +
				"\"\"\"");
	}
	
}
