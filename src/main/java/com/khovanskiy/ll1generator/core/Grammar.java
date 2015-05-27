package com.khovanskiy.ll1generator.core;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class Grammar {

    public static class Node {
        public String getName() {
            return name;
        }

        private String name;
        public Node(String name) {

        }
    }

    public static class Terminal extends Node {
        private Terminal(String name) {
            super(name);
        }

        public void addProduction(String text) {

        }
    }

    public static class NonTerminal extends Node {
        public String getReturnType() {
            return returnType;
        }

        public void setReturnType(String returnType) {
            this.returnType = returnType;
        }

        private String returnType = "void";
        private List<Production> productions = new ArrayList<>();
        private NonTerminal(String name) {
            super(name);
        }

        public void addProduction(Production production) {
            productions.add(production);
        }

        public List<Production> getProductionList() {
            return productions;
        }
    }

    public static class Production {

        private List<Node> nodes = new ArrayList<>();

        public Production() {

        }

        public String getTranslatingSymbol() {
            return translatingSymbol;
        }

        public void setTranslatingSymbol(String translatingSymbol) {
            this.translatingSymbol = translatingSymbol;
        }

        private String translatingSymbol;

        public void addNode(Node node) {
            this.nodes.add(node);
        }
    }

    public final static String EPS_NODE_NAME = "EPS";
    public final static String EPSILON = "";
    public final static String EOF = "$";

    public static class Rule {
        private String left;
        private String right;

        public Rule(String left, String right) {
            this.left = left;
            this.right = right;
        }
    }

    private Map<String, Terminal> terminals = new HashMap<>();
    private Map<String, NonTerminal> nonterminals = new HashMap<>();
    private List<Rule> rules = new ArrayList<>();
    private Map<Node, Set<Terminal>> first = new HashMap<>();
    private Map<String, Set<String>> follow = new HashMap<>();
    private String start;

    public Grammar() {
        getTerminal(EPS_NODE_NAME).addProduction("");
    }

    /*public void add(String left, String right) {
        for (int i = 0; i < left.length(); ++i) {
            char c = left.charAt(i);
            if (isNonTerminal(c)) {
                nonterminals.add(c + "");
            }
        }
        for (int i = 0; i < right.length(); ++i) {
            char c = right.charAt(i);
            if (isNonTerminal(c)) {
                nonterminals.add(c + "");
            } else {
                terminals.add(c + "");
            }
        }
        rules.add(new Rule(left, right));
    }*/

    public NonTerminal getNonTerminal(String name) {
        NonTerminal nonTerminal = nonterminals.get(name);
        if (nonTerminal == null) {
            nonTerminal = new NonTerminal(name);
            nonterminals.put(name, nonTerminal);
        }
        return nonTerminal;
    }

    public Terminal getTerminal(String name) {
        Terminal terminal = terminals.get(name);
        if (terminal == null) {
            terminal = new Terminal(name);
            terminals.put(name, terminal);
        }
        return terminal;
    }

    public boolean isNonTerminal(char c) {
        return c >= 'A' && c <= 'Z';
    }

    public boolean isNonTerminal(String c) {
        if (c.length() != 1) {
            return false;
        }
        return isNonTerminal(c.charAt(0));
    }

    public boolean isTerminal(char c) {
        return !isNonTerminal(c);
    }

    public boolean isTerminal(String c) {
        if (c.length() != 1) {
            return false;
        }
        return isTerminal(c.charAt(0));
    }

    /**
     * Gets FIRST set
     *
     * @return FIRST set
     */
    public Map<Node, Set<Terminal>> getFirstSet() {
        first.clear();
        // 1st part
        for (Map.Entry<String, Terminal> entry : terminals.entrySet()) {
            HashSet<Terminal> initialSet = new HashSet<>();
            initialSet.add(entry.getValue());
            first.put(entry.getValue(), initialSet);
        }
        for (Map.Entry<String, NonTerminal> entry : nonterminals.entrySet()) {
            first.put(entry.getValue(), new HashSet<>());
        }
        // 2nd part
        /*for (Rule rule : rules) {
            if (rule.right.equals(EPSILON)) {
                first.get(rule.left).add(EPSILON);
            }
        }*/
        // 3rd part
        while (true) {
            boolean needToBreak = true;
            for (String name : nonterminals.keySet()) {
                for (Production production : nonterminals.get(name).productions) {
                    for (Node i : production.nodes) {
                        boolean isOk = true;
                        for (Node j : production.nodes){
                            if (!first.get(j).contains(getTerminal(EPS_NODE_NAME))) {
                                isOk = false;
                                break;
                            }
                        }
                        if (isOk) {
                            for (Terminal k : first.get(i)) {
                                if (!first.get(nonterminals.get(name)).contains(k)) {
                                    first.get(nonterminals.get(name)).add(k);
                                    //predictionTable.put(new Pair<NonTermNode, TermNode>(nonTerminals.get(name), cur), production);
                                    needToBreak = false;
                                }
                            }
                        }
                    }

                }
            }
            if (needToBreak) {
                break;
            }
        }
        return first;
    }
/*
    /**
     * Gets FOLLOW set
     *
     * @return FOLLOW set

    public Map<String, Set<String>> getFollowSet() {
        getFirstSet();
        follow.clear();
        for (String nonterm : nonterminals) {
            follow.put(nonterm, new HashSet<String>());
        }
        follow.get(start).add(EOF);
        boolean changed = true;
        while (changed) {
            changed = false;
            for (Rule rule : rules) {
                String name = rule.left;
                String production = rule.right;
                for (int i = 0; i < production.length() - 1; i++) {
                    if (nonterminals.contains(production.charAt(i) + "")) {
                        for (String cur : first.get(production.charAt(i + 1) + "")) {
                            if (!cur.equals(EPSILON) && !follow.get(production.charAt(i) + "").contains(cur)) {
                                follow.get(production.charAt(i) + "").add(cur);
                                changed = true;
                            }
                        }
                    }
                }
                if (production.length() == 0) {
                    continue;
                }
                int i = production.length() - 1;
                if (nonterminals.contains(production.charAt(i) + "")) {
                    for (String cur : follow.get(name)) {
                        if (!cur.equals(EPSILON) && !follow.get(production.charAt(i) + "").contains(cur)) {
                            follow.get(production.charAt(i) + "").add(cur);
                            changed = true;
                        }
                    }
                }
                if (first.get(production.charAt(production.length() - 1) + "").contains(EPSILON)) {
                    i = production.length() - 2;
                    if (nonterminals.contains(production.charAt(i) + "")) {
                        for (String cur : follow.get(production.charAt(i + 1) + "")) {
                            if (!cur.equals(EPSILON) && !follow.get(production.charAt(i) + "").contains(cur)) {
                                follow.get(production.charAt(i) + "").add(cur);
                                changed = true;
                            }
                        }
                    }
                }
            }
        }
        return follow;
    }
    */

    /*public void buildTable() {
        getFollowSet();
        for (Rule rule : rules) {

        }
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (Rule rule : rules) {
            sb.append(rule.left + " -> " + rule.right + "\n");
        }
        return sb.toString();
    }*/
}
