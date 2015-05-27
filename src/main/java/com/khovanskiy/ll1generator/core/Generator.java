package com.khovanskiy.ll1generator.core;

import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.misc.NotNull;
import org.antlr.v4.runtime.tree.ParseTreeWalker;
import org.antlr.v4.runtime.tree.TerminalNode;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;

/**
 * @author Victo Khovanskiy
 */
public class Generator {
    private boolean DEBUG;
    private static final String GEN_DIR = "../java/com/khovanskiy/ll1generator/core";
    private static final String EOF = "EOF";
    private static final String EPS = "EPS";

    private String grammar_name;
    private Item start;

    private final HashMap<String, Item> nonTerminals = new HashMap<>();
    private final HashMap<String, Item> terminals = new HashMap<>();
    private final HashMap<String, HashSet<String>> first = new HashMap<>();
    private final HashMap<String, HashSet<String>> follow = new HashMap<>();

    private String members = "", header = "";

    public Generator(boolean debug) {
        this.DEBUG = debug;
    }

    public void read(File file, String startRule) throws IOException {
        grammar_name = file.getName().split("[.]")[0];
        ANTLRInputStream input = new ANTLRInputStream(new FileInputStream(file));
        GrammarLexer lexer = new GrammarLexer(input);
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        GrammarParser parser = new GrammarParser(tokens);

        ParseTreeWalker walker = new ParseTreeWalker();
        GrammarBaseListener visitor = new GrammarBaseListener() {
            @Override
            public void enterNonTerminalLabel(@NotNull GrammarParser.NonTerminalLabelContext ctx) {
                String name = ctx.NON_TERM_NAME().getText();
                Item curNode = getNonTerm(name);

                if(ctx.synthesized() != null) {
                    if(ctx.synthesized().NON_TERM_NAME() != null) {
                        curNode.setRetType(ctx.synthesized().NON_TERM_NAME().getText());
                    } else if (ctx.synthesized().TERM_NAME() != null) {
                        curNode.setRetType(ctx.synthesized().TERM_NAME().getText());
                    } else {
                        curNode.setRetType(ctx.synthesized().MIXED_CASE().getText());
                    }
                }

                for (GrammarParser.Non_term_prodContext non_term_prodCtx : ctx.non_term_prod()) {
                    Production production = new Production();

                    if (non_term_prodCtx.non_term_prod_helper().isEmpty()) {
                        production.add(getTerm(EPS).getName());
                    }

                    for (GrammarParser.Non_term_prod_helperContext non_term_prod_helperCtx : non_term_prodCtx.non_term_prod_helper()) {
                        if (non_term_prod_helperCtx.NON_TERM_NAME() != null) {
                            production.add(non_term_prod_helperCtx.NON_TERM_NAME().getText());
                        } else {
                            production.add(non_term_prod_helperCtx.TERM_NAME().getText());
                        }
                    }

                    if(non_term_prodCtx.JAVA_CODE() != null) {
                        production.setJavaCode(non_term_prodCtx.JAVA_CODE().getText());
                    }

                    curNode.add(production);
                }
            }

            @Override
            public void enterTerminalLabel(@NotNull GrammarParser.TerminalLabelContext ctx) {
                String name = ctx.TERM_NAME().getText();
                Item curNode = getTerm(name);

                for (GrammarParser.Term_prodContext term_prodCtx : ctx.term_prod()) {
                    Production production = new Production();
                    String s = "";
                    for (TerminalNode term : term_prodCtx.STRING()) {
                        s += term.getText().substring(1);
                        s = s.substring(0, s.length() - 1);
                    }
                    production.add(s);
                    curNode.add(production);
                }
            }

            @Override
            public void enterMembersLabel(@NotNull GrammarParser.MembersLabelContext ctx) {
                if(ctx.JAVA_CODE() != null) {
                    members = ctx.JAVA_CODE().getText().substring(1);
                    members = members.substring(0, members.length() - 1);
                }
            }

            @Override
            public void enterHeaderLabel(@NotNull GrammarParser.HeaderLabelContext ctx) {
                if(ctx.JAVA_CODE() != null) {
                    header = ctx.JAVA_CODE().getText().substring(1);
                    header = header.substring(0, header.length() - 1);
                }
            }
        };

        walker.walk(visitor, parser.gram());

        getTerm(EOF);

        if (!nonTerminals.containsKey(startRule)) {
            throw new RuntimeException("No such rule " + startRule);
        }
        start = getNonTerm(startRule);

        if (DEBUG) {
            System.out.println(grammar_name);
            System.out.println(start.getName());
            System.out.println("=====================================");
            System.out.println("Nonterminals:");
            printMap(nonTerminals);
            System.out.println("=====================================");
            System.out.println("Terminals:");
            printMap(terminals);
        }
    }

