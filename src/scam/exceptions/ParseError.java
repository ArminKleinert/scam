package scam.exceptions;

public class ParseError extends LispError {
	
	private int pos = 0;
	private int lineNr = 0;
	private boolean interactive;
	private String line = "";
	private final String expected;
	private final String found;
	private String desc;
	
	public ParseError(String expected, String found, String desc, String line, int lineNr, int pos) {
		this.expected = expected;
		this.found = found;
		this.desc = desc;
		this.line = line;
		this.lineNr = lineNr;
		this.pos = pos;
	}
		
	public ParseError(String expected, String found, String line, int lineNr, int pos) {
		this.expected = expected;
		this.found = found;
		this.desc = null;
		this.line = line;
		this.lineNr = lineNr;
		this.pos = pos;
	}
	
	public ParseError(String desc, String line, int lineNr, int pos) {
		this.expected = null;
		this.found = null;
		this.desc = desc;
		this.line = line;
		this.lineNr = lineNr;
		this.pos = pos;
	}

		
	public String getMessage() {
		String message = "Syntax Error. ";
		
		if (desc != null) {
			message += desc += ". ";
		}
		
		if (expected != null) {
			message += "Expected " + expected + "";

			if (found != null) {
				message += ", found \"" + found + "\". ";
			} else {
				message += ". ";
			}
		}
		
		if (lineNr > 0) {
			message += "Line number " + lineNr + ", ";
		}
		
		message += "Position " + pos + ", ";
		
		message += "Line: " + line;
						
		return message;
	}


}
