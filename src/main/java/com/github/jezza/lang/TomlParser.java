package com.github.jezza.lang;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringReader;
import java.nio.charset.StandardCharsets;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;

import com.github.jezza.TomlArray;
import com.github.jezza.TomlTable;
import com.github.jezza.util.Strings;

/**
 * @author Jezza
 */
public class TomlParser {
	// If you're reading the source code, and have absolutely no idea where this _TomlLexer comes from,
	// It's generated from "src/main/resources/toml.flex" during compile time.
	private final _TomlLexer lexer;

	private Token current;

	public TomlParser(String in) {
		this(new StringReader(in));
	}

	public TomlParser(InputStream in) {
		this(new InputStreamReader(in, StandardCharsets.UTF_8));
	}

	public TomlParser(Reader in) {
		lexer = new _TomlLexer(in);
	}

	protected final Token current() throws IOException {
		return current != null
				? current
				: (current = lexer.next());
	}

	protected final boolean match(int type) throws IOException {
		boolean match = current().type == type;
		if (match) {
			current = null;
		}
		return match;
	}

	protected final boolean is(int type) throws IOException {
		return current().type == type;
	}

	protected final boolean not(int type) throws IOException {
		return current().type != type;
	}

	protected final Token consume() throws IOException {
		if (current == null) {
			return lexer.next();
		}
		Token current = this.current;
		this.current = null;
		return current;
	}

	protected final Token consume(int type) throws IOException {
		Token token = current();
		if (token.type == type) {
			current = null;
			return token;
		}
		throw new RuntimeException(Strings.format("Unexpected token: {}, expected {}.", token, Tokens.name(type)));
	}

	public final TomlTable parse() throws IOException {
		TomlTable table = new TomlTable(4);
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
					Object value = root.computeIfAbsent(key, k -> new TomlArray(0));
					if (!(value instanceof TomlArray)) {
						throw new IllegalStateException("Attempted to redefine object as array. (" + key + " => " + value + ')');
					}
					TomlArray newArray = (TomlArray) value;
					current = new TomlTable();
					newArray.add(current);
				} else {
//					System.out.println(key + " => std-table");
					Object value = root.computeIfAbsent(key, k -> new TomlTable(4));
					if (!(value instanceof TomlTable)) {
						throw new IllegalStateException("Incompatible signatures: " + key + " => " + value);
					}
					current = ((TomlTable) value);
				}
			} else if (isKey(type)) {
				int row = c.row;
				List<String> key = key();
				if (consume(Tokens.EQ).row != row || current().row != row) {
					throw new IllegalStateException("[ERROR] Key-Value pair not on same line: " + row);
				}
				Object value = value();
				current.put(key, value);
//				System.out.println(key + " => " + value);
			} else {
				throw new IllegalStateException("[ERROR] unexpected token: " + c + " ['{KEY}' | '{STRING}' | '[']");
			}
		}
		return root;
	}

	protected TomlArray array() throws IOException {
		consume(Tokens.LBRACKET);
		TomlArray array = new TomlArray(0);
		while (!match(Tokens.RBRACKET)) {
			array.add(value());
			if (!match(Tokens.COMMA)) {
				consume(Tokens.RBRACKET);
				return array;
			}
		}
		return array;
	}

	protected TomlTable inlineTable() throws IOException {
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

	private static boolean isKey(int type) {
		return type == Tokens.KEY
				|| type == Tokens.STRING
				|| type == Tokens.INTEGER_DEC
				|| type == Tokens.INTEGER_HEX
				|| type == Tokens.INTEGER_OCT
				|| type == Tokens.INTEGER_BIN
				|| type == Tokens.FLOAT;
	}

	protected List<String> key() throws IOException {
		Token t = consume();
		int row = t.row;
		if (!isKey(t.type)) {
			throw new IllegalStateException("[ERROR] unexpected token: " + t + " ['{KEY}' | '{STRING}']");
		}
		// Just a bit of a hack with regards to numbered keys.
		// A simple dotted key composed of two integer keys ends up getting lexed as a float.
		// eg, `1.0` should be parsed as `1`.`0`, but instead we pick it as: `1.0`
		// This code just tears it apart, and inserts it as two segments.
		String[] segments;
		if (t.type == Tokens.FLOAT) {
			String value = t.value;
			int i = value.indexOf('.');
			String first = value.substring(0, i);
			String second = value.substring(i + 1);
			segments = new String[]{
					first,
					second
			};
		} else {
			segments = new String[]{
					t.value
			};
		}
		while (current().type == Tokens.DOT) {
			if (consume().row != row) {
				throw new IllegalStateException("[ERROR] Dotted key not on same line: " + row);
			}
			Token next = current();
			if (!isKey(next.type)) {
				throw new IllegalStateException("[ERROR] unexpected token: " + next + " ['{KEY}' | '{STRING}']");
			}
			if (consume().row != row) {
				throw new IllegalStateException("[ERROR] Dotted key not on same line: " + row);
			}
			String value = next.value;
			// Just a bit of a hack with regards to numbered keys.
			// A simple dotted key composed of two integer keys ends up getting lexed as a float.
			// eg, `1.0` should be parsed as `1`.`0`, but instead we pick it as: `1.0`
			// This code just tears it apart, and inserts it as two segments.
			if (next.type == Tokens.FLOAT) {
				int i = value.indexOf('.');
				int length = segments.length;
				segments = Arrays.copyOf(segments, length + 2);
				segments[length] = value.substring(0, i);
				segments[length + 1] = value.substring(i + 1);
			} else {
				int length = segments.length;
				segments = Arrays.copyOf(segments, length + 1);
				segments[length] = value;
			}
		}
		return List.of(segments);
	}

	protected Object value() throws IOException {
		Token c = current();
		switch (c.type) {
			case Tokens.STRING_POISON:
			case Tokens.ML_STRING_POISON:
				// @TODO Jezza - 26 Jan. 2019: Check some strict flag or something?
				throw new IllegalStateException("[ERROR] Illegal string starting at [" + c.row + ':' + c.col + ']');
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
			case Tokens.OFFSET_DATE_TIME:
				consume();
				// @TODO Jezza - 26 Jan. 2019: I wonder if I need to build my own formatter...
				//  (The default formatter doesn't support the optional space in the place of a T)
				return DateTimeFormatter.ISO_OFFSET_DATE_TIME.parse(c.value.replace(' ', 'T'));
			case Tokens.LOCAL_DATE_TIME:
				consume();
				// @TODO Jezza - 26 Jan. 2019: I wonder if I need to build my own formatter...
				//  (The default formatter doesn't support the optional space in the place of a T)
				return DateTimeFormatter.ISO_LOCAL_DATE_TIME.parse(c.value.replace(' ', 'T'));
			case Tokens.LOCAL_DATE:
				consume();
				return DateTimeFormatter.ISO_LOCAL_DATE.parse(c.value);
			case Tokens.LOCAL_TIME:
				consume();
				return DateTimeFormatter.ISO_LOCAL_TIME.parse(c.value);
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

	protected static Long intoLong(Token token, int offset, int radix) {
		// @TODO Jezza - 26 Jan. 2019: I should build up the correct number in the lexer... (Without '_' and number prefix...)
		String input = token.value.replace("_", "");
		return Long.valueOf(offset > 0
				? input.substring(offset)
				: input, radix);
	}

	protected static Double intoDouble(Token token) {
		// @TODO Jezza - 26 Jan. 2019: I should build up the correct number in the lexer... (Without '_')
		String input = token.value.replace("_", "");
		return Double.valueOf(input);
	}
}
