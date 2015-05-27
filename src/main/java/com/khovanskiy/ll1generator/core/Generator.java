package com.khovanskiy.ll1generator.core;

import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.ParseTreeWalker;

import java.io.FileInputStream;
import java.io.IOException;

/**
 * @author Victor Khovanskiy
 */
public class Generator {
    private final static String END_NODE_NAME = "END";

    private String fileName;
    private String header;
    private String members;
    private Grammar grammar = new Grammar();

    public Generator(String fileName) {
        this.fileName = fileName;
    }

    public void generate() throws IOException {
        ANTLRInputStream input = new ANTLRInputStream(new FileInputStream(fileName));
        GrammarLexer lexer = new GrammarLexer(input);
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        GrammarParser parser = new GrammarParser(tokens);
        ParseTree tree = parser.program();

        ParseTreeWalker walker = new ParseTreeWalker();
        GrammarBaseListener listener = new GrammarBaseListener() {

            private String refactorCode(String code) {
                return code.substring(1, code.length() - 1).trim();
            }

            @Override
            public void enterHeader(GrammarParser.HeaderContext ctx) {
                header = refactorCode(ctx.CODE().getText());
                System.out.println("Header: " + header);
            }

            @Override
            public void enterMembers(GrammarParser.MembersContext ctx) {
                members = refactorCode(ctx.CODE().getText());
                System.out.println("Members: " + members);
            }

            @Override
            public void enterNonterminalRule(GrammarParser.NonterminalRuleContext ctx) {

                String name = ctx.NON_TERM_NAME().getText();
                System.out.println(name);
                Grammar.NonTerminal nonTerminal = grammar.getNonTerminal(name);

                // if X returns something otherwise void
                if (ctx.declSynth() != null) {
                    nonTerminal.setReturnType(ctx.declSynth().getText());
                }

                // foreach Yi : (X -> Y1 | Y2 | .. | Yn)
                for (GrammarParser.NonterminalProductionContext context : ctx.nonterminalProduction()) {
                    Grammar.Production production = new Grammar.Production();

                    if (context.CODE() != null) {
                        System.out.println(context.CODE().getText());
                        production.setTranslatingSymbol(refactorCode(context.CODE().getText()));
                    }

                    if (context.nonterminalVariant().isEmpty()) {
                       production.addNode(grammar.getTerminal(Grammar.EPS_NODE_NAME));
                    } else {
                        for (GrammarParser.NonterminalVariantContext variantContext : context.nonterminalVariant()) {
                            if (variantContext.NON_TERM_NAME() != null) {
                                production.addNode(grammar.getNonTerminal(variantContext.NON_TERM_NAME().getText()));
                            } else {
                                production.addNode(grammar.getTerminal(variantContext.TERM_NAME().getText()));
                            }
                        }
                    }

                    nonTerminal.addProduction(production);
                }
            }

            @Override
            public void enterTerminalRule(GrammarParser.TerminalRuleContext ctx) {
                String name = ctx.TERM_NAME().getText();
                System.out.println(name);
                Grammar.Terminal terminal = grammar.getTerminal(name);
                for (GrammarParser.TerminalProductionContext context : ctx.terminalProduction()) {
                    for (GrammarParser.TerminalVariantContext variantContext : context.terminalVariant()) {
                        if (variantContext.NON_TERM_NAME() != null) {
                            terminal.addProduction(variantContext.NON_TERM_NAME().getText());
                        }
                        if (variantContext.LEFT_PAREN() != null) {
                            terminal.addProduction(variantContext.LEFT_PAREN().getText());
                        }
                        if (variantContext.RIGHT_PAREN() != null) {
                            terminal.addProduction(variantContext.RIGHT_PAREN().getText());
                        }
                    }
                }
            }
        };
        walker.walk(listener, tree);


    }
}
