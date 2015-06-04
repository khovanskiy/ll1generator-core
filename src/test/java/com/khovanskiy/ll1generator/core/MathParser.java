package com.khovanskiy.ll1generator.core;

import java.io.InputStream;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;


public class MathParser {


private MathLexer lex;

	

	public Integer parse(InputStream is) throws ParseException {
		lex = new MathLexer(is);
		lex.nextToken();
		return math();
	}

	private Integer mul(Integer a, Integer b) throws ParseException {
		switch (lex.curToken()) {
			case MUL: {
				List<String> MUL = new ArrayList<>();
				if (lex.curToken().toString().equals("MUL")) {
					MUL.add(lex.curString());
				} else {
					throw new AssertionError("MUL expected, instead of " + lex.curToken().toString());
				}
				lex.nextToken();
				return a * b;
            }
            default:
                throw new AssertionError();
        }
    }

	private Integer binary(Integer a, Integer b) throws ParseException {
		switch (lex.curToken()) {
			case MUL: {
				List<Integer> mul = new ArrayList<>();
				mul.add(mul(a, b));
				return mul.get(0);
            }
			case PLUS: {
				List<Integer> plus = new ArrayList<>();
				plus.add(plus(a, b));
				return plus.get(0);
            }
            default:
                throw new AssertionError();
        }
    }

	private Integer math() throws ParseException {
		switch (lex.curToken()) {
			case NUMBER: {
				List<Integer> operation = new ArrayList<>();
				operation.add(operation());
				return operation.get(0);
            }
            default:
                throw new AssertionError();
        }
    }

	private Integer operation() throws ParseException {
		switch (lex.curToken()) {
			case NUMBER: {
				List<String> NUMBER = new ArrayList<>();
				List<Integer> binary = new ArrayList<>();
				if (lex.curToken().toString().equals("NUMBER")) {
					NUMBER.add(lex.curString());
				} else {
					throw new AssertionError("NUMBER expected, instead of " + lex.curToken().toString());
				}
				lex.nextToken();
				if (lex.curToken().toString().equals("NUMBER")) {
					NUMBER.add(lex.curString());
				} else {
					throw new AssertionError("NUMBER expected, instead of " + lex.curToken().toString());
				}
				lex.nextToken();
				binary.add(binary(Integer.parseInt(NUMBER.get(0)), Integer.parseInt(NUMBER.get(1))));
				return binary.get(0);
            }
            default:
                throw new AssertionError();
        }
    }

	private Integer plus(Integer a, Integer b) throws ParseException {
		switch (lex.curToken()) {
			case PLUS: {
				List<String> PLUS = new ArrayList<>();
				if (lex.curToken().toString().equals("PLUS")) {
					PLUS.add(lex.curString());
				} else {
					throw new AssertionError("PLUS expected, instead of " + lex.curToken().toString());
				}
				lex.nextToken();
				return a + b;
            }
            default:
                throw new AssertionError();
        }
    }

}
