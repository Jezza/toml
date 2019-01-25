package com.github.jezza.lang;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.IOException;

import org.junit.jupiter.api.Test;

/**
 * @author Jezza
 */
final class LiteralStringTest extends AbstractTest {
	@Test
	void simple(){
		test("value", "'value'");
		test("val\"\"ue0", "'val\"\"ue0'");
		test("\\", "'\\'");
		test("\\\\opt\\lib", "'\\\\opt\\lib'");
	}

	@Test
	void linebreak_poison() throws IOException {
		Token token = slurp("'value\nloop'");
		assertEquals(Tokens.STRING_POISON, token.type, "Token isn't a poison string");
	}
}
