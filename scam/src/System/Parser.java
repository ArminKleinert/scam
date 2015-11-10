package System;

import java.io.*;

import Exceptions.ParseError;
import LispObjects.Cons;
import LispObjects.LString;
import LispObjects.LispObject;
import LispObjects.NIL;
import LispObjects.NumberInteger;
import LispObjects.NumberRational;
import LispObjects.NumberReal;
import LispObjects.Symbol;

/**
 * SC@M parser. At construction time, the parser must be given a Reader object to read from.
 * The main method to use is parseObject(), which reads the first S-Expression from the reader
 * object and returns it.
 * 
 */
public class Parser {

	/* Definitions of characters */
	private static final String numericChars = "0123456789";
	private static final String extendedChars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz+-.*/<=>!?:$%_&~^@";
	
	/* Reader used for input */
	private Reader in;

	/* Parser state */
	private boolean interactive;
	private String s = "";
	private int pos = 0;
	private int line = 0;

	/**
	 * Construct parser object with given reader used for input. If the interactive parameter
	 * is set to true, then the parser will output, on the console, a "@ >" line before reading 
	 * an S-Expression and will repeat the remainder of every line after reading one S-Expression
	 * on the next line. These features define a simple command line, where the reader object is
	 * the console input.
	 *  
	 * @param in Reader object to read from.
	 * @param interactive Interactive mode.
	 */
	public Parser(Reader in, boolean interactive) {
		this.in = in;
		interactive = false;
	}

	/**
	 * Parse input, expect and return any LispObject (S-Expression).
	 * 
	 * @return First LispObject provided by reader.
	 * @throws ParseError
	 */
	public LispObject parseObject() throws ParseError {
		if (isInteractive()) System.out.print("@ " + line + " >");
		LispObject result;
		
		// parse and return object
		try {
			// Initialize buffer, return null for EOF
			try {
				nextChar();
			} catch (EOFException e) {
				return null;
			}

			// print buffer 'left over'
			if (isInteractive() && getCurrentPos() != 0) {
				System.out.println(s.substring(pos, s.length()));
			}
			result = parseSExpr();

		} catch (IOException e) {
			throw new ParseError("I/O error: " + e.getMessage(), getCurrentLine(), getCurrentLineNumber(), getCurrentPos());
		}
		return result;
	}
			
	/**
	 * Parse input, expect and return an S-Expression.
     *
	 * This method corresponds to the rule 	SEXPR --> number | symbol | "(" TAIL | ' SEXPR | "..."
	 * 
	 * @return LispObject resulting from parsing input.
	 * @throws IOException
	 * @throws ParseError
	 */
	private LispObject parseSExpr() throws IOException, ParseError {
		
		char c = getChar();
		
		if (c == '(') return parseTail(')');
		if (c == '[') return parseTail(']');

		if (c == '\'') {
			nextChar();
			return new Cons(new Symbol("quote"), new Cons(parseSExpr()));
		}
		
		if (c == ')' || c == ']') throw new ParseError("expression", "'"+c+"'", "Parenthesis mismatch", getCurrentLine(), getCurrentLineNumber(), getCurrentPos());
		
		String token = getToken();
		if (isRationalNumber(token)) return new NumberRational(token);
		if (isIntegerNumber(token)) return new NumberInteger(Integer.parseInt(token));
		if (isRealNumber(token)) return new NumberReal(Float.parseFloat(token));
		if (isString(token)) return new LString(token.substring(1, token.length()-1));
		if (isSymbol(token)) return new Symbol(token);
		
		throw new ParseError("expression", token, getCurrentLine(), getCurrentLineNumber(), getCurrentPos());
	}
	
	/**
	 * Parse input, expect and return a tail segment of a list.
	 *
	 * This method corresponds to the rule 	TAIL --> ')' | '.' SEXPR ')' | SEXPR TAIL
	 * 
	 * @return LispObject resulting from parsing input.
	 * @throws IOException
	 * @throws ParseError
	 */
	private LispObject parseTail(char closedBy) throws ParseError, IOException {
		
		nextChar();
		char c = getChar();
		
		if (c == closedBy) return NIL.instance;
		
		if (c == '.') {
			nextChar();
			LispObject cdr = parseSExpr();
			
			nextChar();
			if (getChar() != closedBy) throw new ParseError("'"+closedBy+"'", "" + getChar(), "Parenthesis mismatch", getCurrentLine(), getCurrentLineNumber(), getCurrentPos());
			return cdr;
		}
		
		Cons pair = new Cons(parseSExpr(), parseTail(closedBy));
		return pair;
	}
	
	/**
	 * Read one line (until EOF) from the input and put it in the character buffer.
	 * 
	 * @throws IOException
	 */
	private void readLine() throws IOException {
		line++;
		StringBuffer sbuf = new StringBuffer();
		int c;
		while ( (c = in.read()) != 10) {
			if (c == -1) break;
			if (c != 13) sbuf.append( (char)c );
		}
		if (sbuf.length() == 0 && c == -1) throw new EOFException();
		s = sbuf.toString();
	}
	
