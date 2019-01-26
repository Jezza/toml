package com.github.jezza.lang;

/**
 * @author Jezza
 */
public final class Tokens {
	public static final int EOS = -1;
	public static final int BAD_CHARACTER = 0;

	public static final int STRING = 3;
	public static final int ML_STRING = 4;
	public static final int STRING_POISON = 5;
	public static final int ML_STRING_POISON = 6;

	public static final int INTEGER_DEC = 10;
	public static final int INTEGER_HEX = 11;
	public static final int INTEGER_OCT = 12;
	public static final int INTEGER_BIN = 13;

	public static final int FLOAT = 15;
	public static final int INF = 16;
	public static final int NAN = 17;

	public static final int KEY = 20;

	public static final int DOT = 25;
	public static final int COMMA = 26;
	public static final int EQ = 27;
	public static final int LBRACKET = 28;
	public static final int RBRACKET = 29;
	public static final int LBRACE = 30;
	public static final int RBRACE = 31;
	public static final int TRUE = 32;
	public static final int FALSE = 33;
	public static final int DATE = 34;

	private Tokens() {
		throw new IllegalStateException();
	}

	public static String name(int type) {
		switch (type) {
			case EOS:
				return "EOS";
			case BAD_CHARACTER:
				return "BAD_CHARACTER";
			case STRING:
				return "BASIC_STRING";
			case ML_STRING:
				return "ML_STRING";
			case STRING_POISON:
				return "BASIC_STRING_POISON";
			case ML_STRING_POISON:
				return "ML_STRING_POISON";
			case INTEGER_DEC:
				return "INTEGER_DEC";
			case INTEGER_HEX:
				return "INTEGER_HEX";
			case INTEGER_OCT:
				return "INTEGER_OCT";
			case INTEGER_BIN:
				return "INTEGER_BIN";
			case FLOAT:
				return "FLOAT";
			case INF:
				return "INF";
			case NAN:
				return "NAN";
			case KEY:
				return "KEY";
			case DOT:
				return "DOT";
			case COMMA:
				return "COMMA";
			case EQ:
				return "EQ";
			case LBRACKET:
				return "LBRACKET";
			case RBRACKET:
				return "RBRACKET";
			case LBRACE:
				return "LBRACE";
			case RBRACE:
				return "RBRACE";
			case TRUE:
				return "TRUE";
			case FALSE:
				return "FALSE";
			case DATE:
				return "DATE";
			default:
				return "<unknown>";
		}
	}
}