    public static void printMap(Map mp) {
        for (Object o : mp.entrySet()) {
            Map.Entry pairs = (Map.Entry) o;
            System.out.println(pairs.getKey() + " = " + pairs.getValue());
        }
    }

    private Item getTerm(String name) {
        if (!terminals.containsKey(name)) {
            terminals.put(name, new Item(name));
        }
        return terminals.get(name);
    }

    private Item getNonTerm(String name) {
        if (!nonTerminals.containsKey(name)) {
            nonTerminals.put(name, new Item(name));
        }
        return nonTerminals.get(name);
    }

    private void generateLexer() throws IOException {
        final String LEXER_NAME = grammar_name + "Lexer";
        File file = new File(GEN_DIR, LEXER_NAME + ".java");
        file.getParentFile().mkdirs();
        PrintWriter out = new PrintWriter(file);

        out.println(header);

        out.println(
                "import java.io.IOException;\n" +
                        "import java.io.InputStream;\n" +
                        "import java.text.ParseException;\n" +
                        "\n" +
                        "public class " + LEXER_NAME + " {\n" +
                        "    private InputStream is;\n" +
                        "    private int curChar;\n" +
                        "    private int curPos;\n" +
                        "    private Token curToken;\n" +
                        "    private String curString;\n" +
                        "    \n" +
                        "    public " + LEXER_NAME + "(InputStream is) throws ParseException {\n" +
                        "        this.is = is;\n" +
                        "        curPos = 0;\n" +
                        "        nextChar();\n" +
                        "    }\n" +
                        "\n" +
//                        "    private boolean isBlank(int c) { return c == ' ' || c == '\\r' || c == '\\n' || c == '\\t'; }\n" +
//                        "\n" +
                        "    private void nextChar() throws ParseException {\n" +
                        "        curPos++;\n" +
                        "        try {\n" +
                        "            curChar = is.read();\n" +
                        "        } catch (IOException e) {\n" +
                        "            throw new ParseException(e.getMessage(), curPos);\n" +
                        "        }\n" +
                        "    }\n" +
                        "\n" +
                        "    public Token curToken() { return curToken; }\n" +
                        "\n" +
                        "    public int curPos() { return curPos; }\n" +
                        "\n" +
                        "    public String curString() { return curString; }\n" +
                        "\n" +
                        "    public void nextToken() throws ParseException {\n" +
                        "        curString = \"\";\n" +
//                        "        while (isBlank(curChar)) {\n" +
//                        "            nextChar();\n" +
//                        "        }\n" +
                        "        if (curChar == -1) {\n" +
                        "            curToken = Token.EOF;\n" +
                        "            return;\n" +
                        "        }\n"
        );
        boolean first = true;
        for (String curStringTerminal : terminals.keySet()) {
            for (Production productionString : terminals.get(curStringTerminal).getProductionList()) {
                out.println(String.format(
                        (first ? "        if" : "        else if") +
                                " (\"%1$s\".startsWith(String.valueOf((char) curChar))) {\n" +
                                "            curToken = Token.%2$s;\n" +
                                "            while(curString.length() < \"%1$s\".length()) {\n" +
                                "                 curString += (char) curChar;\n" +
                                "                 nextChar();\n" +
                                "            }\n" +
                                "            if(!curString.equals(\"%1$s\")) {\n" +
                                "                throw new ParseException(\"Input doesn't match: expected \" + \"%1$s\" + \", got \" + curString, curPos);\n" +
                                "            }\n" +
                                "        }",
                        productionString.get(0), curStringTerminal.toUpperCase()
                ));
                first = false;
            }
        }
        out.println("    }\n}");
        out.close();
    }

