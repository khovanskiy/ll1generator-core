package com.khovanskiy.ll1generator.core;

import java.io.IOException;

public class LL1GeneratorCoreMain {
    public static void main(String[] args) throws IOException {
        Generator generator = new Generator(args[0]);
        generator.generate();
    }
}
