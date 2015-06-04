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
        if ("0".startsWith(String.valueOf((char) curChar))) {
            curToken = Token.NUMBER;
            while(curString.length() < "0".length()) {
                 curString += (char) curChar;
                 nextChar();
            }
            if(!curString.equals("0")) {
                throw new ParseException("Input doesn't match: expected " + "0" + ", got " + curString, curPos);
            }
        }
        else if ("1".startsWith(String.valueOf((char) curChar))) {
            curToken = Token.NUMBER;
            while(curString.length() < "1".length()) {
                 curString += (char) curChar;
                 nextChar();
            }
            if(!curString.equals("1")) {
                throw new ParseException("Input doesn't match: expected " + "1" + ", got " + curString, curPos);
            }
        }
        else if ("2".startsWith(String.valueOf((char) curChar))) {
            curToken = Token.NUMBER;
            while(curString.length() < "2".length()) {
                 curString += (char) curChar;
                 nextChar();
            }
            if(!curString.equals("2")) {
                throw new ParseException("Input doesn't match: expected " + "2" + ", got " + curString, curPos);
            }
        }
        else if ("3".startsWith(String.valueOf((char) curChar))) {
            curToken = Token.NUMBER;
            while(curString.length() < "3".length()) {
                 curString += (char) curChar;
                 nextChar();
            }
            if(!curString.equals("3")) {
                throw new ParseException("Input doesn't match: expected " + "3" + ", got " + curString, curPos);
            }
        }
        else if ("4".startsWith(String.valueOf((char) curChar))) {
            curToken = Token.NUMBER;
            while(curString.length() < "4".length()) {
                 curString += (char) curChar;
                 nextChar();
            }
            if(!curString.equals("4")) {
                throw new ParseException("Input doesn't match: expected " + "4" + ", got " + curString, curPos);
            }
        }
        else if ("5".startsWith(String.valueOf((char) curChar))) {
            curToken = Token.NUMBER;
            while(curString.length() < "5".length()) {
                 curString += (char) curChar;
                 nextChar();
            }
            if(!curString.equals("5")) {
                throw new ParseException("Input doesn't match: expected " + "5" + ", got " + curString, curPos);
            }
        }
        else if ("6".startsWith(String.valueOf((char) curChar))) {
            curToken = Token.NUMBER;
            while(curString.length() < "6".length()) {
                 curString += (char) curChar;
                 nextChar();
            }
            if(!curString.equals("6")) {
                throw new ParseException("Input doesn't match: expected " + "6" + ", got " + curString, curPos);
            }
        }
        else if ("7".startsWith(String.valueOf((char) curChar))) {
            curToken = Token.NUMBER;
            while(curString.length() < "7".length()) {
                 curString += (char) curChar;
                 nextChar();
            }
            if(!curString.equals("7")) {
                throw new ParseException("Input doesn't match: expected " + "7" + ", got " + curString, curPos);
            }
        }
        else if ("8".startsWith(String.valueOf((char) curChar))) {
            curToken = Token.NUMBER;
            while(curString.length() < "8".length()) {
                 curString += (char) curChar;
                 nextChar();
            }
            if(!curString.equals("8")) {
                throw new ParseException("Input doesn't match: expected " + "8" + ", got " + curString, curPos);
            }
        }
        else if ("9".startsWith(String.valueOf((char) curChar))) {
            curToken = Token.NUMBER;
            while(curString.length() < "9".length()) {
                 curString += (char) curChar;
                 nextChar();
            }
            if(!curString.equals("9")) {
                throw new ParseException("Input doesn't match: expected " + "9" + ", got " + curString, curPos);
            }
        }
        else if ("*".startsWith(String.valueOf((char) curChar))) {
            curToken = Token.MUL;
            while(curString.length() < "*".length()) {
                 curString += (char) curChar;
                 nextChar();
            }
            if(!curString.equals("*")) {
                throw new ParseException("Input doesn't match: expected " + "*" + ", got " + curString, curPos);
            }
        }
        else if ("+".startsWith(String.valueOf((char) curChar))) {
            curToken = Token.PLUS;
            while(curString.length() < "+".length()) {
                 curString += (char) curChar;
                 nextChar();
            }
            if(!curString.equals("+")) {
                throw new ParseException("Input doesn't match: expected " + "+" + ", got " + curString, curPos);
            }
        }
	}
}
