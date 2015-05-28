
package com.khovanskiy.ll1generator.core;


import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;

public class MathLexer {
	private InputStream is;
	private int curChar;
	private int curPos;
	private Token curToken;
	private String curString;

	public MathLexer(InputStream is) throws ParseException {
		this.is = is;
		curPos = 0;
		nextChar();
	}

	private boolean isBlank(int c) { return c == ' ' || c == '\r' || c == '\n' || c == '\t'; }

	private void nextChar() throws ParseException {
		curPos++;
		try {
			curChar = is.read();
		} catch (IOException e) {
			throw new ParseException(e.getMessage(), curPos);
		}
	}

	public Token curToken() {
		return curToken;
	}

	public int curPos() {
		return curPos;
	}

	public String curString() {
		return curString;
	}

	public void nextToken() throws ParseException {
		curString = "";

		if (curChar == -1) {
			curToken = Token.EOF;
			return;
		}
        if ("add ".startsWith(String.valueOf((char) curChar))) {
            curToken = Token.ADD;
            while(curString.length() < "add ".length()) {
                 curString += (char) curChar;
                 nextChar();
            }
            if(!curString.equals("add ")) {
                throw new ParseException("Input doesn't match: expected " + "add " + ", got " + curString, curPos);
            }
        }
        else if ("plus2 ".startsWith(String.valueOf((char) curChar))) {
            curToken = Token.PLUS2;
            while(curString.length() < "plus2 ".length()) {
                 curString += (char) curChar;
                 nextChar();
            }
            if(!curString.equals("plus2 ")) {
                throw new ParseException("Input doesn't match: expected " + "plus2 " + ", got " + curString, curPos);
            }
        }
	}
}
