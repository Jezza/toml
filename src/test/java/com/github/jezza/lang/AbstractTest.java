package com.github.jezza.lang;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

/**
 * @author Jezza
 */
abstract class AbstractTest {

	static Token slurp(String input) {
		_TomlLexer lexer = new _TomlLexer(input);
		try {
			Token token = lexer.next();
			assertEquals(Tokens.EOS, lexer.next().type, "Input wasn't completely consumed");
			return token;
		} catch (IOException e) {
			throw new IllegalStateException("Should never happen", e);
		}
	}

	static String nom(String input) {
		Token string = slurp(input);
		assertEquals(Tokens.STRING, string.type, "Failed to parse string");
		assertTrue(string.value instanceof String, "Output is a string type, but doesn't hold a String value");
		return (String) string.value;
	}

	static void test(String expected, String input) {
		assertEquals(expected, nom(input), "Failed to parse string correctly...");
	}

	Token slurpFile(String file) throws IOException {
		try (InputStream input = getClass().getResourceAsStream(file)) {
			_TomlLexer lexer = new _TomlLexer(new InputStreamReader(input, StandardCharsets.UTF_8));
			Token token = lexer.next();
			assertEquals(Tokens.EOS, lexer.next().type, "Input wasn't completely consumed");
			return token;
		}
	}

	String nomFile(String file) throws IOException {
		Token string = slurpFile(file);
		assertEquals(Tokens.STRING, string.type, "Failed to parse string");
		assertTrue(string.value instanceof String, "Output is a string type, but doesn't hold a String value");
		return (String) string.value;
	}

	void testFile(String expected, String file) throws IOException {
		assertEquals(expected, nomFile(file), "Failed to parse string correctly...");
	}
}
