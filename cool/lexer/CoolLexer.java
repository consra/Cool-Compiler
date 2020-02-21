// Generated from /home/costi/cpl_tema1/src/cool/lexer/CoolLexer.g4 by ANTLR 4.7.2

    package cool.lexer;

import org.antlr.v4.runtime.Lexer;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.TokenStream;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.atn.*;
import org.antlr.v4.runtime.dfa.DFA;
import org.antlr.v4.runtime.misc.*;

@SuppressWarnings({"all", "warnings", "unchecked", "unused", "cast"})
public class CoolLexer extends Lexer {
	static { RuntimeMetaData.checkVersion("4.7.2", RuntimeMetaData.VERSION); }

	protected static final DFA[] _decisionToDFA;
	protected static final PredictionContextCache _sharedContextCache =
		new PredictionContextCache();
	public static final int
		ERROR=1, IF=2, THEN=3, ELSE=4, FI=5, CLASS=6, INHERITS=7, BOOL=8, TWOP=9, 
		TYPE=10, NEW=11, LET=12, IN=13, WHILE=14, LOOP=15, POOL=16, CASE=17, ESAC=18, 
		NOT=19, ISVOID=20, OF=21, ARROW=22, ID=23, INT=24, UNEDINGSTRING=25, STRING=26, 
		SEMI=27, COMMA=28, ASSIGN=29, LPAREN=30, RPAREN=31, LBRACE=32, RBRACE=33, 
		PLUS=34, MINUS=35, TLD=36, MULT=37, DIV=38, EQUAL=39, LT=40, LE=41, AT=42, 
		DOT=43, LINE_COMMENT=44, EOF_COMM=45, BLOCK_COMMENT=46, OPEN_COMMENT=47, 
		CLOSE_COMMENT=48, WS=49, Ef=50, UNMATCHED_CHAR=51;
	public static String[] channelNames = {
		"DEFAULT_TOKEN_CHANNEL", "HIDDEN"
	};

	public static String[] modeNames = {
		"DEFAULT_MODE"
	};

	private static String[] makeRuleNames() {
		return new String[] {
			"IF", "THEN", "ELSE", "FI", "CLASS", "INHERITS", "BOOL", "TWOP", "TYPE", 
			"NEW", "LET", "IN", "WHILE", "LOOP", "POOL", "CASE", "ESAC", "NOT", "ISVOID", 
			"OF", "ARROW", "LETTER", "LOWLETTER", "ID", "DIGIT", "INT", "DIGITS", 
			"EXPONENT", "UNEDINGSTRING", "STRING", "SEMI", "COMMA", "ASSIGN", "LPAREN", 
			"RPAREN", "LBRACE", "RBRACE", "PLUS", "MINUS", "TLD", "MULT", "DIV", 
			"EQUAL", "LT", "LE", "AT", "DOT", "NEW_LINE", "LINE_COMMENT", "EOF_COMM", 
			"BLOCK_COMMENT", "OPEN_COMMENT", "CLOSE_COMMENT", "WS", "Ef", "UNMATCHED_CHAR"
		};
	}
	public static final String[] ruleNames = makeRuleNames();

