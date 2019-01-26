package com.github.jezza.lang;

/**
 * @author Jezza
 */
public final class Token {
	public static final Token EOS = new Token(Tokens.EOS, -1, -1, null);

	public final int type;
	public final int row;
	public final int col;
	public final String value;

	public Token(int type, int row, int col, String value) {
		this.type = type;
		this.row = row;
		this.col = col;
		this.value = value;
	}

	@Override
	public String toString() {
		return String.format("Token{type=%s, row=%d, col=%d, value=%s}",
				Tokens.name(type),
				row,
				col,
				value);
	}
}
