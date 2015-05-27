package com.khovanskiy.ll1generator.core;

import java.io.File;
import java.io.IOException;

public class LL1GeneratorCoreMain {


    public static void main(String[] args) {
        Generator generator = new Generator(true);
        try {
            generator.read(new File("Regexp"), "regexp");
            generator.generateFiles();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}