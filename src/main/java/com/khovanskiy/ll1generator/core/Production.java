package com.khovanskiy.ll1generator.core;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Victor Khovanskiy
 */
public class Production {
    private List<String> items = new ArrayList<String>();
    private String javaCode = "";

    public void add(String item) {
        items.add(item);
    }

    public String getJavaCode() {
        return javaCode;
    }

    public void setJavaCode(String code) { javaCode += code; }

    public int size() {
        return items.size();
    }

    public String get(int i) {
        return items.get(i);
    }

    public List<String> getItems() {
        return items;
    }

    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        for (String cur : items) {
            stringBuilder.append(cur).append(" ");
        }
        stringBuilder.delete(stringBuilder.length() - 1, stringBuilder.length());
        return stringBuilder.toString();
    }
}
