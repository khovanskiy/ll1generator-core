
package com.khovanskiy.ll1generator.core;
import com.khovanskiy.ll1generator.core.Tree;

import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;

public class RegexpLexer {
    private InputStream is;
    private int curChar;
    private int curPos;
    private Token curToken;
    private String curString;
    
    public RegexpLexer(InputStream is) throws ParseException {
        this.is = is;
        curPos = 0;
        nextChar();
    }

    private void nextChar() throws ParseException {
        curPos++;
        try {
            curChar = is.read();
        } catch (IOException e) {
            throw new ParseException(e.getMessage(), curPos);
        }
    }

    public Token curToken() { return curToken; }

    public int curPos() { return curPos; }

    public String curString() { return curString; }

    public void nextToken() throws ParseException {
        curString = "";
        if (curChar == -1) {
            curToken = Token.EOF;
            return;
        }

        if ("|".startsWith(String.valueOf((char) curChar))) {
            curToken = Token.OR;
            while(curString.length() < "|".length()) {
                 curString += (char) curChar;
                 nextChar();
            }
            if(!curString.equals("|")) {
                throw new ParseException("Input doesn't match: expected " + "|" + ", got " + curString, curPos);
            }
        }
        else if ("(".startsWith(String.valueOf((char) curChar))) {
            curToken = Token.OPEN_BRACKET;
            while(curString.length() < "(".length()) {
                 curString += (char) curChar;
                 nextChar();
            }
            if(!curString.equals("(")) {
                throw new ParseException("Input doesn't match: expected " + "(" + ", got " + curString, curPos);
            }
        }
        else if (")".startsWith(String.valueOf((char) curChar))) {
            curToken = Token.CLOSE_BRACKET;
            while(curString.length() < ")".length()) {
                 curString += (char) curChar;
                 nextChar();
            }
            if(!curString.equals(")")) {
                throw new ParseException("Input doesn't match: expected " + ")" + ", got " + curString, curPos);
            }
        }
        else if ("*".startsWith(String.valueOf((char) curChar))) {
            curToken = Token.ASTERISK;
            while(curString.length() < "*".length()) {
                 curString += (char) curChar;
                 nextChar();
            }
            if(!curString.equals("*")) {
                throw new ParseException("Input doesn't match: expected " + "*" + ", got " + curString, curPos);
            }
        }
        else if ("a".startsWith(String.valueOf((char) curChar))) {
            curToken = Token.CHAR;
            while(curString.length() < "a".length()) {
                 curString += (char) curChar;
                 nextChar();
            }
            if(!curString.equals("a")) {
                throw new ParseException("Input doesn't match: expected " + "a" + ", got " + curString, curPos);
            }
        }
        else if ("b".startsWith(String.valueOf((char) curChar))) {
            curToken = Token.CHAR;
            while(curString.length() < "b".length()) {
                 curString += (char) curChar;
                 nextChar();
            }
            if(!curString.equals("b")) {
                throw new ParseException("Input doesn't match: expected " + "b" + ", got " + curString, curPos);
            }
        }
        else if ("c".startsWith(String.valueOf((char) curChar))) {
            curToken = Token.CHAR;
            while(curString.length() < "c".length()) {
                 curString += (char) curChar;
                 nextChar();
            }
            if(!curString.equals("c")) {
                throw new ParseException("Input doesn't match: expected " + "c" + ", got " + curString, curPos);
            }
        }
        else if ("d".startsWith(String.valueOf((char) curChar))) {
            curToken = Token.CHAR;
            while(curString.length() < "d".length()) {
                 curString += (char) curChar;
                 nextChar();
            }
            if(!curString.equals("d")) {
                throw new ParseException("Input doesn't match: expected " + "d" + ", got " + curString, curPos);
            }
        }
        else if ("e".startsWith(String.valueOf((char) curChar))) {
            curToken = Token.CHAR;
            while(curString.length() < "e".length()) {
                 curString += (char) curChar;
                 nextChar();
            }
            if(!curString.equals("e")) {
                throw new ParseException("Input doesn't match: expected " + "e" + ", got " + curString, curPos);
            }
        }
        else if ("f".startsWith(String.valueOf((char) curChar))) {
            curToken = Token.CHAR;
            while(curString.length() < "f".length()) {
                 curString += (char) curChar;
                 nextChar();
            }
            if(!curString.equals("f")) {
                throw new ParseException("Input doesn't match: expected " + "f" + ", got " + curString, curPos);
            }
        }
    }
}
