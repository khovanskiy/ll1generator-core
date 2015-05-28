grammar Grammar;

@header {
package com.khovanskiy.ll1generator.core;
}

gram : header? members? rule1* ;

header : '@header' JAVA_CODE # headerLabel ;

members : '@members' JAVA_CODE # membersLabel ;

rule1 : NON_TERM_NAME inherited? ('returns' synthesized)? ':' nonterminalProduction ('|' nonterminalProduction)* ';' # nonTerminalLabel
      | TERM_NAME ':' terminalProduction ('|' terminalProduction)* ';' # terminalLabel ;

inherited : JAVA_CODE ;

synthesized : NON_TERM_NAME | TERM_NAME | MIXED_CASE ;

nonterminalVariant : NON_TERM_NAME | TERM_NAME ;

nonterminalProduction : nonterminalVariant* JAVA_CODE? ;
terminalProduction : STRING+ ;

NON_TERM_NAME : [a-z] [a-z_0-9]* ;
TERM_NAME : [A-Z] [A-Z_0-9]* ;
MIXED_CASE : [A-Za-z] [a-z_A-A0-9]* ;
JAVA_CODE : '{' (~[{}]+ JAVA_CODE?)* '}' ;
STRING : '\'' (~'\'' | '\\\'')* '\'' ;

WS : [ \t\r\n]+ -> skip ;
