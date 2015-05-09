grammar Grammar;

@header {
package com.khovanskiy.ll1generator.core;
}

program : fn fn;

fn : NUMBER;

NUMBER : [0-9]+;