    private void generateParser() throws IOException {
        final String PARSER_NAME = grammar_name + "Parser";
        final String LEXER_NAME = grammar_name + "Lexer";
        File file = new File(GEN_DIR, PARSER_NAME + ".java");
        file.getParentFile().mkdirs();
        PrintWriter out = new PrintWriter(file);

        out.println(header);

        out.print(
                "import java.io.InputStream;\n" +
                        "import java.text.ParseException;\n" +
                        "import java.util.ArrayList;\n" +
                        "import java.util.List;\n");


        out.print(
                "\n" +
                        "public class " + PARSER_NAME + " {\n" +
                        "    private " + LEXER_NAME + " lex;\n");

        out.println(members);

        out.println(String.format(
                "\n" +
                        "    public %s parse(InputStream is) throws ParseException {\n" +
                        "        lex = new " + LEXER_NAME + "(is);\n" +
                        "        lex.nextToken();\n" +
                        "        %s" + start.getName() + "();\n" +
                        "    }\n",
                start.getRetType(),
                start.getRetType().equals("void") ? "" : "return "
        ));

        for (String nonTerm : nonTerminals.keySet()) {
            out.print(String.format(
                    "    private %s " + nonTerm + "() throws ParseException {\n" +
                            "        switch (lex.curToken()) {\n",
                    getNonTerm(nonTerm).getRetType()
            ));

            Set<String> set = new HashSet<String>(first.get(nonTerm));
            if(set.contains(EPS))
                set.addAll(follow.get(nonTerm));
            set.remove(EPS);

            for (String term : set) {
                out.print(
                        "            case " + term + ":\n" +
                                "            {\n"
                );
                boolean ret = false;
                int suitableProds = 0;
                Set<String> rules = new HashSet<>();
                for (Production prod : nonTerminals.get(nonTerm).getProductionList()) {
                    if (suitableProds == 0 && prod.get(0).equals(EPS)) {
                        if(!prod.getJavaCode().isEmpty()) {
                            out.println(prod.getJavaCode());
                            ret = true;
                        }
                    } else if (first.get(prod.get(0)).contains(term)) {
                        for (String i : prod.getItems()) {
                            if(!rules.contains(i)) {
                                if (nonTerminals.containsKey(i) && !getNonTerm(i).getRetType().equals("void")) {
                                    out.print(String.format(
                                            "                List<%1$s> " + i + " = new ArrayList<%1$s>();\n",
                                            getNonTerm(i).getRetType()
                                    ));
                                    rules.add(i);
                                } else if (terminals.containsKey(i)) {
                                    out.print("                List<String> " + i + " = new ArrayList<String>();\n");
                                    rules.add(i);
                                }
                            }
                        }

                        suitableProds++;
                        for (String i : prod.getItems()) {
                            if (terminals.containsKey(i)) {
                                out.print(String.format(
                                        "                if (lex.curToken().toString().equals(\"%1$s\")) {\n" +
                                                "                    " + i + ".add(lex.curString());\n" +
//                                                "                    children.add(new Tree(lex.curToken().toString(), new Tree(lex.curString())));\n" +
                                                "                } else {\n" +
                                                "                    throw new AssertionError(\"%1$s expected, instead of \" + lex.curToken().toString());\n" +
                                                "                }\n" +
                                                "                lex.nextToken();\n", i
                                ));
                            } else if(nonTerminals.containsKey(i) && !getNonTerm(i).getRetType().equals("void")) {
//                                out.print(String.format("                children.add(new Tree(\"%1$s\", %1$s()));\n", i));
                                out.print(String.format("                " + i + ".add(%s());\n", i));
                            } else {
                                out.print(String.format("                %s();\n", i));
                            }
                        }
                        if(!prod.getJavaCode().isEmpty()) {
                            out.println("\t\t\t\t" + prod.getJavaCode());
                            ret = true;
                        }
                    }
                }

                if (suitableProds > 1) {
                    throw new AssertionError(String.format("Grammar is not LL1, bad rule: %s", nonTerm));
                } else {
                    out.print(
                            (ret ? "" : "                return;\n") +
                                    "            }\n");
                }

//                out.print(String.format(
//                        "                return new Tree(\"%s\", children.toArray(new Tree[children.size()]));\n" +
//                                "            }\n", nonTerm));
            }

            out.print(
                    "            default:\n" +
                            "                throw new AssertionError();\n" +
                            "        }\n" +
                            "    }\n\n"
            );
        }

        out.print("}\n");
        out.close();
    }

