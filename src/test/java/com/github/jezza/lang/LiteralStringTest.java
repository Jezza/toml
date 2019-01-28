package com.github.jezza.lang;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;

import org.junit.jupiter.api.Test;

/**
 * @author Jezza
 */
final class LiteralStringTest extends AbstractTest {
	@Test
	void simple() throws IOException {
		test("value", "'value'");
		test("val\"\"ue0", "'val\"\"ue0'");
		test("\\", "'\\'");
		test("\\\\opt\\lib", "'\\\\opt\\lib'");
	}

	@Test
	void linebreak_poison() throws IOException {
		_TomlLexer lexer = lex("'value\nloop'");
		Token token = lexer.next();
		assertEquals(Tokens.STRING_POISON, token.type, "Token isn't a poison string");
		assertEquals(Tokens.KEY, lexer.next().type, "Failed to read next token. [Expected a key, as it's a poisoned string]");
		assertEquals(Tokens.EOS, lexer.next().type, "Failed to consume all input");
	}

	@Test
	void multiline() throws IOException {
		_TomlLexer lexer = lexFile("/strings/literal/mutliline.toml");
		{
			Token next = lexer.next();
			assertTrue(next.type == Tokens.STRING || next.type == Tokens.ML_STRING, "Failed to parse string.");
			assertEquals("I [dw]on't need \\d{2} apples", next.value);
		}
		{
			Token next = lexer.next();
			assertTrue(next.type == Tokens.STRING || next.type == Tokens.ML_STRING, "Failed to parse string.");
			assertEquals("The first newline is\n" +
					"trimmed in raw strings.\n" +
					"   All other whitespace\n" +
					"   is preserved.\n", next.value);
		}
	}

	@Test
	void singleline() throws IOException {
		_TomlLexer lexer = lexFile("/strings/literal/singleline.toml");
		{
			Token next = lexer.next();
			assertEquals(Tokens.STRING, next.type);
			assertEquals("C:\\Users\\nodejs\\templates", next.value);
		}
		{
			Token next = lexer.next();
			assertEquals(Tokens.STRING, next.type);
			assertEquals("\\\\ServerX\\admin$\\system32\\", next.value);
		}
		{
			Token next = lexer.next();
			assertEquals(Tokens.STRING, next.type);
			assertEquals("Tom \"Dubs\" Preston-Werner", next.value);
		}
		{
			Token next = lexer.next();
			assertEquals(Tokens.STRING, next.type);
			assertEquals("<\\i\\c*\\s*>", next.value);
		}
	}
}
