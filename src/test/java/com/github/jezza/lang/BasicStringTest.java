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
	void basic() throws IOException {
		test("value", "\"value\"");
		test("value\nreturn", "\"value\\nreturn\"");
	}

	@Test
	void unicode4() throws IOException {
		test("Here: \u0000", "\"Here: \\u0000\"");
		test("Here: \u9900", "\"Here: \\u9900\"");
		test("Here: \u0901", "\"Here: \\u0901\"");
	}

	@Test
	void unicode8() throws IOException {
		test("Here: \udbff\udfff", "\"Here: \\U0010FFFF\"");
	}

	@Test
	void linebreak_poison() throws IOException {
		_TomlLexer lexer = lex("\"value\nloop\"");
		Token token = lexer.next();
		assertEquals(Tokens.STRING_POISON, token.type, "Token isn't a poison string");
		assertEquals(Tokens.KEY, lexer.next().type, "Failed to read next token. [Expected a key, as it's a poisoned string]");
		assertEquals(Tokens.EOS, lexer.next().type, "Failed to consume all input");
	}

	@Test
	void simple() throws IOException {
		Token token = slurpFile("/strings/basic/multiline/simple.toml");
		assertTrue(token.type == Tokens.STRING || token.type == Tokens.ML_STRING, "Failed to parse string");
		assertEquals("Roses are red\nViolets are blue", token.value);
	}

	@Test
	void empty() throws IOException {
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
	void escape() throws IOException {
		test("\u0000\u0090\u0109\n\udbff\udfff", "\"\"\"" +
				"\\u0000" +
				"\\u0090" +
				"\\u0109\n" +
				"\\U0010FFFF" +
				"\"\"\"");
	}

	@Test
	void quote() throws IOException {
		_TomlLexer lexer = lexFile("/strings/basic/multiline/quote.toml");
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
