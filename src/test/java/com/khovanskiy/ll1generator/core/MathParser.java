
package com.khovanskiy.ll1generator.core;


import java.io.InputStream;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;


public class MathParser {


private MathLexer lex;

	

	public Integer parse(InputStream is, Integer a, Integer b) throws ParseException {
		lex = new MathLexer(is);
		lex.nextToken();
		return math(a, b);
	}

	private Integer add(Integer a, Integer b) throws ParseException {
		switch (lex.curToken()) {
			case ADD: {
				List<String> ADD = new ArrayList<>();
				if (lex.curToken().toString().equals("ADD")) {
					ADD.add(lex.curString());
				} else {
					throw new AssertionError("ADD expected, instead of " + lex.curToken().toString());
				}
				lex.nextToken();
				return a + b;
            }
            default:
                throw new AssertionError();
        }
    }

	private Integer plus2(Integer a) throws ParseException {
		switch (lex.curToken()) {
			case PLUS2: {
				List<String> PLUS2 = new ArrayList<>();
				if (lex.curToken().toString().equals("PLUS2")) {
					PLUS2.add(lex.curString());
				} else {
					throw new AssertionError("PLUS2 expected, instead of " + lex.curToken().toString());
				}
				lex.nextToken();
				return a + 2;
            }
            default:
                throw new AssertionError();
        }
    }

	private Integer math(Integer a, Integer b) throws ParseException {
		switch (lex.curToken()) {
			case ADD: {
				List<Integer> add = new ArrayList<>();
				add.add(add(a, b));
				return add.get(0);
            }
			case PLUS2: {
				List<Integer> plus2 = new ArrayList<>();
				plus2.add(plus2(a));
				return plus2.get(0);
            }
            default:
                throw new AssertionError();
        }
    }

}
