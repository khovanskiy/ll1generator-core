package com.khovanskiy.ll1generator.core;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * @author Victor Khovanskiy
 */
public class Node {
    private String name;
    private String returnType = "void";
    private List<Production> productionList = new ArrayList<>();

    private List<String> callAttrs = new ArrayList<>();
    private final Map<String, String> declAttrs = new HashMap<>();

    public Node(String name) {
        this.name = name;
    }

    public void putDeclAttrs(String name, String type) {
        declAttrs.put(name, type);
    }

    String getDeclAttrs(boolean withTypes) {
        StringBuilder builder = new StringBuilder();
        Iterator<Map.Entry<String, String>> iterator = declAttrs.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<String, String> entry = iterator.next();
            if (withTypes) {
                builder.append(entry.getValue()).append(" ");
            }
            builder.append(entry.getKey());
            if (iterator.hasNext()) {
                builder.append(", ");
            }
        }
        return builder.toString();
    }

    public String getCallAttrs() {
        StringBuilder builder = new StringBuilder();
        Iterator<String> iterator = callAttrs.iterator();
        while (iterator.hasNext()) {
            String entry = iterator.next();
            builder.append(entry);
            if (iterator.hasNext()) {
                builder.append(", ");
            }
        }
        return builder.toString();
    }

    public void setCallAttrs(List<String> callAttrs) {
        this.callAttrs = callAttrs;
    }

    @Override
    public boolean equals(Object o) {
        return (getClass() == o.getClass() && name.equals(((Node) o).name));
    }

    public String getName() {
        return name;
    }

    public String getReturnType() {
        return this.returnType;
    }

    public void setReturnType(String s) {
        this.returnType = s;
    }

    public void add(Production production) {
        productionList.add(production);
    }

    public List<Production> getProductionList() {
        return productionList;
    }

    public Production get(int i) {
        return productionList.get(i);
    }

    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(name).append(" -> ");
        for (Production cur : productionList) {
            stringBuilder.append(cur.toString()).append(" | ");
        }
        stringBuilder.delete(stringBuilder.length() - 3, stringBuilder.length());
        return stringBuilder.toString();
    }
}
