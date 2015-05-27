
package com.khovanskiy.ll1generator.core;
import com.khovanskiy.ll1generator.core.Tree;

import java.io.InputStream;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

public class RegexpParser {
    private RegexpLexer lex;

//members


    public Tree parse(InputStream is) throws ParseException {
        lex = new RegexpLexer(is);
        lex.nextToken();
        return regexp();
    }

    private Tree p() throws ParseException {
        switch (lex.curToken()) {
            case OR:
            {
{return new Tree("P", new Tree("eps"));}
            }
            case OPEN_BRACKET:
            {
                List<Tree> f = new ArrayList<Tree>();
                List<Tree> p = new ArrayList<Tree>();
                f.add(f());
                p.add(p());
				{return new Tree("P", f.get(0), p.get(0));}
            }
            case CHAR:
            {
                List<Tree> f = new ArrayList<Tree>();
                List<Tree> p = new ArrayList<Tree>();
                f.add(f());
                p.add(p());
				{return new Tree("P", f.get(0), p.get(0));}
            }
            case CLOSE_BRACKET:
            {
{return new Tree("P", new Tree("eps"));}
            }
            case EOF:
            {
{return new Tree("P", new Tree("eps"));}
            }
            default:
                throw new AssertionError();
        }
    }

    private Tree regexp() throws ParseException {
        switch (lex.curToken()) {
            case OPEN_BRACKET:
            {
                List<Tree> t = new ArrayList<Tree>();
                List<Tree> d = new ArrayList<Tree>();
                t.add(t());
                d.add(d());
				{ return new Tree("R", t.get(0), d.get(0)); }
            }
            case CHAR:
            {
                List<Tree> t = new ArrayList<Tree>();
                List<Tree> d = new ArrayList<Tree>();
                t.add(t());
                d.add(d());
				{ return new Tree("R", t.get(0), d.get(0)); }
            }
            default:
                throw new AssertionError();
        }
    }

    private Tree d() throws ParseException {
        switch (lex.curToken()) {
            case OR:
            {
                List<String> OR = new ArrayList<String>();
                List<Tree> t = new ArrayList<Tree>();
                List<Tree> d = new ArrayList<Tree>();
                if (lex.curToken().toString().equals("OR")) {
                    OR.add(lex.curString());
                } else {
                    throw new AssertionError("OR expected, instead of " + lex.curToken().toString());
                }
                lex.nextToken();
                t.add(t());
                d.add(d());
				{ return new Tree("D", new Tree("|"), t.get(0), d.get(0));}
            }
            case CLOSE_BRACKET:
            {
{return new Tree("D", new Tree("eps"));}
            }
            case EOF:
            {
{return new Tree("D", new Tree("eps"));}
            }
            default:
                throw new AssertionError();
        }
    }

    private Tree t() throws ParseException {
        switch (lex.curToken()) {
            case OPEN_BRACKET:
            {
                List<Tree> f = new ArrayList<Tree>();
                List<Tree> p = new ArrayList<Tree>();
                f.add(f());
                p.add(p());
				{return new Tree("T", f.get(0), p.get(0));}
            }
            case CHAR:
            {
                List<Tree> f = new ArrayList<Tree>();
                List<Tree> p = new ArrayList<Tree>();
                f.add(f());
                p.add(p());
				{return new Tree("T", f.get(0), p.get(0));}
            }
            default:
                throw new AssertionError();
        }
    }

    private Tree f() throws ParseException {
        switch (lex.curToken()) {
            case OPEN_BRACKET:
            {
                List<Tree> n = new ArrayList<Tree>();
                List<Tree> w = new ArrayList<Tree>();
                n.add(n());
                w.add(w());
				{return new Tree("F", n.get(0), w.get(0));}
            }
            case CHAR:
            {
                List<Tree> n = new ArrayList<Tree>();
                List<Tree> w = new ArrayList<Tree>();
                n.add(n());
                w.add(w());
				{return new Tree("F", n.get(0), w.get(0));}
            }
            default:
                throw new AssertionError();
        }
    }

    private Tree w() throws ParseException {
        switch (lex.curToken()) {
            case OR:
            {
{return new Tree("W", new Tree("eps"));}
            }
            case OPEN_BRACKET:
            {
{return new Tree("W", new Tree("eps"));}
            }
            case ASTERISK:
            {
                List<String> ASTERISK = new ArrayList<String>();
                List<Tree> w = new ArrayList<Tree>();
                if (lex.curToken().toString().equals("ASTERISK")) {
                    ASTERISK.add(lex.curString());
                } else {
                    throw new AssertionError("ASTERISK expected, instead of " + lex.curToken().toString());
                }
                lex.nextToken();
                w.add(w());
				{return new Tree("W", new Tree(ASTERISK.get(0)), w.get(0));}
            }
            case CHAR:
            {
{return new Tree("W", new Tree("eps"));}
            }
            case CLOSE_BRACKET:
            {
{return new Tree("W", new Tree("eps"));}
            }
            case EOF:
            {
{return new Tree("W", new Tree("eps"));}
            }
            default:
                throw new AssertionError();
        }
    }

    private Tree n() throws ParseException {
        switch (lex.curToken()) {
            case OPEN_BRACKET:
            {
                List<String> OPEN_BRACKET = new ArrayList<String>();
                List<Tree> regexp = new ArrayList<Tree>();
                List<String> CLOSE_BRACKET = new ArrayList<String>();
                if (lex.curToken().toString().equals("OPEN_BRACKET")) {
                    OPEN_BRACKET.add(lex.curString());
                } else {
                    throw new AssertionError("OPEN_BRACKET expected, instead of " + lex.curToken().toString());
                }
                lex.nextToken();
                regexp.add(regexp());
                if (lex.curToken().toString().equals("CLOSE_BRACKET")) {
                    CLOSE_BRACKET.add(lex.curString());
                } else {
                    throw new AssertionError("CLOSE_BRACKET expected, instead of " + lex.curToken().toString());
                }
                lex.nextToken();
				{return new Tree("N", new Tree(OPEN_BRACKET.get(0)), regexp.get(0), new Tree(CLOSE_BRACKET.get(0)));}
            }
            case CHAR:
            {
                List<String> CHAR = new ArrayList<String>();
                if (lex.curToken().toString().equals("CHAR")) {
                    CHAR.add(lex.curString());
                } else {
                    throw new AssertionError("CHAR expected, instead of " + lex.curToken().toString());
                }
                lex.nextToken();
				{return new Tree("N", new Tree(CHAR.get(0)));}
            }
            default:
                throw new AssertionError();
        }
    }

}
