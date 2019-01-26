package com.github.jezza.lang;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;

import org.junit.jupiter.api.Test;

/**
 * @author Jezza
 */
final class BasicStringTest extends AbstractTest {

	@Test
	void basic() {
		test("value", "\"value\"");
		test("value\nreturn", "\"value\\nreturn\"");
	}

	@Test
	void unicode4() {
		test("Here: \u0000", "\"Here: \\u0000\"");
		test("Here: \u9900", "\"Here: \\u9900\"");
		test("Here: \u0901", "\"Here: \\u0901\"");
	}

	@Test
	void unicode8() {
		test("Here: \udbff\udfff", "\"Here: \\U0010FFFF\"");
	}

	@Test
	void linebreak_poison() {
		Token token = slurp("\"value\nloop\"");
		assertEquals(Tokens.STRING_POISON, token.type, "Token isn't a poison string");
	}

	@Test
	void simple() throws IOException {
		Token token = slurpFile("/strings/basic/multiline/simple.toml");
		assertTrue(token.type == Tokens.STRING || token.type == Tokens.ML_STRING, "Failed to parse string");
		assertEquals("Roses are red\nViolets are blue", token.value);
	}

	@Test
	void empty() {
		test("", "\"\"\"\"\"\"");
	}

	@Test
	void cut() throws IOException {
		Token token = slurpFile("/strings/basic/multiline/cut.toml");
		assertTrue(token.type == Tokens.STRING || token.type == Tokens.ML_STRING, "Failed to parse string");
		assertEquals("The quick brown fox jumps over the lazy dog.", token.value);
	}

	@Test
	void indent() throws IOException {
		Token token = slurpFile("/strings/basic/multiline/indent.toml");
		assertTrue(token.type == Tokens.STRING || token.type == Tokens.ML_STRING, "Failed to parse string");
		assertEquals("The quick brown fox jumps over the lazy dog.", token.value);
	}

	@Test
	void escape() {
		test("\u0000\u0090\u0109\n\udbff\udfff", "\"\"\"" +
				"\\u0000" +
				"\\u0090" +
				"\\u0109\n" +
				"\\U0010FFFF" +
				"\"\"\"");
	}

	@Test
	void quote() throws IOException {
		_TomlLexer lexer = lexer("/strings/basic/multiline/quote.toml");
		{
			Token next = lexer.next();
			assertTrue(next.type == Tokens.STRING || next.type == Tokens.ML_STRING, "Failed to parse string");
			assertEquals("Roses are red\nViolets are blue", next.value);
		}
		{
			Token next = lexer.next();
			assertTrue(next.type == Tokens.STRING || next.type == Tokens.ML_STRING, "Failed to parse string");
			assertEquals("Roses a\"\"re red\nViolet\"s are blue", next.value);
		}
		{
			Token next = lexer.next();
			assertTrue(next.type == Tokens.STRING || next.type == Tokens.ML_STRING, "Failed to parse string");
			assertEquals("Roses a\"re red\nViolets are blue", next.value);
		}
	}
}
