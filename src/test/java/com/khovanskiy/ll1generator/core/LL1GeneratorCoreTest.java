package com.khovanskiy.ll1generator.core;

import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

public class LL1GeneratorCoreTest {

    @Test
    public void testRegexpGrammar() throws Exception {
        InputStream is = new ByteArrayInputStream("a+".getBytes());
        /*MathParser parser = new MathParser();
        System.out.println("Answer = " + parser.parse(is));*/

        RegexpParser parser = new RegexpParser();
        Tree tree = parser.parse(is, "Test grammar");

        TreeVisualizer visualizer = new TreeVisualizer(tree, "output.html");
        visualizer.render();/**/
        is.close();
    }
}
