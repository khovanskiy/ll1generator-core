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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

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
                Item currentNode = getNonTerm(name);

                if (ctx.synthesized() != null) {
                    if (ctx.synthesized().NON_TERM_NAME() != null) {
                        currentNode.setReturnType(ctx.synthesized().NON_TERM_NAME().getText());
                    } else if (ctx.synthesized().TERM_NAME() != null) {
                        currentNode.setReturnType(ctx.synthesized().TERM_NAME().getText());
                    } else {
                        currentNode.setReturnType(ctx.synthesized().MIXED_CASE().getText());
                    }
                }

                if (ctx.inherited() != null) {

                }

                for (GrammarParser.NonterminalProductionContext nonterminalContext : ctx.nonterminalProduction()) {
                    Production production = new Production();

                    if (nonterminalContext.nonterminalVariant().isEmpty()) {
                        production.add(getTerm(EPS).getName());
                    }

                    for (GrammarParser.NonterminalVariantContext variantContext : nonterminalContext
                            .nonterminalVariant()) {
                        if (variantContext.NON_TERM_NAME() != null) {
                            production.add(variantContext.NON_TERM_NAME().getText());
                        } else {
                            production.add(variantContext.TERM_NAME().getText());
                        }
                    }

                    if (nonterminalContext.JAVA_CODE() != null) {
                        production.setJavaCode(refactor(nonterminalContext.JAVA_CODE().getText()));
                    }

                    currentNode.add(production);
                }
            }

            @Override
            public void enterTerminalLabel(@NotNull GrammarParser.TerminalLabelContext ctx) {
                String name = ctx.TERM_NAME().getText();
                Item curNode = getTerm(name);

                for (GrammarParser.TerminalProductionContext terminalContext : ctx.terminalProduction()) {
                    Production production = new Production();
                    String s = "";
                    for (TerminalNode term : terminalContext.STRING()) {
                        s += term.getText().substring(1);
                        s = s.substring(0, s.length() - 1);
                    }
                    production.add(s);
                    curNode.add(production);
                }
            }

            @Override
            public void enterMembersLabel(@NotNull GrammarParser.MembersLabelContext ctx) {
                if (ctx.JAVA_CODE() != null) {
                    members = ctx.JAVA_CODE().getText().substring(1);
                    members = members.substring(0, members.length() - 1);
                }
            }

            @Override
            public void enterHeaderLabel(@NotNull GrammarParser.HeaderLabelContext ctx) {
                if (ctx.JAVA_CODE() != null) {
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

        out.println("\nimport java.io.IOException;");
        out.println("import java.io.InputStream;");
        out.println("import java.text.ParseException;\n");

        out.println("public class " + LEXER_NAME + " {");
        out.println("\tprivate InputStream is;");
        out.println("\tprivate int curChar;");
        out.println("\tprivate int curPos;");
        out.println("\tprivate Token curToken;");
        out.println("\tprivate String curString;\n");
        out.println("\tpublic " + LEXER_NAME + "(InputStream is) throws ParseException {");
        out.println("\t\tthis.is = is;");
        out.println("\t\tcurPos = 0;");
        out.println("\t\tnextChar();");
        out.println("\t}\n");

        out.println(
                "\tprivate boolean isBlank(int c) { return c == ' ' || c == '\\r' || c == '\\n' || c == '\\t'; }\n");

        out.println("\tprivate void nextChar() throws ParseException {");
        out.println("\t\tcurPos++;");
        out.println("\t\ttry {");
        out.println("\t\t\tcurChar = is.read();");
        out.println("\t\t} catch (IOException e) {");
        out.println("\t\t\tthrow new ParseException(e.getMessage(), curPos);");
        out.println("\t\t}");
        out.println("\t}\n");

        out.println("\tpublic Token curToken() {\n\t\treturn curToken;\n\t}\n");

        out.println("\tpublic int curPos() {\n\t\treturn curPos;\n\t}\n");

        out.println("\tpublic String curString() {\n\t\treturn curString;\n\t}\n");

        out.println("\tpublic void nextToken() throws ParseException {");
        out.println("\t\tcurString = \"\";\n");

        /*out.println("\t\twhile (isBlank(curChar)) {");
        out.println("\t\t\tnextChar();");
        out.println("\t\t}");*/

        out.println("\t\tif (curChar == -1) {");
        out.println("\t\t\tcurToken = Token.EOF;");
        out.println("\t\t\treturn;");
        out.println("\t\t}");

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

        out.println("\nimport java.io.InputStream;");
        out.println("import java.text.ParseException;");
        out.println("import java.util.ArrayList;");
        out.println("import java.util.List;\n");

        out.println("\npublic class " + PARSER_NAME + " {");
        out.println("\n\nprivate " + LEXER_NAME + " lex;\n");

        out.println(prefix("\t", refactor(members)));

        out.println("\n\tpublic " + start.getReturnType() + " parse(InputStream is) throws ParseException {");
        out.println("\t\tlex = new " + LEXER_NAME + "(is);");
        out.println("\t\tlex.nextToken();");
        out.println("\t\t" + (start.getReturnType().equals("void") ? "" : "return ") + start.getName() + "();");
        out.println("\t}\n");

        for (String nonTerm : nonTerminals.keySet()) {
            out.println("\tprivate " + getNonTerm(nonTerm).getReturnType() + " " + nonTerm + "() throws ParseException {");
            out.println("\t\tswitch (lex.curToken()) {");

            Set<String> set = new HashSet<>(first.get(nonTerm));
            if (set.contains(EPS)) {
                set.addAll(follow.get(nonTerm));
            }
            set.remove(EPS);

            for (String term : set) {
                out.println("\t\t\tcase " + term + ": {");

                boolean ret = false;
                int suitableProds = 0;
                Set<String> rules = new HashSet<>();
                for (Production prod : nonTerminals.get(nonTerm).getProductionList()) {
                    if (suitableProds == 0 && prod.get(0).equals(EPS)) {
                        if (!prod.getJavaCode().isEmpty()) {
                            out.println(prefix("\t\t\t\t", prod.getJavaCode()));
                            ret = true;
                        }
                    } else if (first.get(prod.get(0)).contains(term)) {
                        for (String i : prod.getItems()) {
                            if (!rules.contains(i)) {
                                if (nonTerminals.containsKey(i) && !getNonTerm(i).getReturnType().equals("void")) {
                                    out.println("\t\t\t\tList<" + getNonTerm(i).getReturnType() + "> " + i + " = new ArrayList<>();");
                                    rules.add(i);
                                } else if (terminals.containsKey(i)) {
                                    out.print("\t\t\t\tList<String> " + i + " = new ArrayList<>();\n");
                                    rules.add(i);
                                }
                            }
                        }

                        suitableProds++;
                        for (String i : prod.getItems()) {
                            if (terminals.containsKey(i)) {
                                out.println("\t\t\t\tif (lex.curToken().toString().equals(\"" + i + "\")) {");
                                out.println("\t\t\t\t\t" + i + ".add(lex.curString());");
//                              //out.println("      "                    children.add(new Tree(lex.curToken().toString(), new Tree(lex.curString())));\n" +
                                out.println("\t\t\t\t} else {");
                                out.println("\t\t\t\t\tthrow new AssertionError(\"" + i + " expected, instead of \" + lex.curToken().toString());");
                                out.println("\t\t\t\t}");
                                out.println("\t\t\t\tlex.nextToken();");
                            } else if (nonTerminals.containsKey(i) && !getNonTerm(i).getReturnType().equals("void")) {
//                              //out.print(String.format("                children.add(new Tree(\"%1$s\", %1$s()));\n", i));
                                out.print(String.format("\t\t\t\t" + i + ".add(" + i + "());\n", i));
                            } else {
                                out.print(String.format("\t\t\t\t%s();\n", i));
                            }
                        }
                        if (!prod.getJavaCode().isEmpty()) {
                            out.println(prefix("\t\t\t\t", prod.getJavaCode()));
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

    private String refactor(String content) {
        content = content.trim();
        if (content.charAt(0) == '{' && content.charAt(content.length() - 1) == '}') {
            return content.substring(1, content.length() - 1).trim();
        }
        return content;
    }

    private String prefix(String prefix, String content) {
        StringBuilder builder = new StringBuilder();

        for (String s : content.split("\\n")) {
            builder.append(prefix);
            builder.append(s);
        }

        return builder.toString();
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
            follow.put(name, new HashSet<>());
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
                    if (production.size() > 1 && first.get(production.get(production.size() - 1)).contains(EPS)) {
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