	/**
	 * Continue reading until the getChar() method will return the first character
	 * after a sequence of spaces, tabs, or comments.
	 * 
	 * @throws IOException
	 */
	private void nextChar() throws ParseError, IOException {
		pos++;
		if (pos >= s.length()) {
			readLine();
			// Reposition	
			pos = -1;
			nextChar();
		} else {
			// skip passChars
			char c = s.charAt(pos);
			if (c == ' ' || c == '\t') nextChar();
		}
		// skip to the next line when comment is encountered
		if (getChar() == ';') {
			readLine();
			pos = -1;
			nextChar();
		}
	}

	/**
	 * @return The character at the current position in the buffer.
	 * @throws ParseError
	 */
	private char getChar() throws ParseError {
		if (pos >= s.length()) throw new ParseError("character", "end of line", getCurrentLine(), getCurrentLineNumber(), getCurrentPos());
		return s.charAt(pos);
	}
	
	/**
	 * @return Token at current position in buffer.
	 * @throws IOException
	 * @throws ParseError
	 */
	private String getToken() throws IOException, ParseError {
		// Check if EOL has been reached, read new char if yes
		if (pos >= s.length()) {
			nextChar();
		}
		StringBuffer sbuf = new StringBuffer();
		
		if (getChar() == '"') {
			sbuf.append('"');
			pos++;
			while ((getChar() != '"')) {
				sbuf.append(getChar());
				pos++;
			}
			sbuf.append('"');
			pos++;
		} else {
			// Get all characters until EOL or space, tab, paranthese or quote
			while (pos != s.length() &&  (" \t()'".indexOf(getChar()) == -1)) {
				sbuf.append(s.charAt(pos));
				pos++;
			}
		}
		
		// Current position must still point to this token, so decrease pos
		pos--;
		
		//if (sbuf.length() == 0) throw new LispError("Expected number, symbol or string at " + pos + ", found " + s.substring(pos, s.length()));
		return sbuf.toString();
	}

	/**
	 * @param token
	 * @return True iff given token is an integer.
	 */
	private boolean isIntegerNumber(String token) {
		if (token.equals("")) return false;
		// Starts with + or - is OK
		if ((token.startsWith("-") || token.startsWith("+")) && token.length() > 1) token = token.substring(1, token.length());
		// characters must be numeric
		for (int i = 0; i < token.length(); i++) {
			if (numericChars.indexOf(token.charAt(i)) == -1) return false;
		}
		return true;
	}
	
	/**
	 * @param token
	 * @return True iff given token is a nr
	 */
	private boolean isRealNumber(String token) {
		if ((token.equals("")) || (token.equals("."))) return false;
		// Starts with + or - is OK
		if ((token.startsWith("-") || token.startsWith("+")) && token.length() > 1) token = token.substring(1, token.length());
		// characters must be numeric
		for (int i = 0; i < token.length(); i++) {
			if ((numericChars+".").indexOf(token.charAt(i)) == -1) return false;
		}
		return true;
	}
	
	/**
	 * @param token
	 * @return True iff given token is a rational number
	 */
	private boolean isRationalNumber(String token) {
		// Starts with + or - is OK
		int n;
		if ((n = token.indexOf('/')) == -1) return false;
		if (token.indexOf('/', n+1) != -1) return false;
		return (isIntegerNumber(token.substring(0, n)) && isIntegerNumber(token.substring(n+1,token.length())));
	}
	
	/**
	 * @param token
	 * @return True iff given token is a symbol
	 */
	private boolean isSymbol(String token) {
		if (token.equals("")) return false;
		// first char must be extendedChar
		if (extendedChars.indexOf(token.charAt(0)) == -1) return false;
		// a single dot is no symbol
		if (".".equals(token)) return false;
		// rest must be extendedChar or numericChar
		for (int i = 1; i < token.length(); i++) {
			if ((extendedChars + numericChars).indexOf(token.charAt(i)) == -1) return false;
		}
		return true;
	}
	
	/**
	 * @param token
	 * @return True iff given token is a quoted string
	 */
	private boolean isString(String token) {
		if ((token == "") || (token.length() < 2))return false;
		return (token.startsWith("\"") && token.endsWith("\""));
	}
	
	/**
	 * @return Current buffer
	 */
	public String getCurrentLine() {
		return s;
	}
	
	/**
	 * @return Current line number (interactive only).
	 */
	public int getCurrentLineNumber() {
		return line;
	}
	
	/**
	 * @return Current position in buffer.
	 */
	public int getCurrentPos() {
		return pos;
	}
	
	/**
	 * @return True if parser is in interactive mode (see constructor).
	 */
	public boolean isInteractive() {
		return interactive;
	}
}
