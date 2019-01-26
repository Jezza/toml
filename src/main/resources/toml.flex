package com.github.jezza.lang;

import static com.github.jezza.lang.Tokens.*;

import java.io.StringReader;

%%

%{
	private final StringBuilder string = new StringBuilder(32);
	private boolean poisoned = false;

	public _TomlLexer(String in) {
		this(new StringReader(in));
	}
%}

%final
%class _TomlLexer
%function next
%type Token
%unicode
%line
%column
%eofclose

%eofval{
	return Token.EOS;
%eofval}

EOL = "\r" | "\n" | "\r\n"
WS = [\ \t\f]
EOL_WS = ( {WS} | {EOL} )+
INPUT = [^\r\n]

COMMENT=#{INPUT}*{EOL}?

INTEGER_DEC_SQ=[0-9](_?[0-9])*

INTEGER_DEC=[-+]?{INTEGER_DEC_SQ}
INTEGER_HEX=[-+]?0x[0-9A-Fa-f](_?[0-9A-Fa-f])*
INTEGER_OCT=[-+]?0o[0-7](_?[0-7])*
INTEGER_BIN=[-+]?0b[01](_?[01])*

FLOAT=[-+]?{INTEGER_DEC}(\.{INTEGER_DEC_SQ})?([eE]{INTEGER_DEC})?

INF=[-+]?inf
NAN=[-+]?nan

DATE=[0-9]{4}-[0-9]{2}-[0-9]{2}([Tt][0-9]{2}:[0-9]{2}:[0-9]{2}(\.[0-9]+)?)?([Zz]|[+-][0-9]{2}:[0-9]{2})?

KEY=[0-9_\-a-zA-Z]+

BASIC_STRING_CHAR = [\u0020-\u0021\u0023-\u005B\u005D-\u007E\u0080-\U10FFFF]
ML_BASIC_STRING_CHAR = {EOL_WS} | [\u0020-\u0021\u0023-\u005B\u005D-\u007E\u0080-\U10FFFF]
LITERAL_STRING_CHAR = [\u0009\u0020-\u0026\u0028-\u007E\u0080-\U10FFFF]
ML_LITERAL_STRING_CHAR = {EOL_WS} | [\u0009\u0020-\u0026\u0028-\u007E\u0080-\U10FFFF]

%state S_BASIC_STRING, S_BASIC_STRING_RECOVERY, S_LITERAL_STRING, S_ML_BASIC_STRING, S_ML_LITERAL_STRING

%%

<YYINITIAL> {
  {EOL_WS}+             {}
  {COMMENT}             {}

  \"                    { string.setLength(0); poisoned = false; yybegin(S_BASIC_STRING); }
  \"\"\"{EOL}           { string.setLength(0); yybegin(S_ML_BASIC_STRING); }
  \"\"\"                { string.setLength(0); yybegin(S_ML_BASIC_STRING); }
  '                     { string.setLength(0); poisoned = false; yybegin(S_LITERAL_STRING); }
  '''{EOL}              { string.setLength(0); yybegin(S_ML_LITERAL_STRING); }
  '''                   { string.setLength(0); yybegin(S_ML_LITERAL_STRING); }

  {INTEGER_DEC}         { return new Token(INTEGER_DEC, yyline, yycolumn, yytext()); }
  {INTEGER_HEX}         { return new Token(INTEGER_HEX, yyline, yycolumn, yytext()); }
  {INTEGER_OCT}         { return new Token(INTEGER_OCT, yyline, yycolumn, yytext()); }
  {INTEGER_BIN}         { return new Token(INTEGER_BIN, yyline, yycolumn, yytext()); }

  {FLOAT}               { return new Token(FLOAT, yyline, yycolumn, yytext()); }
  {INF}                 { return new Token(INF, yyline, yycolumn, yytext()); }
  {NAN}                 { return new Token(NAN, yyline, yycolumn, yytext()); }

  {DATE}                { return new Token(DATE, yyline, yycolumn, yytext()); }

  true                  { return new Token(TRUE, yyline, yycolumn, "true"); }
  false                 { return new Token(FALSE, yyline, yycolumn, "false"); }
  {KEY}                 { return new Token(KEY, yyline, yycolumn, yytext()); }

  "."                   { return new Token(DOT, yyline, yycolumn, "."); }
  ","                   { return new Token(COMMA, yyline, yycolumn, ","); }
  "="                   { return new Token(EQ, yyline, yycolumn, "="); }
  "["                   { return new Token(LBRACKET, yyline, yycolumn, "["); }
  "]"                   { return new Token(RBRACKET, yyline, yycolumn, "]"); }
  "{"                   { return new Token(LBRACE, yyline, yycolumn, "{"); }
  "}"                   { return new Token(RBRACE, yyline, yycolumn, "}"); }

  [^] { return new Token(BAD_CHARACTER, yyline, yycolumn, yytext()); }
}

<S_BASIC_STRING> {
  \"                   { yybegin(YYINITIAL);
                         return new Token(poisoned ? STRING_POISON : STRING, yyline, yycolumn, string.toString()); }

  {BASIC_STRING_CHAR}+ { string.append(yytext()); }

  \\b                  { string.append('\b'); }
  \\t                  { string.append('\t'); }
  \\n                  { string.append('\n'); }
  \\f                  { string.append('\f'); }
  \\r                  { string.append('\r'); }
  \\\"                 { string.append('\"'); }
  \\                   { string.append('\\'); }
  \\u[0-9A-Fa-f]{4}    { string.append((char) Integer.parseInt(yytext().substring(2), 16)); }
  \\U[0-9A-Fa-f]{8}    { string.append(Character.toChars(Integer.parseInt(yytext().substring(2), 16))); }

  [^]                  { poisoned = true;}
}

<S_ML_BASIC_STRING> {
	\"\"\"                         { yybegin(YYINITIAL);
                                     return new Token(ML_STRING, yyline, yycolumn, string.toString()); }
    \\{EOL_WS}                     { }

	{ML_BASIC_STRING_CHAR}+        { string.append(yytext()); }

	\"\"                           { string.append("\"\""); }
	\"                             { string.append('"'); }

    \\b                            { string.append('\b'); }
    \\t                            { string.append('\t'); }
    \\n                            { string.append('\n'); }
    \\f                            { string.append('\f'); }
    \\r                            { string.append('\r'); }
    \\\"                           { string.append('\"'); }
    \\                             { string.append('\\'); }
    \\u[0-9A-Fa-f]{4}              { string.append((char) Integer.parseInt(yytext().substring(2), 16)); }
    \\U[0-9A-Fa-f]{8}              { string.append(Character.toChars(Integer.parseInt(yytext().substring(2), 16))); }
}

<S_LITERAL_STRING> {
	'                        { yybegin(YYINITIAL);
                               return new Token(poisoned ? STRING_POISON : STRING, yyline, yycolumn, string.toString()); }

    {LITERAL_STRING_CHAR}+   { string.append(yytext()); }

    [^]                      { poisoned = true;}
}

<S_ML_LITERAL_STRING> {
	'''                         { yybegin(YYINITIAL);
                                  return new Token(poisoned ? ML_STRING_POISON : ML_STRING, yyline, yycolumn, string.toString()); }

	{ML_LITERAL_STRING_CHAR}+   { string.append(yytext()); }

	''                          { string.append("''"); }
	'                           { string.append('\''); }

	[^]                         { poisoned = true; }
}