package com.github.jezza.lang;

import static org.junit.jupiter.api.Assertions.assertEquals;

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
		Token token = slurp("\"value\nloop\"");
		assertEquals(Tokens.STRING_POISON, token.type, "Token isn't a poison string");
	}
}
