package com.github.jezza.lang;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import com.github.jezza.TomlArray;
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

	public TomlTable parse() throws IOException {
		TomlTable table = new TomlTable();
		parse(table);
		return table;
	}

	public TomlTable parse(TomlTable root) throws IOException {
		TomlTable current = root;

		Token c;
		while ((c = current()) != Token.EOS) {
			int type = c.type;
			if (type == Tokens.LBRACKET) {
				consume();
				boolean array = match(Tokens.LBRACKET);
				List<String> key = key();
				consume(Tokens.RBRACKET);
				if (array) {
//					System.out.println(key + " => array-table");
					consume(Tokens.RBRACKET);
					Object value = root.computeIfAbsent(key, k -> new TomlArray());
					if (!(value instanceof TomlArray)) {
						throw new IllegalStateException("Attempted to redefine object as array. (" + key + " => " + value + ')');
					}
					TomlArray newArray = (TomlArray) value;
					current = new TomlTable();
					newArray.add(current);
				} else {
//					System.out.println(key + " => std-table");
					Object value = root.computeIfAbsent(key, k -> new TomlTable());
					if (!(value instanceof TomlTable)) {
						throw new IllegalStateException("Incompatible signatures: " + key + " => " + value);
					}
					current = ((TomlTable) value);
				}
			} else if (type == Tokens.KEY || type == Tokens.STRING || type == Tokens.INTEGER_DEC) {
				List<String> key = key();
				consume(Tokens.EQ);
				Object value = value();
				current.put(key, value);
//				System.out.println(key + " => " + value);
			} else {
				throw new IllegalStateException("[ERROR] unexpected token: " + c + " ['{KEY}' | '{STRING}' | '[']");
			}
		}
		return root;
	}

	public TomlArray array() throws IOException {
		consume(Tokens.LBRACKET);
		TomlArray array = new TomlArray();
		while (!match(Tokens.RBRACKET)) {
			array.add(value());
			if (!match(Tokens.COMMA)) {
				consume(Tokens.RBRACKET);
				return array;
			}
		}
		return array;
	}

	public TomlTable inlineTable() throws IOException {
		int row = consume(Tokens.LBRACE).row;
		TomlTable table = new TomlTable();
		if (is(Tokens.RBRACE)) {
			if (consume().row != row) {
				throw new IllegalStateException("[ERROR] Inline table not on same line: " + row);
			}
			return table;
		}
		do {
			List<String> key = key();
			if (consume(Tokens.EQ).row != row || current().row != row) {
				throw new IllegalStateException("[ERROR] Inline table not on same line: " + row);
			}
			Object value = value();
			table.put(key, value);
			if (!is(Tokens.COMMA)) {
				break;
			}
			if (consume().row != row) {
				throw new IllegalStateException("[ERROR] Inline table not on same line: " + row);
			}
		} while (true);
		if (consume(Tokens.RBRACE).row != row) {
			throw new IllegalStateException("[ERROR] Inline table not on same line: " + row);
		}
		return table;
	}

	public List<String> key() throws IOException {
		// It assumes you've already checked the token...
		List<String> key = new ArrayList<>(1);
		Token t = consume();
		int row = t.row;
		assert t.type == Tokens.KEY || t.type == Tokens.STRING || t.type == Tokens.INTEGER_DEC;
		key.add(t.value);
		while (current().type == Tokens.DOT) {
			if (consume().row != row) {
				throw new IllegalStateException("[ERROR] Dotted key not on same line: " + row);
			}
			Token next = current();
			if (next.type != Tokens.KEY && next.type != Tokens.STRING && next.type != Tokens.INTEGER_DEC) {
				throw new IllegalStateException("[ERROR] unexpected token: " + next + " ['{KEY}' | '{STRING}']");
			}
			if (consume().row != row) {
				throw new IllegalStateException("[ERROR] Dotted key not on same line: " + row);
			}
			key.add(next.value);
		}
		return key;
	}

	public Object value() throws IOException {
		Token c = current();
		switch (c.type) {
			case Tokens.STRING_POISON:
			case Tokens.ML_STRING_POISON:
				// @TODO Jezza - 26 Jan. 2019: Check some strict flag or something?
			case Tokens.STRING:
			case Tokens.ML_STRING:
				consume();
				return c.value;
			case Tokens.TRUE:
				consume();
				return Boolean.TRUE;
			case Tokens.FALSE:
				consume();
				return Boolean.FALSE;
			case Tokens.LBRACKET:
				return array();
			case Tokens.LBRACE:
				return inlineTable();
			case Tokens.DATE:
				consume();
				// @TODO Jezza - 26 Jan. 2019: I should probably do this... At some point...
				return c.value;
			case Tokens.INF:
				consume();
				return c.value.charAt(0) == '-'
						? Double.NEGATIVE_INFINITY
						: Double.POSITIVE_INFINITY;
			case Tokens.NAN:
				consume();
				return Double.NaN;
			case Tokens.FLOAT:
				consume();
				return intoDouble(c);
			case Tokens.INTEGER_BIN:
				consume();
				return intoLong(c, 2, 2);
			case Tokens.INTEGER_OCT:
				consume();
				return intoLong(c, 2, 8);
			case Tokens.INTEGER_DEC:
				consume();
				return intoLong(c, 0, 10);
			case Tokens.INTEGER_HEX:
				consume();
				return intoLong(c, 2, 16);
			default:
				throw new IllegalStateException("[ERROR] unexpected token: " + c
						+ " ['{STRING}' | '{ML_STRING}' | '{BOOLEAN}' | '{LBRACKET}' | '{DATE}' | '{FLOAT}' | '{INTEGER_(BIN|OCT|DEC|HEX)}']");
		}
	}

	private static Long intoLong(Token token, int offset, int radix) {
		String input = token.value.replace("_", "");
		return Long.parseLong(offset > 0
				? input.substring(offset)
				: input, radix);
	}

	private static Double intoDouble(Token token) {
		String input = token.value.replace("_", "");
		return Double.parseDouble(input);
	}
}
