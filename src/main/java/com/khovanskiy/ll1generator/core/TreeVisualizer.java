package com.khovanskiy.ll1generator.core;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Writer;

public class TreeVisualizer {

    private Tree root;
    private String filename;

    public TreeVisualizer(Tree tree, String filename) {
        this.root = tree;
        this.filename = filename;
    }

    public void render() throws IOException {
        PrintWriter output = new PrintWriter(new FileOutputStream(filename));
        {
            output.write("<html>\n<head>\n");
            output.write("<script type=\"text/javascript\" src=\"core.js\"></script>\n");
            output.write("</head>\n<body>\n");
            /*output.write("<table border='1'>");
            List<Tree> layer = new ArrayList<Tree>();
            layer.add(root);
            while (!layer.isEmpty()) {
                List<Tree> next = new ArrayList<Tree>();
                output.write("<tr>");
                for (Tree tree : layer) {
                    output.write("<td colspan=\"" + tree.count() + "\">");
                    output.write(tree.getNode());
                    output.write("</td>");
                    next.addAll(tree.getChildren());
                }
                output.write("</tr>");
                layer = next;
            }
            output.write("</table>");*/
            output.write("<div id='graph' style='width: 100%; height: 100%'>\n");
            render(root, output, 0);
            output.write("</div>\n");
            output.write("</body>\n</html>");
        }
        output.close();
    }

    //private String[] colors = {"#A8FF2F", "#47F9FF", "#D40EB5", "#A734BD", "#0D0DFF"};
    //private String[] colors = {"#FFE3F2", "#FFCBE3", "#F26680", "#C54856", "#404458"};
    private String[] colors = {"#F15A5A", "#F0C419", "#4EBA6F", "#2D95BF", "#955BA5"};
    private String terminal = "#fff";

    private void render(Tree tree, Writer writer, int k) throws IOException {
        String color;
        if (tree.getChildren().size() == 0 && !tree.getNode().equals("eps")) {
            color = terminal;
        } else {
            color = colors[k % colors.length];
        }
        writer.write("<table style='border:1px solid #333; width: 100%; height: 100%; background:" + color + "'>\n");
        writer.write("<tr>\n");
        writer.write("<th style='text-align: center; vertical-align:middle;' colspan='" + tree.getChildren().size() + "'>" + tree.getNode() + "</th></tr>\n");

        if (tree.getChildren().size() > 0) {
            writer.write("<tr>\n");
            for (Tree child : tree.getChildren()) {
                writer.write("<td style='height:100%;'>\n");
                render(child, writer, k + 1);
                writer.write("</td>\n");
            }
            writer.write("</tr>\n");
        }
        writer.write("</table>\n");
    }
}
