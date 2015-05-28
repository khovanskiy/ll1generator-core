package com.khovanskiy.ll1generator.core;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author Victor Khovanskiy
 */
public class Node {
    private String name;
    private String returnType = "void";
    private List<Production> productionList = new ArrayList<>();

    public Node(String name) {
        this.name = name;
    }

    public Set<Map.Entry<String, String>> getAttrs() {
        return attrs.entrySet();
    }

    public void setAttrs(String name, String type) {
        attrs.put(name, type);
    }

    public String getDeclAttrs() {
        StringBuilder builder = new StringBuilder();
        Iterator<Map.Entry<String, String>> iterator = getAttrs().iterator();
        while (iterator.hasNext()) {
            Map.Entry<String, String> entry = iterator.next();
            builder.append(entry.getValue()).append(" ").append(entry.getKey());
            if (iterator.hasNext()) {
                builder.append(", ");
            }
        }
        return builder.toString();
    }

    public String getCallAttrs() {
        StringBuilder builder = new StringBuilder();
        Iterator<Map.Entry<String, String>> iterator = getAttrs().iterator();
        while (iterator.hasNext()) {
            Map.Entry<String, String> entry = iterator.next();
            builder.append(entry.getKey());
            if (iterator.hasNext()) {
                builder.append(", ");
            }
        }
        return builder.toString();
    }

    private Map<String, String> attrs = new HashMap<>();

    @Override
    public boolean equals(Object o) {
        return (getClass() == o.getClass() && name.equals(((Node) o).name));
    }

    public String getName() {
        return name;
    }

    public void setReturnType(String s) {
        this.returnType = s;
    }

    public String getReturnType() {
        return this.returnType;
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
