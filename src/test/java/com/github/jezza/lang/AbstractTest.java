package com.github.jezza.lang;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringReader;
import java.nio.charset.StandardCharsets;

import com.github.jezza.TomlTable;

/**
 * @author Jezza
 */
abstract class AbstractTest {
	Token slurpFile(String file) throws IOException {
		_TomlLexer lexer = lexFile(file);
		Token token = lexer.next();
		assertEquals(Tokens.EOS, lexer.next().type, "Input wasn't completely consumed");
		return token;
	}

	static Token slurp(String input) throws IOException {
		_TomlLexer lexer = lex(input);
		Token token = lexer.next();
		assertEquals(Tokens.EOS, lexer.next().type, "Input wasn't completely consumed");
		return token;
	}

	static void test(String expected, String input) throws IOException {
		Token string = slurp(input);
		assertTrue(string.type == Tokens.STRING || string.type == Tokens.ML_STRING, "Failed to parse string");
		assertEquals(expected, string.value, "Failed to parse string correctly...");
	}

	InputStream locate(String file) {
		InputStream in = getClass().getResourceAsStream(file);
		if (in == null) {
			throw new IllegalStateException("Unable to locate \"" + file + '"');
		}
		return in;
	}

	Reader reader(String file) {
		return new InputStreamReader(locate(file), StandardCharsets.UTF_8);
	}

	_TomlLexer lexFile(String file) {
		return new _TomlLexer(reader(file));
	}

	static _TomlLexer lex(String input) {
		return new _TomlLexer(new StringReader(input));
	}

	TomlTable parseFile(String file) throws IOException {
		return new TomlParser(reader(file)).parse();
	}

	static TomlTable parse(String input) throws IOException {
		return new TomlParser(new StringReader(input)).parse();
	}

	void compareOutput(String expected, String file) throws IOException {
		// Yeah, I know I shouldn't use a toString to verify tests...
		// But I'm a lazy fuck...
		String output = parseFile(file).toString();
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		byte[] buffer = new byte[8192];
		try (InputStream in = locate(expected)) {
			int c;
			while ((c = in.read(buffer)) != -1) {
				out.write(buffer, 0, c);
			}
		}
		String content = out.toString("UTF-8");
		assertEquals(content, output);
	}
}
