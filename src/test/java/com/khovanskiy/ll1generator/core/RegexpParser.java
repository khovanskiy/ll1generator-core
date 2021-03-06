package com.khovanskiy.ll1generator.core;
import com.khovanskiy.ll1generator.core.Tree;

import java.io.InputStream;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;


public class RegexpParser {


private RegexpLexer lex;

	//members

	public Tree parse(InputStream is, String name) throws ParseException {
		lex = new RegexpLexer(is);
		lex.nextToken();
		return regexp(name);
	}

	private Tree p() throws ParseException {
		switch (lex.curToken()) {
			case OR: {
				return new Tree("P", new Tree("eps"));
            }
			case OPEN_BRACKET: {
				List<Tree> f = new ArrayList<>();
				List<Tree> p = new ArrayList<>();
				f.add(f());
				p.add(p());
				Tree res = new Tree("P", f.get(0), p.get(0)); return res;
            }
			case CHAR: {
				List<Tree> f = new ArrayList<>();
				List<Tree> p = new ArrayList<>();
				f.add(f());
				p.add(p());
				Tree res = new Tree("P", f.get(0), p.get(0)); return res;
            }
			case CLOSE_BRACKET: {
				return new Tree("P", new Tree("eps"));
            }
			case EOF: {
				return new Tree("P", new Tree("eps"));
            }
            default:
                throw new AssertionError();
        }
    }

	private Tree regexp(String name) throws ParseException {
		switch (lex.curToken()) {
			case OPEN_BRACKET: {
				List<Tree> t = new ArrayList<>();
				List<Tree> d = new ArrayList<>();
				t.add(t());
				d.add(d());
				return new Tree("RegExp \"" + name + "\"", t.get(0), d.get(0));
            }
			case CHAR: {
				List<Tree> t = new ArrayList<>();
				List<Tree> d = new ArrayList<>();
				t.add(t());
				d.add(d());
				return new Tree("RegExp \"" + name + "\"", t.get(0), d.get(0));
            }
            default:
                throw new AssertionError();
        }
    }

	private Tree d() throws ParseException {
		switch (lex.curToken()) {
			case OR: {
				List<String> OR = new ArrayList<>();
				List<Tree> t = new ArrayList<>();
				List<Tree> d = new ArrayList<>();
				if (lex.curToken().toString().equals("OR")) {
					OR.add(lex.curString());
				} else {
					throw new AssertionError("OR expected, instead of " + lex.curToken().toString());
				}
				lex.nextToken();
				t.add(t());
				d.add(d());
				return new Tree("D", new Tree("|"), t.get(0), d.get(0));
            }
			case CLOSE_BRACKET: {
				return new Tree("D", new Tree("eps"));
            }
			case EOF: {
				return new Tree("D", new Tree("eps"));
            }
            default:
                throw new AssertionError();
        }
    }

	private Tree t() throws ParseException {
		switch (lex.curToken()) {
			case OPEN_BRACKET: {
				List<Tree> f = new ArrayList<>();
				List<Tree> p = new ArrayList<>();
				f.add(f());
				p.add(p());
				if (f.get(0).temp != null) return new Tree("T+", f.get(0), new Tree("P", new Tree("F", f.get(0).temp, new Tree("W", new Tree("*"), new Tree("W", new Tree("eps")))), p.get(0))); return new Tree("T", f.get(0), p.get(0));
            }
			case CHAR: {
				List<Tree> f = new ArrayList<>();
				List<Tree> p = new ArrayList<>();
				f.add(f());
				p.add(p());
				if (f.get(0).temp != null) return new Tree("T+", f.get(0), new Tree("P", new Tree("F", f.get(0).temp, new Tree("W", new Tree("*"), new Tree("W", new Tree("eps")))), p.get(0))); return new Tree("T", f.get(0), p.get(0));
            }
            default:
                throw new AssertionError();
        }
    }

	private Tree f() throws ParseException {
		switch (lex.curToken()) {
			case OPEN_BRACKET: {
				List<Tree> n = new ArrayList<>();
				List<Tree> w = new ArrayList<>();
				n.add(n());
				w.add(w());
				Tree res = new Tree("F" + w.get(0).getNode(), n.get(0), w.get(0)); if (w.get(0).getNode().equals("+")) res.temp = n.get(0); return res;
            }
			case CHAR: {
				List<Tree> n = new ArrayList<>();
				List<Tree> w = new ArrayList<>();
				n.add(n());
				w.add(w());
				Tree res = new Tree("F" + w.get(0).getNode(), n.get(0), w.get(0)); if (w.get(0).getNode().equals("+")) res.temp = n.get(0); return res;
            }
            default:
                throw new AssertionError();
        }
    }

	private Tree w() throws ParseException {
		switch (lex.curToken()) {
			case OR: {
				return new Tree("W", new Tree("eps"));
            }
			case OPEN_BRACKET: {
				return new Tree("W", new Tree("eps"));
            }
			case ASTERISK: {
				List<String> ASTERISK = new ArrayList<>();
				List<Tree> w = new ArrayList<>();
				if (lex.curToken().toString().equals("ASTERISK")) {
					ASTERISK.add(lex.curString());
				} else {
					throw new AssertionError("ASTERISK expected, instead of " + lex.curToken().toString());
				}
				lex.nextToken();
				w.add(w());
				if (ASTERISK.get(0).equals("*")) return new Tree("*", new Tree("*"), w.get(0)); else return new Tree("+", new Tree("eps"));
            }
			case CHAR: {
				return new Tree("W", new Tree("eps"));
            }
			case CLOSE_BRACKET: {
				return new Tree("W", new Tree("eps"));
            }
			case EOF: {
				return new Tree("W", new Tree("eps"));
            }
            default:
                throw new AssertionError();
        }
    }

	private Tree n() throws ParseException {
		switch (lex.curToken()) {
			case OPEN_BRACKET: {
				List<String> OPEN_BRACKET = new ArrayList<>();
				List<Tree> regexp = new ArrayList<>();
				List<String> CLOSE_BRACKET = new ArrayList<>();
				if (lex.curToken().toString().equals("OPEN_BRACKET")) {
					OPEN_BRACKET.add(lex.curString());
				} else {
					throw new AssertionError("OPEN_BRACKET expected, instead of " + lex.curToken().toString());
				}
				lex.nextToken();
				regexp.add(regexp(""));
				if (lex.curToken().toString().equals("CLOSE_BRACKET")) {
					CLOSE_BRACKET.add(lex.curString());
				} else {
					throw new AssertionError("CLOSE_BRACKET expected, instead of " + lex.curToken().toString());
				}
				lex.nextToken();
				return new Tree("N", new Tree(OPEN_BRACKET.get(0)), regexp.get(0), new Tree(CLOSE_BRACKET.get(0)));
            }
			case CHAR: {
				List<String> CHAR = new ArrayList<>();
				if (lex.curToken().toString().equals("CHAR")) {
					CHAR.add(lex.curString());
				} else {
					throw new AssertionError("CHAR expected, instead of " + lex.curToken().toString());
				}
				lex.nextToken();
				return new Tree("N", new Tree(CHAR.get(0)));
            }
            default:
                throw new AssertionError();
        }
    }

}