    private void generateTokens() throws IOException {
        File file = new File(GEN_DIR, "Token.java");
        file.getParentFile().mkdirs();
        PrintWriter out = new PrintWriter(file);

        out.println(header);

        out.print("public enum Token {\n    ");
        List<String> tokenNames = new ArrayList<String>(terminals.keySet());
        for (int i = 0; i < tokenNames.size(); i++) {
            out.print(tokenNames.get(i) + (i != tokenNames.size() - 1 ? ", " : ""));
        }
        out.println("\n}");
        out.close();
    }

    private void computeFirst() {
        for (String name : terminals.keySet()) {
            HashSet<String> set = new HashSet<String>();
            set.add(name);
            first.put(name, set);
        }
        for (String name : nonTerminals.keySet()) {
            first.put(name, new HashSet<String>());
            for (Production production : nonTerminals.get(name).getProductionList()) {
                if (production.get(0).equals(EPS)) {
                    first.get(name).add(EPS);
                }
            }
        }

        boolean changed;
        do {
            changed = false;
            for (String name : nonTerminals.keySet()) {
                for (Production production : nonTerminals.get(name).getProductionList()) {
                    for (int i = 0; i < production.size(); i++) {
                        if (first.get(production.get(i)).contains(EPS)) {
                            for (String cur : first.get(production.get(i))) {
                                if (!first.get(name).contains(cur)) {
                                    first.get(name).add(cur);
                                    changed = true;
                                }
                            }
                            if (i == production.size() - 1) {
                                if (!first.get(name).contains(EPS)) {
                                    first.get(name).add(EPS);
                                    changed = true;
                                }
                            }
                        } else {
                            for (String cur : first.get(production.get(i))) {
                                if (!first.get(name).contains(cur)) {
                                    first.get(name).add(cur);
                                    changed = true;
                                }
                            }
                            break;
                        }
                    }
                }
            }
        } while (changed);
    }

    private void computeFollow() {
        for (String name : nonTerminals.keySet()) {
            follow.put(name, new HashSet<String>());
        }

        follow.get(start.getName()).add(EOF);

        boolean changed;
        do {
            changed = false;
            for (String name : nonTerminals.keySet()) {
                for (Production production : nonTerminals.get(name).getProductionList()) {
                    for (int i = 0; i < production.size() - 1; i++) {
                        if (nonTerminals.containsKey(production.get(i))) {
                            for (String cur : first.get(production.get(i + 1))) {
                                if (!cur.equals(EPS) && !follow.get(production.get(i)).contains(cur)) {
                                    follow.get(production.get(i)).add(cur);
                                    changed = true;
                                }
                            }
                        }
                    }
                    int i = production.size() - 1;
                    if (nonTerminals.containsKey(production.get(i))) {
                        for (String cur : follow.get(name)) {
                            if (!cur.equals(EPS) && !follow.get(production.get(i)).contains(cur)) {
                                follow.get(production.get(i)).add(cur);
                                changed = true;
                            }
                        }
                    }
                    if(production.size() > 1 && first.get(production.get(production.size() - 1)).contains(EPS)) {
                        i = production.size() - 2;
                        if (nonTerminals.containsKey(production.get(i))) {
                            for (String cur : follow.get(production.get(i + 1))) {
                                if (!cur.equals(EPS) && !follow.get(production.get(i)).contains(cur)) {
                                    follow.get(production.get(i)).add(cur);
                                    changed = true;
                                }
                            }
                        }
                    }
                }
            }
        } while (changed);
    }

    public void generateFiles() throws IOException {
        generateTokens();
        generateLexer();
        computeFirst();
        computeFollow();

        if (DEBUG) {
            System.out.println("=====================================");
            System.out.println("first:");
            printMap(first);
            System.out.println("=====================================");
            System.out.println("follow:");
            printMap(follow);
        }

        generateParser();
    }
}
