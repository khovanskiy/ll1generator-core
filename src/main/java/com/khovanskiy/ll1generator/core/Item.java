package com.khovanskiy.ll1generator.core;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Victor Khovanskiy
 */
public class Item {
    private String name;
    private String returnType = "void";
    private List<Production> productionList = new ArrayList<>();

    public Item(String name) {
        this.name = name;
    }

    @Override
    public boolean equals(Object o) {
        return (getClass() == o.getClass() && name.equals(((Item) o).name));
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