	private static String[] makeLiteralNames() {
		return new String[] {
			null, null, "'if'", "'then'", "'else'", "'fi'", "'class'", "'inherits'", 
			null, "':'", null, "'new'", "'let'", "'in'", "'while'", "'loop'", "'pool'", 
			"'case'", "'esac'", "'not'", "'isvoid'", "'of'", "'=>'", null, null, 
			null, null, "';'", "','", "'<-'", "'('", "')'", "'{'", "'}'", "'+'", 
			"'-'", "'~'", "'*'", "'/'", "'='", "'<'", "'<='", "'@'", "'.'", null, 
			null, null, "'(*'", "'*)'"
		};
	}
	private static final String[] _LITERAL_NAMES = makeLiteralNames();
	private static String[] makeSymbolicNames() {
		return new String[] {
			null, "ERROR", "IF", "THEN", "ELSE", "FI", "CLASS", "INHERITS", "BOOL", 
			"TWOP", "TYPE", "NEW", "LET", "IN", "WHILE", "LOOP", "POOL", "CASE", 
			"ESAC", "NOT", "ISVOID", "OF", "ARROW", "ID", "INT", "UNEDINGSTRING", 
			"STRING", "SEMI", "COMMA", "ASSIGN", "LPAREN", "RPAREN", "LBRACE", "RBRACE", 
			"PLUS", "MINUS", "TLD", "MULT", "DIV", "EQUAL", "LT", "LE", "AT", "DOT", 
			"LINE_COMMENT", "EOF_COMM", "BLOCK_COMMENT", "OPEN_COMMENT", "CLOSE_COMMENT", 
			"WS", "Ef", "UNMATCHED_CHAR"
		};
	}
	private static final String[] _SYMBOLIC_NAMES = makeSymbolicNames();
	public static final Vocabulary VOCABULARY = new VocabularyImpl(_LITERAL_NAMES, _SYMBOLIC_NAMES);

	/**
	 * @deprecated Use {@link #VOCABULARY} instead.
	 */
	@Deprecated
	public static final String[] tokenNames;
	static {
		tokenNames = new String[_SYMBOLIC_NAMES.length];
		for (int i = 0; i < tokenNames.length; i++) {
			tokenNames[i] = VOCABULARY.getLiteralName(i);
			if (tokenNames[i] == null) {
				tokenNames[i] = VOCABULARY.getSymbolicName(i);
			}

			if (tokenNames[i] == null) {
				tokenNames[i] = "<INVALID>";
			}
		}
	}

	@Override
	@Deprecated
	public String[] getTokenNames() {
		return tokenNames;
	}

	@Override

	public Vocabulary getVocabulary() {
		return VOCABULARY;
	}


	    private void raiseError(String msg) {
	        setText(msg);
	        setType(ERROR);
	    }

	    public String expandEscaped(String msg) {
	        String res = msg.replace("\\n", "\n");
	        res = res.replace("\\t", "\t");
	        res = res.replace("\\\\", "\0");
	        res = res.replace("\\", "");
	        res = res.replace("\0", "\\");
	        res = res.substring(1, res.length()-1);
	        return res;
	    }


	public CoolLexer(CharStream input) {
		super(input);
		_interp = new LexerATNSimulator(this,_ATN,_decisionToDFA,_sharedContextCache);
	}

	@Override
	public String getGrammarFileName() { return "CoolLexer.g4"; }

	@Override
	public String[] getRuleNames() { return ruleNames; }

	@Override
	public String getSerializedATN() { return _serializedATN; }

	@Override
	public String[] getChannelNames() { return channelNames; }

	@Override
	public String[] getModeNames() { return modeNames; }

	@Override
	public ATN getATN() { return _ATN; }

	@Override
	public void action(RuleContext _localctx, int ruleIndex, int actionIndex) {
		switch (ruleIndex) {
		case 28:
			UNEDINGSTRING_action((RuleContext)_localctx, actionIndex);
			break;
		case 29:
			STRING_action((RuleContext)_localctx, actionIndex);
			break;
		case 49:
			EOF_COMM_action((RuleContext)_localctx, actionIndex);
			break;
		case 51:
			OPEN_COMMENT_action((RuleContext)_localctx, actionIndex);
			break;
		case 52:
			CLOSE_COMMENT_action((RuleContext)_localctx, actionIndex);
			break;
		case 55:
			UNMATCHED_CHAR_action((RuleContext)_localctx, actionIndex);
			break;
		}
	}
	private void UNEDINGSTRING_action(RuleContext _localctx, int actionIndex) {
		switch (actionIndex) {
		case 0:
			 raiseError("Unterminated string constant"); 
			break;
		case 1:
			 raiseError("EOF in string constant"); 
			break;
		}
	}
	private void STRING_action(RuleContext _localctx, int actionIndex) {
		switch (actionIndex) {
		case 2:
			 raiseError("SString contains null characterr"); 
			break;
		case 3:
			 if (getText().length() > 1024) {
			         raiseError("SString constant too longg");
			      }
			         setText(expandEscaped(getText()));
			      
			break;
		}
	}
	private void EOF_COMM_action(RuleContext _localctx, int actionIndex) {
		switch (actionIndex) {
		case 4:
			 raiseError("EOF in comment"); 
			break;
		}
	}
	private void OPEN_COMMENT_action(RuleContext _localctx, int actionIndex) {
		switch (actionIndex) {
		case 5:
			 raiseError("Unmatched (*"); 
			break;
		}
	}
	private void CLOSE_COMMENT_action(RuleContext _localctx, int actionIndex) {
		switch (actionIndex) {
		case 6:
			 raiseError("Unmatched *)"); 
			break;
		}
	}
	private void UNMATCHED_CHAR_action(RuleContext _localctx, int actionIndex) {
		switch (actionIndex) {
		case 7:
			 raiseError("Invalid character: " + getText()); 
			break;
		}
	}

