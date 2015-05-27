package com.khovanskiy.ll1generator.core;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * @author Victor Khovanskiy
 */
public class Tree {

    private String node;
    private List<Tree> children = Collections.emptyList();
    private int count;
    private boolean counted;

    public Tree(String node, Tree... children) {
        this.node = node;
        this.children = Arrays.asList(children);
    }

    public int count() {
        if (!counted) {
            count += children.size();
            for (Tree tree : children) {
                count += tree.count();
            }
            counted = true;
        }
        return count;
    }

    public Tree(String node) {
        this.node = node;
    }

    public List<Tree> getChildren() {
        return children;
    }

    public String getNode() {
        return node;
    }

    @Override
    public String toString() {
        if (children == null) {
            return "" + node + "";
        }
        return node + ":" + Arrays.toString(children.toArray())+ "";
    }
}
