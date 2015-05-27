grammar Grammar;

@header {
package com.khovanskiy.ll1generator.core;
}

program : NEWLINE* header NEWLINE* members NEWLINE* (((lineRule ';' NEWLINE) | NEWLINE)*);

header : '@header' CODE;

members : '@members' CODE;

lineRule  : NON_TERM_NAME declSynth? ':' nonterminalProduction ('|' nonterminalProduction)* #nonterminalRule
        | TERM_NAME ':' terminalProduction ('|' terminalProduction)* #terminalRule;

nonterminalProduction : (nonterminalVariant)* CODE?;
nonterminalVariant : NON_TERM_NAME | TERM_NAME;
terminalProduction : ('\'' terminalVariant '\'')*;
terminalVariant : NON_TERM_NAME | LEFT_PAREN | RIGHT_PAREN;

declSynth : 'returns' '[' type ']';
type : NON_TERM_NAME | TERM_NAME | CODE_NAME;

NEWLINE : '\r'? '\n' ;
NON_TERM_NAME : [_a-z]+ ;
TERM_NAME : [_A-Z]+ ;
CODE_NAME : [_a-zA-Z]+ ;
CODE : '{' (~('}'|'{')+ CODE?)* '}' ;
LEFT_PAREN : '(' ;
RIGHT_PAREN : ')' ;

WS : [ \t]+ -> channel(HIDDEN) ;