	public static final String _serializedATN =
		"\3\u608b\ua72a\u8133\ub9ed\u417c\u3be7\u7786\u5964\2\65\u0194\b\1\4\2"+
		"\t\2\4\3\t\3\4\4\t\4\4\5\t\5\4\6\t\6\4\7\t\7\4\b\t\b\4\t\t\t\4\n\t\n\4"+
		"\13\t\13\4\f\t\f\4\r\t\r\4\16\t\16\4\17\t\17\4\20\t\20\4\21\t\21\4\22"+
		"\t\22\4\23\t\23\4\24\t\24\4\25\t\25\4\26\t\26\4\27\t\27\4\30\t\30\4\31"+
		"\t\31\4\32\t\32\4\33\t\33\4\34\t\34\4\35\t\35\4\36\t\36\4\37\t\37\4 \t"+
		" \4!\t!\4\"\t\"\4#\t#\4$\t$\4%\t%\4&\t&\4\'\t\'\4(\t(\4)\t)\4*\t*\4+\t"+
		"+\4,\t,\4-\t-\4.\t.\4/\t/\4\60\t\60\4\61\t\61\4\62\t\62\4\63\t\63\4\64"+
		"\t\64\4\65\t\65\4\66\t\66\4\67\t\67\48\t8\49\t9\3\2\3\2\3\2\3\3\3\3\3"+
		"\3\3\3\3\3\3\4\3\4\3\4\3\4\3\4\3\5\3\5\3\5\3\6\3\6\3\6\3\6\3\6\3\6\3\7"+
		"\3\7\3\7\3\7\3\7\3\7\3\7\3\7\3\7\3\b\3\b\3\b\3\b\3\b\3\b\3\b\3\b\3\b\5"+
		"\b\u009c\n\b\3\t\3\t\3\n\3\n\3\n\3\n\3\n\3\n\3\n\3\n\3\n\3\n\3\n\7\n\u00ab"+
		"\n\n\f\n\16\n\u00ae\13\n\5\n\u00b0\n\n\3\13\3\13\3\13\3\13\3\f\3\f\3\f"+
		"\3\f\3\r\3\r\3\r\3\16\3\16\3\16\3\16\3\16\3\16\3\17\3\17\3\17\3\17\3\17"+
		"\3\20\3\20\3\20\3\20\3\20\3\21\3\21\3\21\3\21\3\21\3\22\3\22\3\22\3\22"+
		"\3\22\3\23\3\23\3\23\3\23\3\24\3\24\3\24\3\24\3\24\3\24\3\24\3\25\3\25"+
		"\3\25\3\26\3\26\3\26\3\27\3\27\3\30\3\30\3\31\3\31\3\31\3\31\7\31\u00f0"+
		"\n\31\f\31\16\31\u00f3\13\31\3\32\3\32\3\33\6\33\u00f8\n\33\r\33\16\33"+
		"\u00f9\3\34\6\34\u00fd\n\34\r\34\16\34\u00fe\3\35\3\35\5\35\u0103\n\35"+
		"\3\35\3\35\3\36\3\36\3\36\3\36\3\36\3\36\3\36\7\36\u010e\n\36\f\36\16"+
		"\36\u0111\13\36\3\36\3\36\3\36\3\36\5\36\u0117\n\36\3\37\3\37\3\37\3\37"+
		"\3\37\3\37\5\37\u011f\n\37\3\37\3\37\3\37\3\37\3\37\7\37\u0126\n\37\f"+
		"\37\16\37\u0129\13\37\3\37\3\37\3\37\3 \3 \3!\3!\3\"\3\"\3\"\3#\3#\3$"+
		"\3$\3%\3%\3&\3&\3\'\3\'\3(\3(\3)\3)\3*\3*\3+\3+\3,\3,\3-\3-\3.\3.\3.\3"+
		"/\3/\3\60\3\60\3\61\5\61\u0153\n\61\3\61\3\61\3\62\3\62\3\62\3\62\7\62"+
		"\u015b\n\62\f\62\16\62\u015e\13\62\3\62\3\62\5\62\u0162\n\62\3\62\3\62"+
		"\3\63\3\63\3\63\7\63\u0169\n\63\f\63\16\63\u016c\13\63\3\63\3\63\3\63"+
		"\3\64\3\64\3\64\7\64\u0174\n\64\f\64\16\64\u0177\13\64\3\64\3\64\3\64"+
		"\3\64\3\65\3\65\3\65\3\65\3\65\3\66\3\66\3\66\3\66\3\66\3\67\6\67\u0188"+
		"\n\67\r\67\16\67\u0189\3\67\3\67\38\38\38\38\39\39\39\7\u010f\u0127\u015c"+
		"\u016a\u0175\2:\3\4\5\5\7\6\t\7\13\b\r\t\17\n\21\13\23\f\25\r\27\16\31"+
		"\17\33\20\35\21\37\22!\23#\24%\25\'\26)\27+\30-\2/\2\61\31\63\2\65\32"+
		"\67\29\2;\33=\34?\35A\36C\37E G!I\"K#M$O%Q&S\'U(W)Y*[+],_-a\2c.e/g\60"+
		"i\61k\62m\63o\64q\65\3\2\f\3\2C\\\6\2\62;C\\aac|\4\2C\\c|\3\2c|\3\2\62"+
		";\4\2--//\3\2$$\4\2\f\f\17\17\3\2+,\5\2\13\f\16\17\"\"\2\u01a7\2\3\3\2"+
		"\2\2\2\5\3\2\2\2\2\7\3\2\2\2\2\t\3\2\2\2\2\13\3\2\2\2\2\r\3\2\2\2\2\17"+
		"\3\2\2\2\2\21\3\2\2\2\2\23\3\2\2\2\2\25\3\2\2\2\2\27\3\2\2\2\2\31\3\2"+
		"\2\2\2\33\3\2\2\2\2\35\3\2\2\2\2\37\3\2\2\2\2!\3\2\2\2\2#\3\2\2\2\2%\3"+
		"\2\2\2\2\'\3\2\2\2\2)\3\2\2\2\2+\3\2\2\2\2\61\3\2\2\2\2\65\3\2\2\2\2;"+
		"\3\2\2\2\2=\3\2\2\2\2?\3\2\2\2\2A\3\2\2\2\2C\3\2\2\2\2E\3\2\2\2\2G\3\2"+
		"\2\2\2I\3\2\2\2\2K\3\2\2\2\2M\3\2\2\2\2O\3\2\2\2\2Q\3\2\2\2\2S\3\2\2\2"+
		"\2U\3\2\2\2\2W\3\2\2\2\2Y\3\2\2\2\2[\3\2\2\2\2]\3\2\2\2\2_\3\2\2\2\2c"+
		"\3\2\2\2\2e\3\2\2\2\2g\3\2\2\2\2i\3\2\2\2\2k\3\2\2\2\2m\3\2\2\2\2o\3\2"+
		"\2\2\2q\3\2\2\2\3s\3\2\2\2\5v\3\2\2\2\7{\3\2\2\2\t\u0080\3\2\2\2\13\u0083"+
		"\3\2\2\2\r\u0089\3\2\2\2\17\u009b\3\2\2\2\21\u009d\3\2\2\2\23\u00af\3"+
		"\2\2\2\25\u00b1\3\2\2\2\27\u00b5\3\2\2\2\31\u00b9\3\2\2\2\33\u00bc\3\2"+
		"\2\2\35\u00c2\3\2\2\2\37\u00c7\3\2\2\2!\u00cc\3\2\2\2#\u00d1\3\2\2\2%"+
		"\u00d6\3\2\2\2\'\u00da\3\2\2\2)\u00e1\3\2\2\2+\u00e4\3\2\2\2-\u00e7\3"+
		"\2\2\2/\u00e9\3\2\2\2\61\u00eb\3\2\2\2\63\u00f4\3\2\2\2\65\u00f7\3\2\2"+
		"\2\67\u00fc\3\2\2\29\u0100\3\2\2\2;\u0106\3\2\2\2=\u0118\3\2\2\2?\u012d"+
		"\3\2\2\2A\u012f\3\2\2\2C\u0131\3\2\2\2E\u0134\3\2\2\2G\u0136\3\2\2\2I"+
		"\u0138\3\2\2\2K\u013a\3\2\2\2M\u013c\3\2\2\2O\u013e\3\2\2\2Q\u0140\3\2"+
		"\2\2S\u0142\3\2\2\2U\u0144\3\2\2\2W\u0146\3\2\2\2Y\u0148\3\2\2\2[\u014a"+
		"\3\2\2\2]\u014d\3\2\2\2_\u014f\3\2\2\2a\u0152\3\2\2\2c\u0156\3\2\2\2e"+
		"\u0165\3\2\2\2g\u0170\3\2\2\2i\u017c\3\2\2\2k\u0181\3\2\2\2m\u0187\3\2"+
		"\2\2o\u018d\3\2\2\2q\u0191\3\2\2\2st\7k\2\2tu\7h\2\2u\4\3\2\2\2vw\7v\2"+
		"\2wx\7j\2\2xy\7g\2\2yz\7p\2\2z\6\3\2\2\2{|\7g\2\2|}\7n\2\2}~\7u\2\2~\177"+
		"\7g\2\2\177\b\3\2\2\2\u0080\u0081\7h\2\2\u0081\u0082\7k\2\2\u0082\n\3"+
		"\2\2\2\u0083\u0084\7e\2\2\u0084\u0085\7n\2\2\u0085\u0086\7c\2\2\u0086"+
		"\u0087\7u\2\2\u0087\u0088\7u\2\2\u0088\f\3\2\2\2\u0089\u008a\7k\2\2\u008a"+
		"\u008b\7p\2\2\u008b\u008c\7j\2\2\u008c\u008d\7g\2\2\u008d\u008e\7t\2\2"+
		"\u008e\u008f\7k\2\2\u008f\u0090\7v\2\2\u0090\u0091\7u\2\2\u0091\16\3\2"+
		"\2\2\u0092\u0093\7v\2\2\u0093\u0094\7t\2\2\u0094\u0095\7w\2\2\u0095\u009c"+
		"\7g\2\2\u0096\u0097\7h\2\2\u0097\u0098\7c\2\2\u0098\u0099\7n\2\2\u0099"+
		"\u009a\7u\2\2\u009a\u009c\7g\2\2\u009b\u0092\3\2\2\2\u009b\u0096\3\2\2"+
		"\2\u009c\20\3\2\2\2\u009d\u009e\7<\2\2\u009e\22\3\2\2\2\u009f\u00a0\7"+
		"U\2\2\u00a0\u00a1\7G\2\2\u00a1\u00a2\7N\2\2\u00a2\u00a3\7H\2\2\u00a3\u00a4"+
		"\7a\2\2\u00a4\u00a5\7V\2\2\u00a5\u00a6\7[\2\2\u00a6\u00a7\7R\2\2\u00a7"+
		"\u00b0\7G\2\2\u00a8\u00ac\t\2\2\2\u00a9\u00ab\t\3\2\2\u00aa\u00a9\3\2"+
		"\2\2\u00ab\u00ae\3\2\2\2\u00ac\u00aa\3\2\2\2\u00ac\u00ad\3\2\2\2\u00ad"+
		"\u00b0\3\2\2\2\u00ae\u00ac\3\2\2\2\u00af\u009f\3\2\2\2\u00af\u00a8\3\2"+
		"\2\2\u00b0\24\3\2\2\2\u00b1\u00b2\7p\2\2\u00b2\u00b3\7g\2\2\u00b3\u00b4"+
		"\7y\2\2\u00b4\26\3\2\2\2\u00b5\u00b6\7n\2\2\u00b6\u00b7\7g\2\2\u00b7\u00b8"+
		"\7v\2\2\u00b8\30\3\2\2\2\u00b9\u00ba\7k\2\2\u00ba\u00bb\7p\2\2\u00bb\32"+
		"\3\2\2\2\u00bc\u00bd\7y\2\2\u00bd\u00be\7j\2\2\u00be\u00bf\7k\2\2\u00bf"+
		"\u00c0\7n\2\2\u00c0\u00c1\7g\2\2\u00c1\34\3\2\2\2\u00c2\u00c3\7n\2\2\u00c3"+
		"\u00c4\7q\2\2\u00c4\u00c5\7q\2\2\u00c5\u00c6\7r\2\2\u00c6\36\3\2\2\2\u00c7"+
		"\u00c8\7r\2\2\u00c8\u00c9\7q\2\2\u00c9\u00ca\7q\2\2\u00ca\u00cb\7n\2\2"+
		"\u00cb \3\2\2\2\u00cc\u00cd\7e\2\2\u00cd\u00ce\7c\2\2\u00ce\u00cf\7u\2"+
		"\2\u00cf\u00d0\7g\2\2\u00d0\"\3\2\2\2\u00d1\u00d2\7g\2\2\u00d2\u00d3\7"+
		"u\2\2\u00d3\u00d4\7c\2\2\u00d4\u00d5\7e\2\2\u00d5$\3\2\2\2\u00d6\u00d7"+
		"\7p\2\2\u00d7\u00d8\7q\2\2\u00d8\u00d9\7v\2\2\u00d9&\3\2\2\2\u00da\u00db"+
		"\7k\2\2\u00db\u00dc\7u\2\2\u00dc\u00dd\7x\2\2\u00dd\u00de\7q\2\2\u00de"+
		"\u00df\7k\2\2\u00df\u00e0\7f\2\2\u00e0(\3\2\2\2\u00e1\u00e2\7q\2\2\u00e2"+
		"\u00e3\7h\2\2\u00e3*\3\2\2\2\u00e4\u00e5\7?\2\2\u00e5\u00e6\7@\2\2\u00e6"+
		",\3\2\2\2\u00e7\u00e8\t\4\2\2\u00e8.\3\2\2\2\u00e9\u00ea\t\5\2\2\u00ea"+
		"\60\3\2\2\2\u00eb\u00f1\5/\30\2\u00ec\u00f0\5-\27\2\u00ed\u00f0\7a\2\2"+
		"\u00ee\u00f0\5\63\32\2\u00ef\u00ec\3\2\2\2\u00ef\u00ed\3\2\2\2\u00ef\u00ee"+
		"\3\2\2\2\u00f0\u00f3\3\2\2\2\u00f1\u00ef\3\2\2\2\u00f1\u00f2\3\2\2\2\u00f2"+
		"\62\3\2\2\2\u00f3\u00f1\3\2\2\2\u00f4\u00f5\t\6\2\2\u00f5\64\3\2\2\2\u00f6"+
		"\u00f8\5\63\32\2\u00f7\u00f6\3\2\2\2\u00f8\u00f9\3\2\2\2\u00f9\u00f7\3"+
		"\2\2\2\u00f9\u00fa\3\2\2\2\u00fa\66\3\2\2\2\u00fb\u00fd\5\63\32\2\u00fc"+
		"\u00fb\3\2\2\2\u00fd\u00fe\3\2\2\2\u00fe\u00fc\3\2\2\2\u00fe\u00ff\3\2"+
		"\2\2\u00ff8\3\2\2\2\u0100\u0102\7g\2\2\u0101\u0103\t\7\2\2\u0102\u0101"+
		"\3\2\2\2\u0102\u0103\3\2\2\2\u0103\u0104\3\2\2\2\u0104\u0105\5\67\34\2"+
		"\u0105:\3\2\2\2\u0106\u010f\7$\2\2\u0107\u0108\7^\2\2\u0108\u0109\7\17"+
		"\2\2\u0109\u010e\7\f\2\2\u010a\u010b\7^\2\2\u010b\u010e\7\f\2\2\u010c"+
		"\u010e\n\b\2\2\u010d\u0107\3\2\2\2\u010d\u010a\3\2\2\2\u010d\u010c\3\2"+
		"\2\2\u010e\u0111\3\2\2\2\u010f\u0110\3\2\2\2\u010f\u010d\3\2\2\2\u0110"+
		"\u0116\3\2\2\2\u0111\u010f\3\2\2\2\u0112\u0113\t\t\2\2\u0113\u0117\b\36"+
		"\2\2\u0114\u0115\7\2\2\3\u0115\u0117\b\36\3\2\u0116\u0112\3\2\2\2\u0116"+
		"\u0114\3\2\2\2\u0117<\3\2\2\2\u0118\u0127\7$\2\2\u0119\u011a\7^\2\2\u011a"+
		"\u011b\7\17\2\2\u011b\u011f\7\f\2\2\u011c\u011d\7^\2\2\u011d\u011f\7\f"+
		"\2\2\u011e\u0119\3\2\2\2\u011e\u011c\3\2\2\2\u011f\u0126\3\2\2\2\u0120"+
		"\u0121\7\2\2\2\u0121\u0126\b\37\4\2\u0122\u0126\n\t\2\2\u0123\u0124\7"+
		"^\2\2\u0124\u0126\13\2\2\2\u0125\u011e\3\2\2\2\u0125\u0120\3\2\2\2\u0125"+
		"\u0122\3\2\2\2\u0125\u0123\3\2\2\2\u0126\u0129\3\2\2\2\u0127\u0128\3\2"+
		"\2\2\u0127\u0125\3\2\2\2\u0128\u012a\3\2\2\2\u0129\u0127\3\2\2\2\u012a"+
		"\u012b\7$\2\2\u012b\u012c\b\37\5\2\u012c>\3\2\2\2\u012d\u012e\7=\2\2\u012e"+
		"@\3\2\2\2\u012f\u0130\7.\2\2\u0130B\3\2\2\2\u0131\u0132\7>\2\2\u0132\u0133"+
		"\7/\2\2\u0133D\3\2\2\2\u0134\u0135\7*\2\2\u0135F\3\2\2\2\u0136\u0137\7"+
		"+\2\2\u0137H\3\2\2\2\u0138\u0139\7}\2\2\u0139J\3\2\2\2\u013a\u013b\7\177"+
		"\2\2\u013bL\3\2\2\2\u013c\u013d\7-\2\2\u013dN\3\2\2\2\u013e\u013f\7/\2"+
		"\2\u013fP\3\2\2\2\u0140\u0141\7\u0080\2\2\u0141R\3\2\2\2\u0142\u0143\7"+
		",\2\2\u0143T\3\2\2\2\u0144\u0145\7\61\2\2\u0145V\3\2\2\2\u0146\u0147\7"+
		"?\2\2\u0147X\3\2\2\2\u0148\u0149\7>\2\2\u0149Z\3\2\2\2\u014a\u014b\7>"+
		"\2\2\u014b\u014c\7?\2\2\u014c\\\3\2\2\2\u014d\u014e\7B\2\2\u014e^\3\2"+
		"\2\2\u014f\u0150\7\60\2\2\u0150`\3\2\2\2\u0151\u0153\7\17\2\2\u0152\u0151"+
		"\3\2\2\2\u0152\u0153\3\2\2\2\u0153\u0154\3\2\2\2\u0154\u0155\7\f\2\2\u0155"+
		"b\3\2\2\2\u0156\u0157\7/\2\2\u0157\u0158\7/\2\2\u0158\u015c\3\2\2\2\u0159"+
		"\u015b\13\2\2\2\u015a\u0159\3\2\2\2\u015b\u015e\3\2\2\2\u015c\u015d\3"+
		"\2\2\2\u015c\u015a\3\2\2\2\u015d\u0161\3\2\2\2\u015e\u015c\3\2\2\2\u015f"+
		"\u0162\5a\61\2\u0160\u0162\7\2\2\3\u0161\u015f\3\2\2\2\u0161\u0160\3\2"+
		"\2\2\u0162\u0163\3\2\2\2\u0163\u0164\b\62\6\2\u0164d\3\2\2\2\u0165\u016a"+
		"\5i\65\2\u0166\u0169\5g\64\2\u0167\u0169\n\n\2\2\u0168\u0166\3\2\2\2\u0168"+
		"\u0167\3\2\2\2\u0169\u016c\3\2\2\2\u016a\u016b\3\2\2\2\u016a\u0168\3\2"+
		"\2\2\u016b\u016d\3\2\2\2\u016c\u016a\3\2\2\2\u016d\u016e\7\2\2\3\u016e"+
		"\u016f\b\63\7\2\u016ff\3\2\2\2\u0170\u0175\5i\65\2\u0171\u0174\5g\64\2"+
		"\u0172\u0174\13\2\2\2\u0173\u0171\3\2\2\2\u0173\u0172\3\2\2\2\u0174\u0177"+
		"\3\2\2\2\u0175\u0176\3\2\2\2\u0175\u0173\3\2\2\2\u0176\u0178\3\2\2\2\u0177"+
		"\u0175\3\2\2\2\u0178\u0179\5k\66\2\u0179\u017a\3\2\2\2\u017a\u017b\b\64"+
		"\6\2\u017bh\3\2\2\2\u017c\u017d\7*\2\2\u017d\u017e\7,\2\2\u017e\u017f"+
		"\3\2\2\2\u017f\u0180\b\65\b\2\u0180j\3\2\2\2\u0181\u0182\7,\2\2\u0182"+
		"\u0183\7+\2\2\u0183\u0184\3\2\2\2\u0184\u0185\b\66\t\2\u0185l\3\2\2\2"+
		"\u0186\u0188\t\13\2\2\u0187\u0186\3\2\2\2\u0188\u0189\3\2\2\2\u0189\u0187"+
		"\3\2\2\2\u0189\u018a\3\2\2\2\u018a\u018b\3\2\2\2\u018b\u018c\b\67\6\2"+
		"\u018cn\3\2\2\2\u018d\u018e\7\2\2\3\u018e\u018f\3\2\2\2\u018f\u0190\b"+
		"8\6\2\u0190p\3\2\2\2\u0191\u0192\13\2\2\2\u0192\u0193\b9\n\2\u0193r\3"+
		"\2\2\2\31\2\u009b\u00ac\u00af\u00ef\u00f1\u00f9\u00fe\u0102\u010d\u010f"+
		"\u0116\u011e\u0125\u0127\u0152\u015c\u0161\u0168\u016a\u0173\u0175\u0189"+
		"\13\3\36\2\3\36\3\3\37\4\3\37\5\b\2\2\3\63\6\3\65\7\3\66\b\39\t";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}