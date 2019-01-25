package com.github.jezza.lang;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringReader;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

import com.github.jezza.TomlTable;
import com.github.jezza.util.Strings;

/**
 * @author Jezza
 */
public final class TomlParser {
	private final _TomlLexer lexer;

	private Token current;

	public TomlParser(String in) {
		this(new StringReader(in));
	}

	public TomlParser(Reader in) {
		lexer = new _TomlLexer(in);
	}

	private Token current() throws IOException {
		return current != null
				? current
				: (current = lexer.next());
	}

	private boolean match(int type) throws IOException {
		boolean match = current().type == type;
		if (match) {
			current = null;
		}
		return match;
	}

	private boolean is(int type) throws IOException {
		return current().type == type;
	}

	private boolean not(int type) throws IOException {
		return current().type != type;
	}

	private Token consume() throws IOException {
		if (current == null) {
			return lexer.next();
		}
		Token current = this.current;
		this.current = null;
		return current;
	}

	private Token consume(int type) throws IOException {
		Token token = current();
		if (token.type == type) {
			current = null;
			return token;
		}
		throw new RuntimeException(Strings.format2("Unexpected token: {}, expected {}.", token, Tokens.name(type)));
	}

	public TomlTable table() throws IOException {
		Map<String, Object> table = new HashMap<>();

		Token c;
		while ((c = current()) != Token.EOS) {
			System.out.println(c);
			switch (c.type) {
				case Tokens.KEY:
			}
			consume();
		}

		// a = ""
		// [section]
		// b = ""
		// [[array]]
		// c = ""
		// [[array]]
		// c = ""

//		{
//			a = "",
//			section = {
//				b = ""
//			},
//			array = [
//				{
//					c = ""
//				},
//				{
//					c = ""
//				},
//			]
//		}
		return new TomlTable(table);
	}

	public static void main(String[] args) throws IOException {
		String value0 = "\u1050";
		String value1 = "\\u1050";
		System.out.println(value0);
		System.out.println(value1);
		System.out.println(convert(value1));
	}

	private static char convert(String input) {
		return (char) Integer.parseInt(input.substring(2), 16);
	}

	public static void main0(String[] args) throws IOException {
		InputStream in = TomlParser.class.getResourceAsStream("/integers.toml");
		var parser = new TomlParser(new InputStreamReader(in, StandardCharsets.UTF_8));
		TomlTable table = parser.table();
		System.out.println(table);
	}
}
